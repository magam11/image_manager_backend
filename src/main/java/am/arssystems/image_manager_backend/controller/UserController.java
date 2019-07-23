package am.arssystems.image_manager_backend.controller;


import am.arssystems.image_manager_backend.Constant;
import am.arssystems.image_manager_backend.dto.AuthenticationRequest;
import am.arssystems.image_manager_backend.dto.AuthenticationResponse;
import am.arssystems.image_manager_backend.dto.Response;
import am.arssystems.image_manager_backend.dto.request.ChangePasswordAfterForgotRequest;
import am.arssystems.image_manager_backend.dto.request.ChangePasswordRequest;
import am.arssystems.image_manager_backend.dto.request.VerifyRequest;
import am.arssystems.image_manager_backend.dto.response.*;
import am.arssystems.image_manager_backend.entity.User;
import am.arssystems.image_manager_backend.entity.UserImage;
import am.arssystems.image_manager_backend.entity.View;
import am.arssystems.image_manager_backend.repository.UserImageRepository;
import am.arssystems.image_manager_backend.repository.UserRepository;
import am.arssystems.image_manager_backend.security.CurrentUser;
import am.arssystems.image_manager_backend.security.JwtTokenUtil;
import am.arssystems.image_manager_backend.service.UserService;
import am.arssystems.image_manager_backend.service.serviceImpl.UserServiceImpl;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private UserService userService;
    private UserRepository userRepository;
    private UserImageRepository userImageRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenUtil jwtTokenUtil;
    @Value("${image.folder}")
    private String imageDirection;


    @Autowired
    public UserController(UserServiceImpl userService, UserRepository userRepository,
                          PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil,
                          UserImageRepository userImageRepository) {
        this.userImageRepository = userImageRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping("/data/page/{pageNumber}")
    @JsonView(View.Base.class)
    public ResponseEntity getUserData(@AuthenticationPrincipal CurrentUser currentUser,
                                      @PathVariable("pageNumber")int pageNumber,
                                      @RequestParam(name = "perPage",required = false, defaultValue = "50")int perPage){
        System.out.println("[eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee "+perPage);
       UserData response =  userService.getBaseUserData(currentUser.getUser(),pageNumber,perPage);
       return ResponseEntity.ok(response);

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
                                                      @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
            User user = currentUser.getUser();
            ChangePasswordResponse response = userService.changePassword(user, changePasswordRequest.getNewPassword());
            return ResponseEntity.ok(response);

    }

    @PostMapping("/login")//for desktop
    public ResponseEntity login(@RequestBody AuthenticationRequest authenticationRequest) {
        User user = userRepository.findAllByPhoneNumber(authenticationRequest.getPhoneNumber());
        if (user == null || !passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.ok(LoginResponseForDesktop.builder()
                    .success(false)
                    .message("Invalid phone number or password")
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

    @PostMapping("/forgotPassword")
    public ResponseEntity forgotPassword(@RequestParam(name = "phoneNumber")String phoneNumber){
        User user = userRepository.findAllByPhoneNumber(phoneNumber);
        if(user==null){
            return ResponseEntity.ok(Response.builder()
                    .message("There is no user registered system with the specified phone number")
                    .success(false)
                    .build());
        }else{
            userService.setUserPasswordRandomActivationKeyAndSendSMS(user);
            return ResponseEntity.ok(Response.builder()
                    .success(true)
                    .message("Ok")
                    .build());
        }
    }

    @PostMapping("/changePassAfterForgot")
    public ResponseEntity changePasswordAfterForgot(@RequestBody ChangePasswordAfterForgotRequest changePasswordAfterForgotRequest){
        String phoneNumber = changePasswordAfterForgotRequest.getPhoneNumber();
        User user = userRepository.findAllByPhoneNumber(phoneNumber);
        if(changePasswordAfterForgotRequest.getNewPassword()==null || changePasswordAfterForgotRequest.getNewPassword().length()<6){
            return ResponseEntity.ok(Response.builder()
                    .message("Password must contain at least 6 characters")
                    .success(false)
                    .build());
        }
        if (user!=null && changePasswordAfterForgotRequest.getActivationKey().equals(jwtTokenUtil.getPhoneNumberFromToken(user.getPassword()))){
            user.setPassword(passwordEncoder.encode(changePasswordAfterForgotRequest.getNewPassword()));
            userRepository.save(user);
            return ResponseEntity.ok(ChangePasswordResponse.builder()
                    .newToken(jwtTokenUtil.generateTokenPassAndId(user.getPhoneNumber(),user.getPassword(),user.getId()))
                    .message("Ok")
                    .success(true)
                    .build());
        }
        return ResponseEntity.ok(Response.builder()
                .success(false)
                .message("Invalid code")
                .build());
    }

    @DeleteMapping("/")
    public ResponseEntity deleteUser(@AuthenticationPrincipal CurrentUser currentUser){
        User user = currentUser.getUser();
        Map<String,Object> map = new HashMap<>();
        Set<String> keySet = map.keySet();
        List<UserImage> userImages = userImageRepository.getAllByUser(user);
        for (UserImage userImage : userImages) {
            String picName = userImage.getPicName();
            File image = new File(imageDirection+picName);
            image.delete();
            userImageRepository.delete(userImage);
        }
        userRepository.delete(user);
        return ResponseEntity.ok(Response.builder()
                .success(true)
                .message("User deleted")
                .build());
    }



}
