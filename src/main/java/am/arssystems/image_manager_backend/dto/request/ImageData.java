package am.arssystems.image_manager_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ImageData {

    private Collection<String> picNames;
    private boolean imageStatus;
    @NotNull
    private int page;
    
//    @Override
//    public String toString() {
//        String picNames = "";
//        for (String picName : this.picNames) {
//            picNames += picName + ",";
//        }
//        picNames = picNames.substring(0, picNames.length() - 1);
//        return "{" +
//                "\"page\"" + ":\"" + page + '\"' + "," +
//                "\"picNames\"" + ":[" + picNames + "]" +
//                '}';
//    }
}