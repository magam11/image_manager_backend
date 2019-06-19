package am.arssystems.image_manager_backend.service;

import am.arssystems.image_manager_backend.dto.response.ChangePasswordResponse;
import am.arssystems.image_manager_backend.dto.response.UserData;
import am.arssystems.image_manager_backend.entity.User;

public interface UserService {

    String createRandomKey(int lengthKey);

    ChangePasswordResponse changePassword(User user, String newPassword);

    void saveUser(User user);

    void setUserPasswordRandomActivationKeyAndSendSMS(User user);

    UserData getBaseUserData(User user, int pageIndex);

    int getTotalPageCount(int allCount, int preSize);
}
