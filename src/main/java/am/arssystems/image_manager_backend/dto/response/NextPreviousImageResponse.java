package am.arssystems.image_manager_backend.dto.response;

import am.arssystems.image_manager_backend.entity.UserImage;
import am.arssystems.image_manager_backend.entity.View;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NextPreviousImageResponse {
    @JsonView(View.Base.class)
    private List<UserImage> picturesData;

}
