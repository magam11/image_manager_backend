package am.arssystems.image_manager_backend.controller;

import am.arssystems.image_manager_backend.dto.request.ImageData;
import am.arssystems.image_manager_backend.dto.request.ImageManagerRequest;
import am.arssystems.image_manager_backend.dto.response.ListOfPickNames;
import am.arssystems.image_manager_backend.dto.response.NextPreviousImageResponse;
import am.arssystems.image_manager_backend.dto.response.UserData;
import am.arssystems.image_manager_backend.entity.User;
import am.arssystems.image_manager_backend.entity.UserImage;
import am.arssystems.image_manager_backend.entity.View;
import am.arssystems.image_manager_backend.repository.UserImageRepository;
import am.arssystems.image_manager_backend.security.CurrentUser;
import am.arssystems.image_manager_backend.service.ImageService;
import am.arssystems.image_manager_backend.service.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/image")
public class UserImageController {

    @Value("${image.folder}")
    private String imagesDereqtion;

    @Value("${count.limit}")
    private String limit;


    private UserImageRepository userImageRepository;
    private ImageService imageService;
    private UserService userService;
    private SimpleDateFormat dateFormat = new SimpleDateFormat();

    @Autowired
    public UserImageController(UserImageRepository userImageRepository,
                               ImageService imageService, UserService userService) {
        this.imageService = imageService;
        this.userImageRepository = userImageRepository;
        this.userService = userService;
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
        String userForlderIndex = pickName.split("_")[0];
        try {

            in = new FileInputStream(imagesDereqtion +userForlderIndex+"\\"+ pickName);
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            IOUtils.copy(in, response.getOutputStream());

        } catch (Exception e) {
            System.out.println("File not found");
        } finally {
            if (in != null)
                in.close();
        }
    }

    @DeleteMapping("/picture/{pickName}") //for desktop
    public ResponseEntity deleteImage(@AuthenticationPrincipal CurrentUser currentUser,
                                      @PathVariable(name = "pickName") String pickName) {
        User user = currentUser.getUser();
        UserImage userImage = userImageRepository.findAllByUserAndAndPicName(user, pickName);
        userImageRepository.delete(userImage);
        ResponseEntity.status(HttpStatus.NO_CONTENT);
        File image = new File(imagesDereqtion +user.getId()+"\\"+ pickName);
        boolean delete = image.delete();
        Map<String, Object> resalt = new HashMap<>();
        resalt.put("success", delete);
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
            String filename = user.getId()+"_"+System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
            File userImageFolder = new File(imagesDereqtion + user.getId());
            if (!userImageFolder.exists()) {
                userImageFolder.mkdirs();
            }
            File image = new File(imagesDereqtion + user.getId() + "\\" + filename);
            multipartFile.transferTo(image);
            result.put("success", true);
            double imageFileSize = imageService.getImageFileSize(image);
            userImageRepository.save(UserImage.builder()
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

    /**
     * Set deletedAt currentDate if actionType = delete and set deletedAt = null if actionType = remake
     *
     * @param
     * @param imageManagerRequest ActionType values (delete, remake)
     * @return httpStatus.ok (200)
     * @created 24.06.2019
     */

    @PutMapping("/")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity updateUserImageDeletedAt(@RequestBody ImageManagerRequest imageManagerRequest,
                                                   @AuthenticationPrincipal CurrentUser currentUser) {
        imageService.setDeletedAtDate(currentUser.getUser(), imageManagerRequest.getPicName(), imageManagerRequest.getActionType());
        return ResponseEntity.ok().build();
    }

    /**
     *
     */

    @GetMapping("/next")
    @JsonView(View.Base.class)
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity getNextImageData(@RequestParam(name = "picName") String picName,
                                           @AuthenticationPrincipal CurrentUser currentUser) {
        List<UserImage> twoNextImagesByPictureName = imageService.getTwoNextImagesByPictureName(picName, currentUser.getUser());
        return ResponseEntity.ok(NextPreviousImageResponse.builder()
                .picturesData(twoNextImagesByPictureName)
                .build());
    }

    @GetMapping("/previous")
    @JsonView(View.Base.class)
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity getPreviousImageData(@RequestParam(name = "picName") String picName,
                                               @AuthenticationPrincipal CurrentUser currentUser) {
        List<UserImage> twoPreviousImageByPictureName = imageService.getTwoPreviousImageByPictureName(picName, currentUser.getUser());
        return ResponseEntity.ok(NextPreviousImageResponse.builder()
                .picturesData(twoPreviousImageByPictureName)
                .build());
    }

    @PutMapping("/many")
    @PreAuthorize("hasAuthority('user')")
    @JsonView(View.Base.class)
    public ResponseEntity updateImageStatusInBatch(@RequestBody @Valid ImageData imageData,
                                                   @AuthenticationPrincipal CurrentUser currentUser,
                                                   @RequestParam(name = "perPage",defaultValue = "50",required = false)int perPage) {
        imageService.updateImageStatus(imageData, currentUser.getUser());
        UserData response = userService.getBaseUserData(currentUser.getUser(), imageData.getPage(),perPage);
        if (response.getTotoalPageCount()>=1 && imageData.getPage() > 1 && (response.getPicturesData() == null || response.getPicturesData().size() == 0)) {
            response = userService.getBaseUserData(currentUser.getUser(), imageData.getPage() - 1,perPage);
        }
        return ResponseEntity.ok(response);

    }

    @GetMapping("/page/{pageNumber}")
    @PreAuthorize("hasAuthority('user')")
    @JsonView(View.Base.class)
    public ResponseEntity getImagesByFromAndTo(@AuthenticationPrincipal CurrentUser currentUser,
                                               @PathVariable("pageNumber") int page,
                                               @RequestParam(name = "fromDate") String fromDate,
                                               @RequestParam(name = "toDate") String toDate,
                                               @RequestParam(name = "perPage",defaultValue = "50",required = false)int perPage) {
        UserData response = imageService.getImagesBeetweenInDate(currentUser.getUser(), fromDate, toDate, page, perPage);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/deleted/page/{page}")
    @JsonView(View.Base.class)
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity getDeletedImageData(@AuthenticationPrincipal CurrentUser currentUser,
                                              @PathVariable("page") int page,
                                              @RequestParam(name = "perPage", required = false, defaultValue = "50")int perPage) {
        UserData response = imageService.getDeletedImageData(currentUser.getUser(), page, perPage);
        if(page>1&& response.getTotoalPageCount()>=1 && (response.getPicturesData()==null ||response.getPicturesData().size()==0)){
            response = imageService.getDeletedImageData(currentUser.getUser(),--page, perPage);
        }
        return ResponseEntity.ok(response);
    }



    @PutMapping("/recoverMany")
    @PreAuthorize("hasAuthority('user')")
    @JsonView(View.Base.class)
    public ResponseEntity deleteInBatch(@RequestBody ImageData imageData,
                                        @AuthenticationPrincipal CurrentUser currentUser,
                                        @RequestParam(name = "perPage", required = false, defaultValue = "50")int perPage){
        imageService.recoveerImagesInBatch(currentUser.getUser(), imageData);
        @NotNull int page = imageData.getPage();
        UserData response = imageService.getDeletedImageData(currentUser.getUser(),  imageData.getPage(),perPage);
        if(imageData.getPage()>1 && response.getTotoalPageCount()>=1 && (response.getPicturesData()==null ||response.getPicturesData().size()==0)){
            response = imageService.getDeletedImageData(currentUser.getUser(),--page,perPage);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/many")
    @PreAuthorize("hasAuthority('user')")
    @JsonView(View.Base.class)
    public ResponseEntity deleteImagesInBatch(@RequestBody ImageData imageData,
                                              @AuthenticationPrincipal CurrentUser currentUser,
                                              @RequestParam(name = "perPage", required = false, defaultValue = "50")int perPage){
        imageService.deleteImages(currentUser.getUser(),imageData.getPicNames());
        @NotNull int page = imageData.getPage();
        UserData response = imageService.getDeletedImageData(currentUser.getUser(),  imageData.getPage(),perPage);
        if(response.getTotoalPageCount()>=1 && imageData.getPage()>1 && (response.getPicturesData()==null ||response.getPicturesData().size()==0)){
            response = imageService.getDeletedImageData(currentUser.getUser(),--page,perPage);
        }
        return ResponseEntity.ok(response);
    }


    @GetMapping("/filter/page/{page}")
    @PreAuthorize("hasAuthority('user')")
    @JsonView(View.Base.class)
    public ResponseEntity filterByYearAndMoth(@AuthenticationPrincipal CurrentUser currentUser,
                                              @PathVariable("page")int page,
                                              @RequestParam(name = "year")String year,
                                              @RequestParam(name = "month")String month,
                                              @RequestParam(name = "perPage", required = false,defaultValue = "50")int perPage){
       UserData response = imageService.getPictureDataByYearAndMonth((page-1)*perPage,currentUser.getUser(),year,month,perPage);
       return ResponseEntity.ok(response);
    }

}
