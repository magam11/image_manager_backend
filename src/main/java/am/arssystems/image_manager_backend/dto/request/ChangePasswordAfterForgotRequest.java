package am.arssystems.image_manager_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChangePasswordAfterForgotRequest {
    private String phoneNumber;
    private String activationKey;
    private String newPassword;
}
