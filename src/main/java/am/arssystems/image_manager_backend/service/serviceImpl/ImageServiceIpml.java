package am.arssystems.image_manager_backend.service.serviceImpl;

import am.arssystems.image_manager_backend.entity.User;
import am.arssystems.image_manager_backend.repository.UserImageRepository;
import am.arssystems.image_manager_backend.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ImageServiceIpml implements ImageService {

    private UserImageRepository userImageRepository;

    @Autowired
    public ImageServiceIpml(UserImageRepository userImageRepository){
        this.userImageRepository = userImageRepository;

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
        if(inputStream!=null){
            inputStream.close();
        }
        return picSize;
    }

    @Override
    public void setDeletedAtDate(User currentUser, String picName, String actionType) {
        if(actionType.equals("delete"))
        userImageRepository.updateUserImageDeletedAtByUserAndPicName(currentUser,picName);
        if(actionType.equals("remake"))
            userImageRepository.setUserImageDeletedAtNulByUserAndPicName(currentUser,picName);

    }
}
