package am.arssystems.image_manager_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangePasswordRequest {
    @Size(min = 6, message = "Password must contain at least 6 characters")
    private String newPassword;
}
