package am.arssystems.image_manager_backend.service.serviceImpl;

import am.arssystems.image_manager_backend.dto.response.ChangePasswordResponse;
import am.arssystems.image_manager_backend.dto.response.UserData;
import am.arssystems.image_manager_backend.entity.User;
import am.arssystems.image_manager_backend.entity.UserImage;
import am.arssystems.image_manager_backend.repository.UserImageRepository;
import am.arssystems.image_manager_backend.repository.UserRepository;
import am.arssystems.image_manager_backend.security.JwtTokenUtil;
import am.arssystems.image_manager_backend.service.UserService;
import am.arssystems.image_manager_backend.twilio.TwilioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private UserImageRepository userImageRepository;
    private TwilioUtil twilioUtil;
    private JwtTokenUtil jwtTokenUtil;
    @Value("${count.limit}")
    private int limitCountofImage;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TwilioUtil twilioUtil,
                           JwtTokenUtil jwtTokenUtil, UserImageRepository userImageRepository) {
        this.userImageRepository = userImageRepository;
        this.userRepository = userRepository;
        this.twilioUtil = twilioUtil;
    }

    @Override
    public void saveUser(User user) {
        String registerActivationKey = createRandomKey(5);
        twilioUtil.sendSMS(user.getPhoneNumber(), registerActivationKey);
        user.setRegisterActivationKey(registerActivationKey);
        userRepository.save(user);

    }

    @Override
    public String createRandomKey(int lengthKey) {
        final Random random = new Random();
        final String CHARS = "0123456789";
        StringBuilder password = new StringBuilder(lengthKey);
        for (int i = 0; i < lengthKey; i++) {
            password.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return password.toString();
    }


    @Override
    public ChangePasswordResponse changePassword(User user, String newPassword) {
        String newToken = jwtTokenUtil.generateTokenPassAndId(user.getPhoneNumber(), newPassword, user.getId());
        userRepository.changeUserPasswordByUserId(newPassword, user.getId());
        return ChangePasswordResponse.builder()
                .success(true)
                .message("Password changed")
                .newToken(newToken)
                .build();
    }

    @Override
    public void setUserPasswordRandomActivationKeyAndSendSMS(User user) {
        String randomKeyForPassword = createRandomKey(6);
        user.setPassword(jwtTokenUtil.generateToken(randomKeyForPassword));
        userRepository.save(user);
        twilioUtil.sendSMS(user.getPhoneNumber(), "Code for recover your password : " + randomKeyForPassword);
    }

    @Override
    public UserData getBaseUserData(User user, int pageNumber, int perPage) {
        List<UserImage> resultPage = userImageRepository.findAllByUser(user.getId(),(pageNumber-1)*perPage,perPage);
        return UserData.builder()
                .phoneNumber(user.getPhoneNumber())
                .picturesData(resultPage)
                .totoalPageCount(getTotalPageCount(userImageRepository.countAllByUserAndDeletedAtIsNull(user),perPage))
                .fruction(userImageRepository.countAllByUser(user) + "/" + limitCountofImage)
                .build();

    }

    @Override
    public int getTotalPageCount(int allCount, int preSize) {
        int result = 1;
        result = allCount%preSize>0 ? allCount/preSize +1 : allCount/preSize;
        return  result;
    }
}
