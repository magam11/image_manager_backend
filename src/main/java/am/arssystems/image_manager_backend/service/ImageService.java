package am.arssystems.image_manager_backend.service;

import am.arssystems.image_manager_backend.entity.User;

import java.io.File;
import java.io.IOException;

public interface ImageService {

    double getImageFileSize(File image) throws IOException;

    void setDeletedAtDate(User currentUser, String picName,String actionType);

}
