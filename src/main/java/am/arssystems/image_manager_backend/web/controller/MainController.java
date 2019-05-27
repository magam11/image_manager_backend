package am.arssystems.image_manager_backend.web.controller;

import am.arssystems.image_manager_backend.dto.AuthenticationRequest;
import am.arssystems.image_manager_backend.dto.AuthenticationResponse;
import am.arssystems.image_manager_backend.dto.Response;
import am.arssystems.image_manager_backend.entity.User;
import am.arssystems.image_manager_backend.entity.UserImage;
import am.arssystems.image_manager_backend.repository.UserRepository;
import am.arssystems.image_manager_backend.security.CurrentUser;
import am.arssystems.image_manager_backend.security.JwtTokenUtil;
import am.arssystems.image_manager_backend.web.dto.UserInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/")
public class MainController {
    private UserRepository userRepository;
    private JwtTokenUtil jwtTokenUtil;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public MainController(UserRepository userRepository, JwtTokenUtil jwtTokenUtil,
                          PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
    }

    @GetMapping("/userInfo")
    public ResponseEntity userInfoForFirsPage(@AuthenticationPrincipal CurrentUser currentUser) {
        return ResponseEntity.ok(UserInfoResponse.builder()
                .phoneNumber(currentUser.getUser().getPhoneNumber())
                .userId(currentUser.getUser().getId())
                .userName(currentUser.getUser().getName())
                .build());


    }


    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationRequest authenticationRequest) throws ServletException, IOException {
        System.out.println("phoneNumber " + authenticationRequest.getPhoneNumber());
        System.out.println("password " + authenticationRequest.getPhoneNumber());
        User userDB = userRepository.findAllByPhoneNumber(authenticationRequest.getPhoneNumber());
        if (userDB == null || (userDB != null && !passwordEncoder.matches(authenticationRequest.getPassword(),userDB.getPassword()))) {
            return ResponseEntity.ok(Response.builder()
                    .message("Invalide phone number or password")
                    .build());
        }
        if (userDB!=null && userDB.getRegisterActivationKey().isEmpty()){
            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .name(userDB.getName())
                    .success(true)
                    .token(jwtTokenUtil.generateTokenPassAndId(userDB.getPhoneNumber(), userDB.getPassword(), userDB.getId()))
                    .build());
        }
        else {
            return ResponseEntity.ok(Response.builder()
                    .message("Your account is not active")
                    .build());
        }
//        modelMap.addAttribute("loginedUser", allByPhoneNumber);
//        modelMap.addAttribute("currentUser", allByPhoneNumber);
//        modelMap.addAttribute("isLoggedIn", true);
//        httpServletRequest.getRequestDispatcher("/home").forward(httpServletRequest, httpServletResponse);
//        return "redihome";


    }

    @GetMapping("/")
    public String home(ModelMap modelMap,

                       @AuthenticationPrincipal CurrentUser currentUser) throws ServletException, IOException {
        modelMap.addAttribute("isLoggedIn", currentUser != null);
        if (currentUser != null) {
            modelMap.addAttribute("currentUser", currentUser.getUser());
//            httpServletRequest.getRequestDispatcher("/home").forward(httpServletRequest, httpServletResponse);
        }
        return "home";
    }



    @PostMapping("/addImage") //for android
    public ResponseEntity addImage(@RequestParam(name = "picture") MultipartFile multipartFile) throws IOException {
        System.out.println("nkar ekav frontic");
        Map<String, Object> result = new HashMap<>();
            String filename = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
            File image = new File("C:\\Users\\Maga\\Desktop\\nkarner\\" + filename);
            multipartFile.transferTo(image);
            result.put("success", true);
        return ResponseEntity.ok(result);
    }


}
