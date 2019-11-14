package am.arssystems.image_manager_backend.service.serviceImpl;

import am.arssystems.image_manager_backend.dto.request.ImageData;
import am.arssystems.image_manager_backend.dto.request.PicNames;
import am.arssystems.image_manager_backend.dto.response.UserData;
import am.arssystems.image_manager_backend.entity.User;
import am.arssystems.image_manager_backend.entity.UserImage;
import am.arssystems.image_manager_backend.repository.UserImageRepository;
import am.arssystems.image_manager_backend.repository.UserRepository;
import am.arssystems.image_manager_backend.service.ImageService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ImageServiceIpml implements ImageService {

    private UserImageRepository userImageRepository;
    private UserRepository userRepository;

    @Value("${count.limit}")
    private int limitCountofImage;

    @Value("${image.folder}")
    private String uploadImagePath;

    @Autowired
    public ImageServiceIpml(UserImageRepository userImageRepository, UserRepository userRepository) {
        this.userImageRepository = userImageRepository;
        this.userRepository = userRepository;

    }

    @Override
    public double getImageFileSize(File image) throws IOException {
        InputStream inputStream = new FileInputStream(image);
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        double picSize = 0;
        int redlen = 0;
        while ((redlen = inputStream.read(buffer, 0, bufferSize)) > -1) {
            picSize += redlen;
        }
        if (inputStream != null) {
            inputStream.close();
        }
        return picSize;
    }

    @Override
    public void setDeletedAtDate(User currentUser, String picName, String actionType) {
        if (actionType.equals("delete"))
            userImageRepository.updateUserImageDeletedAtByUserAndPicName(currentUser, picName);
        if (actionType.equals("remake"))
            userImageRepository.setUserImageDeletedAtNulByUserAndPicName(currentUser, picName);

    }

    @Override
    public List<UserImage> getTwoNextImagesByPictureName(String picName, User user) {
        return userImageRepository.getUserImageByPreviusImageName(picName, user, PageRequest.of(0, 2)).getContent();
    }

    @Override
    public List<UserImage> getTwoPreviousImageByPictureName(String picName, User user) {
        return userImageRepository.getUserImageByNextImageName(picName, user, PageRequest.of(0, 2)).getContent();
    }

    @Override
    public void updateImageStatus(ImageData imageData, User user) {
        if (!imageData.isImageStatus()) {
            userImageRepository.updateImageStatusInBatch(imageData.getPicNames(), user);
        }
    }

    @Override
    public UserData getImagesBeetweenInDate(User currentUser, String fromDate, String toDate, int page, int perPage) {
        UserData result = new UserData();
        List<UserImage> picturesData = new ArrayList<>();
        int totalElementCount = 1;
        if (fromDate == null && toDate == null) {
            result = new UserData();
        } else if (fromDate == null || fromDate.isEmpty()) { // get by toDate
            picturesData = userImageRepository.getByUserAndCreatedAtLessThan(currentUser.getId(), toDate, (page - 1) * perPage, perPage);
            totalElementCount = userImageRepository.countAllByUserIdAndCreatedAtLessThan(currentUser.getId(), toDate);
        } else if (toDate == null || toDate.isEmpty()) { //get by fromDate
            picturesData = userImageRepository.getByUserAndCreatedAtGreaterThan(currentUser.getId(), fromDate, (page - 1) * perPage, perPage);
            totalElementCount = userImageRepository.countAllByUserIdAndCreatedAtGreaterThan(currentUser.getId(), fromDate);
        } else { //get by fromDate and toDate
            picturesData = userImageRepository.getByUserAndCreatedAtGreaterThanAndLessThan(currentUser.getId(), fromDate, toDate, (page - 1) * perPage, perPage);
            totalElementCount = userImageRepository.countAllByUserIdAndCreatedAtGreaterThanAndLessThan(currentUser.getId(), toDate, fromDate);
        }
        int totalPageCount = 1;
        if (totalElementCount % perPage > 0)
            totalPageCount = totalElementCount / perPage + 1;
        else
            totalPageCount = totalElementCount / perPage;
        result.setPicturesData(picturesData);
        result.setTotoalPageCount(totalPageCount);
        result.setFruction(userImageRepository.countAllByUserAndDeletedAtIsNull(currentUser) + "/" + limitCountofImage);

        return result;
    }

    @Override
    public UserData getDeletedImageData(User user, int page, int perPage) {
        List<UserImage> data = userImageRepository.findAllByUserAndCreatedAtIsNotNull(user.getId(), (page - 1) * perPage, perPage);
        int totoalCount = userImageRepository.countAllByUserAndDeletedAtIsNotNull(user);
        System.out.println("totalElementCount " + totoalCount);
        return UserData.builder()
                .totalElementCount(totoalCount)
                .totoalPageCount(totoalCount % perPage > 0 ? totoalCount / perPage + 1 : totoalCount / perPage)
                .picturesData(data)
                .build();
    }

    @Override
    public void recoveerImagesInBatch(User user, ImageData imageData) {
        userImageRepository.recoverImageInBatch(imageData.getPicNames(), user);
    }

    @Override
    public void deleteImages(User user, Collection<String> picNames) {
        for (String picName : picNames) {
            new File(uploadImagePath + user.getId() + "\\" + picName).delete();
        }
        userImageRepository.deleteInBatch(user, picNames);
    }

    @Override
    public UserData getPictureDataByYearAndMonth(int page, User user, String year, String month, int perPage) {
        List<UserImage> picturesData;
        int totalPageCount = 1;
        int totalElementsCount;
        if (month.equals("ALL")) {
            picturesData = userImageRepository.getPicNamesByUserAndByCreatedAt(page, user.getId(), year, perPage);
            totalElementsCount = userImageRepository.countByUserAndCreatedAtLike(user.getId(), year);
        } else {
            picturesData = userImageRepository.getPicNamesByUserAndByCreatedAt(page, user.getId(), year + "-" + month, perPage);
            totalElementsCount = userImageRepository.countByUserAndCreatedAtLike(user.getId(), year + "-" + month);
        }
        totalPageCount = getTotalPagesCountByAllElementsCount(totalElementsCount);


        return UserData.builder()
                .totoalPageCount(totalPageCount)
                .picturesData(picturesData)
                .build();
    }

    @Override
    public byte[] downloadManyImages(User user, List<String> picNames, HttpServletResponse httpServletResponse) {
        String fileName = System.currentTimeMillis() + "pictures.zip";
        FileOutputStream fos;
        byte[] result = null;
        try {
            fos = new FileOutputStream(uploadImagePath + user.getId() + "\\" + fileName);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            for (String srcFile : picNames) {
                File fileToZip = new File(uploadImagePath + user.getId() + "\\" + srcFile);
                FileInputStream fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                fis.close();
            }
            zipOut.close();
            fos.close();
            File zip = new File(uploadImagePath + user.getId() + "\\" + fileName);
            FileInputStream fileInputStream = new FileInputStream(zip);
            result = IOUtils.toByteArray(fileInputStream);
            fileInputStream.close();
            zip.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename="
                + fileName);
        return result;
    }

    @Override
    public byte[] downloadManyImagesTest(List<String> picNames, HttpServletResponse httpServletResponse) {
        String fileName = "C:\\Users\\Maga\\Desktop\\" + "pictures.zip";
        FileOutputStream fos;
        byte[] result = null;
        try {
            fos = new FileOutputStream(fileName);
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            picNames = Arrays.asList("149150580.jpg", "149150580 - Copy.jpg");
            for (String srcFile : picNames) {
                File fileToZip = new File("C:\\Users\\Maga\\Desktop\\" + "\\" + srcFile);
                FileInputStream fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                fis.close();
            }
            zipOut.close();
            fos.close();
            File zip = new File(fileName);
            FileInputStream fileInputStream = new FileInputStream(zip);
            result = IOUtils.toByteArray(fileInputStream);
            fileInputStream.close();
            zip.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        httpServletResponse.setHeader("Content-Disposition", "attachment;filename="
                + fileName);
        return result;
    }


    private int getTotalPagesCountByAllElementsCount(int totalElemnetsCount) {
        int pageCount = 1;
        if (totalElemnetsCount > 50) {
            pageCount = totalElemnetsCount % 50 == 0 ? totalElemnetsCount / 50 : totalElemnetsCount / 50 + 1;
        }
        return pageCount;
    }


}
