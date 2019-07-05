package am.arssystems.image_manager_backend.service.serviceImpl;

import am.arssystems.image_manager_backend.dto.request.ImageData;
import am.arssystems.image_manager_backend.dto.response.UserData;
import am.arssystems.image_manager_backend.entity.User;
import am.arssystems.image_manager_backend.entity.UserImage;
import am.arssystems.image_manager_backend.repository.UserImageRepository;
import am.arssystems.image_manager_backend.repository.UserRepository;
import am.arssystems.image_manager_backend.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageServiceIpml implements ImageService {

    private UserImageRepository userImageRepository;
    private UserRepository userRepository;

    @Value("${count.limit}")
    private int limitCountofImage;

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
    public UserData getImagesBeetweenInDate(User currentUser, String fromDate, String toDate, int page) {
        UserData result = new UserData();
        List<UserImage> picturesData = new ArrayList<>();
        int totalElementCount = 1;
        if (fromDate == null && toDate == null) {
            result = new UserData();
        } else if (fromDate == null || fromDate.isEmpty()) { // get by toDate
            picturesData = userImageRepository.getByUserAndCreatedAtLessThan(currentUser.getId(), toDate, (page - 1) * 50, 50);
            totalElementCount = userImageRepository.countAllByUserIdAndCreatedAtLessThan(currentUser.getId(), toDate);
        } else if (toDate == null || toDate.isEmpty()) { //get by fromDate
            picturesData = userImageRepository.getByUserAndCreatedAtGreaterThan(currentUser.getId(), fromDate,(page-1)*50,50);
            totalElementCount = userImageRepository.countAllByUserIdAndCreatedAtGreaterThan(currentUser.getId(), fromDate);
        } else { //get by fromDate and toDate
            picturesData = userImageRepository.getByUserAndCreatedAtGreaterThanAndLessThan(currentUser.getId(), fromDate,toDate,(page-1)*50,50);
            totalElementCount = userImageRepository.countAllByUserIdAndCreatedAtGreaterThanAndLessThan(currentUser.getId(),toDate, fromDate);
        }
        int totalPageCount = 1;
        if(totalElementCount%50>0)
            totalPageCount = totalElementCount/50+1;
        else
            totalPageCount=totalElementCount/50;
        result.setPicturesData(picturesData);
        result.setTotoalPageCount(totalPageCount);
        result.setFruction(userImageRepository.countAllByUserAndDeletedAtIsNull(currentUser) + "/" + limitCountofImage);

        return result;
    }

    @Override
    public UserData getDeletedImageData(User user, int page) {
        List<UserImage> data = userImageRepository.findAllByUserAndCreatedAtIsNotNull(user.getId(), (page-1)*50,50);
        int totoalCount = userImageRepository.countAllByUserAndDeletedAtIsNotNull(user);
        return UserData.builder()
                .totalElementCount(totoalCount)
                .totoalPageCount(totoalCount%50>0?totoalCount/50+1:totoalCount/50)
                .picturesData(data)
                .build();
    }
}
