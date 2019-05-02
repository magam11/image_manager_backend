package am.arssystems.image_manager_backend.service;

import am.arssystems.image_manager_backend.dto.response.ChangePasswordResponse;
import am.arssystems.image_manager_backend.entity.User;
import am.arssystems.image_manager_backend.repository.UserRepository;
import am.arssystems.image_manager_backend.security.JwtTokenUtil;
import am.arssystems.image_manager_backend.twilio.TwilioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
public class UserService {

    private UserRepository userRepository;
    private TwilioUtil twilioUtil;
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserService (UserRepository userRepository, TwilioUtil twilioUtil,
                        JwtTokenUtil jwtTokenUtil){
        this.userRepository = userRepository;
        this.twilioUtil = twilioUtil;
    }

    public void saveUser(User user) {
        String userId = System.currentTimeMillis()+ UUID.randomUUID().toString();
        user.setId(userId);
        String registerActivationKey = createRandomRegistrationKey(5);
        twilioUtil.sendSMS(user.getPhoneNumber(),registerActivationKey);
        user.setRegisterActivationKey(registerActivationKey);
        userRepository.save(user);

    }

    private String createRandomRegistrationKey(int lengthKey) {
        final Random random = new Random();
        final String CHARS = "0123456789";
        StringBuilder password = new StringBuilder(lengthKey);
        for (int i = 0; i < lengthKey; i++) {
            password.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return password.toString();
    }


    public ChangePasswordResponse changePassword(User user, String newPassword) {
        String newToken = jwtTokenUtil.generateTokenPassAndId(user.getPhoneNumber(),newPassword,user.getId());
        userRepository.changeUserPasswordByUserId(newPassword,user.getId());
        return ChangePasswordResponse.builder()
                .success(true)
                .message("Password changed")
                .newToken(newToken)
                .build();
    }
}
