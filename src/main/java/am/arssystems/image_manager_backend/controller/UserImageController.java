package am.arssystems.image_manager_backend.controller;

import am.arssystems.image_manager_backend.dto.response.ListOfPickNames;
import am.arssystems.image_manager_backend.entity.User;
import am.arssystems.image_manager_backend.entity.UserImage;
import am.arssystems.image_manager_backend.repository.UserImageRepository;
import am.arssystems.image_manager_backend.security.CurrentUser;
import am.arssystems.image_manager_backend.service.ImageService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/image")
public class UserImageController {

    @Value("${image.folder}")
    private String imagesDereqtion;
    @Value("${count.limit}")
    private String limit;


    private UserImageRepository userImageRepository;
    private ImageService imageService;

    public UserImageController(UserImageRepository userImageRepository,ImageService imageService){
        this.imageService = imageService;
        this.userImageRepository = userImageRepository;
    }



    @PostMapping("/listPickNames") //for desktop
    public ResponseEntity getImagesNames(@AuthenticationPrincipal CurrentUser currentUser) {
        User user = currentUser.getUser();
        List<String> pickNames = userImageRepository.findPickNamesByUserId(user.getId());
        return ResponseEntity.ok(ListOfPickNames.builder()
                .pickNames(pickNames)
                .build());
    }




    @RequestMapping(value = "/{pickName}", method = RequestMethod.GET) //for desktop
    public void getImageAsByteArrayForUserPic(HttpServletResponse response,
                                              @PathVariable(name = "pickName") String pickName) throws IOException {
        double picSizeByPicName = userImageRepository.getPicSizeByPicName(pickName);
        response.setHeader("pictureSize", picSizeByPicName + "");
        InputStream in = null;
        try {

            in = new FileInputStream(imagesDereqtion + pickName);
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            IOUtils.copy(in, response.getOutputStream());

        } catch (Exception e) {
            System.out.println("File not found");
        } finally {
            if (in != null)
                in.close();
        }
    }

    @PostMapping("/deleteImage") //for desktop
    public ResponseEntity deleteImage(@AuthenticationPrincipal CurrentUser currentUser,
                                      @RequestParam(name = "picName") String pickName) {
        User user = currentUser.getUser();
        UserImage userImage = userImageRepository.findAllByUserAndAndPicName(user, pickName);
        userImageRepository.delete(userImage);
        ResponseEntity.status(HttpStatus.NO_CONTENT);
        File image = new File(imagesDereqtion + pickName);
        boolean delete = image.delete();
        Map<String, Object> resalt = new HashMap<>();
        resalt.put("success", true);
        resalt.put("message", "DELETED");
        return ResponseEntity.ok(resalt);
    }


    @PostMapping("/addImage") //for android
    public ResponseEntity addImage(@AuthenticationPrincipal CurrentUser currentUser,
                                   @RequestParam(name = "picture") MultipartFile multipartFile) throws IOException {
        Map<String, Object> result = new HashMap<>();

        User user = currentUser.getUser();
        int count = userImageRepository.countAllByUser(user);
        if (count < Integer.parseInt(limit)) {
            String filename = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
            File image = new File(imagesDereqtion + filename);
            multipartFile.transferTo(image);
            result.put("success", true);
            double imageFileSize = imageService.getImageFileSize(image);
            userImageRepository.save(UserImage.builder()
                    .id(System.currentTimeMillis() + "_" + UUID.randomUUID().toString())
                    .picName(filename)
                    .user(user)
                    .picSize(imageFileSize)
                    .build());
        } else {
            result.put("success", false);
            result.put("message", "The maximum amount of storage for your pictures has expired");
        }
        return ResponseEntity.ok(result);
    }

}
