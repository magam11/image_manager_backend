package am.arssystems.image_manager_backend.controller;


import am.arssystems.image_manager_backend.Constant;
import am.arssystems.image_manager_backend.dto.AuthenticationRequest;
import am.arssystems.image_manager_backend.dto.AuthenticationResponse;
import am.arssystems.image_manager_backend.dto.Response;
import am.arssystems.image_manager_backend.dto.request.ChangePasswordRequest;
import am.arssystems.image_manager_backend.dto.request.VerifyRequest;
import am.arssystems.image_manager_backend.dto.response.ChangePasswordResponse;
import am.arssystems.image_manager_backend.dto.response.LoginResponseForDesktop;
import am.arssystems.image_manager_backend.dto.response.RegisterResponse;
import am.arssystems.image_manager_backend.dto.response.VerifyResponse;
import am.arssystems.image_manager_backend.entity.User;
import am.arssystems.image_manager_backend.repository.UserRepository;
import am.arssystems.image_manager_backend.security.CurrentUser;
import am.arssystems.image_manager_backend.security.JwtTokenUtil;
import am.arssystems.image_manager_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private UserService userService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenUtil jwtTokenUtil;


    @Autowired
    public UserController(UserService userService, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register") // for android
    public ResponseEntity userRegister(@RequestBody User user) {
        RegisterResponse response = new RegisterResponse();
        String phoneNumber = user.getPhoneNumber();
        User userByPhoneNumberFromDB = userRepository.findAllByPhoneNumber(phoneNumber);
        if (userByPhoneNumberFromDB == null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.saveUser(user);
            response.setActionCode(1);
            response.setSuccess(true);
            response.setMessage("The user is successfully registered");
        } else {
            response.setSuccess(false);
            if (!userByPhoneNumberFromDB.getRegisterActivationKey().isEmpty()) {
                response.setActionCode(0);
                response.setMessage("You are already registered. Please write the activation code");
            } else {
                response.setActionCode(-1);
                response.setMessage("You are already registered.");
            }
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/changePassword") // for android
    public ResponseEntity changePasswordByPhoneNumber(@AuthenticationPrincipal CurrentUser currentUser,
                                                      @RequestBody ChangePasswordRequest changePasswordRequest) {
        String newPassword = changePasswordRequest.getNewPassword();
        if (newPassword == null || newPassword.isEmpty() || newPassword.length() < Constant.MIN_LENGTH_OF_PASSWORD) {
            return ResponseEntity.ok(ChangePasswordResponse.builder()
                    .message("Password must contain at least 6 characters")
                    .success(false)
                    .build());
        } else {
            User user = currentUser.getUser();
            ChangePasswordResponse response = userService.changePassword(user, changePasswordRequest.getNewPassword());
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/login")//for desktop
    public ResponseEntity login(@RequestBody AuthenticationRequest authenticationRequest) {
        User user = userRepository.findAllByPhoneNumber(authenticationRequest.getPhoneNumber());
        if (user == null || !passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.ok(LoginResponseForDesktop.builder()
                    .success(false)
                    .message("Invalid entered phone number or password")
                    .build());
        } else {
            if (user.getRegisterActivationKey().isEmpty()) {
                return ResponseEntity.ok(LoginResponseForDesktop.builder()
                        .message("")
                        .success(true)
                        .userInfo(AuthenticationResponse.builder()
                                .token(jwtTokenUtil.generateTokenPassAndId(user.getPhoneNumber(), user.getPassword(), user.getId()))
                                .name(user.getName())
                                .build())
                        .build());
            }else {
                return ResponseEntity.ok(LoginResponseForDesktop.builder()
                        .success(false)
                        .message("Your account is not active")
                        .build());
            }
        }
    }

    @PostMapping("/verify")
    public ResponseEntity verifyCode(@RequestBody VerifyRequest verifyRequest) {
        VerifyResponse result = new VerifyResponse();
        User currentUser = userRepository.findAllByPhoneNumber(verifyRequest.getPhoneNumber());
        result.setSuccess(false);
        if (currentUser != null && currentUser.getRegisterActivationKey().equals(verifyRequest.getVerifyCode())) {
            String token = jwtTokenUtil.generateTokenPassAndId(verifyRequest.getPhoneNumber(), currentUser.getPassword(), currentUser.getId());
            result.setSuccess(true);
            result.setToken(token);
            userRepository.changeUserRegisteredActivationCodeByUserId("",currentUser.getId());

        }

        return ResponseEntity.ok(result);
    }

}
