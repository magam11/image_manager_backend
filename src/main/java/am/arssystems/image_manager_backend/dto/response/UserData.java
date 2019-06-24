package am.arssystems.image_manager_backend.dto.response;


import am.arssystems.image_manager_backend.entity.UserImage;
import am.arssystems.image_manager_backend.entity.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserData {

    @JsonView(View.Base.class)
    private List<UserImage> picturesData;
    @JsonView(View.Base.class)
    private String fruction;
    @JsonView(View.Base.class)
    private int totoalPageCount;
    @JsonView(View.Extra.class)
    private List<String> deletedImageNames;
    @JsonView(View.Base.class)
    private String phoneNumber;


    public UserData(List<UserImage> pictureNames,String fruction){
        this.picturesData = pictureNames;
        this.fruction = fruction;
    }

}
