package am.arssystems.image_manager_backend.service;

import am.arssystems.image_manager_backend.dto.request.ImageData;
import am.arssystems.image_manager_backend.dto.request.PicNames;
import am.arssystems.image_manager_backend.dto.response.UserData;
import am.arssystems.image_manager_backend.entity.User;
import am.arssystems.image_manager_backend.entity.UserImage;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface ImageService {

    double getImageFileSize(File image) throws IOException;

    void setDeletedAtDate(User currentUser, String picName,String actionType);

    List<UserImage> getTwoNextImagesByPictureName(String picName,User user);

    List<UserImage> getTwoPreviousImageByPictureName(String picname,User user);

    void updateImageStatus(ImageData imageData, User user);

    UserData getImagesBeetweenInDate(User currentUser,String fromDate, String toDate, int page,int perPage);

    UserData getDeletedImageData(User user, int page, int perPage);

    void recoveerImagesInBatch(User user, ImageData imageData);

    void deleteImages(User user, Collection<String> picNames);

    UserData getPictureDataByYearAndMonth(int page,User user, String year, String month,int perPage);

    byte[] downloadManyImages(User user, List<String> picNames, HttpServletResponse httpServletResponse);

    byte[] downloadManyImagesTest(List<String> picNames, HttpServletResponse httpServletResponse);
}
