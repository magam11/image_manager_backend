package am.arssystems.image_manager_backend.dto.response;

import am.arssystems.image_manager_backend.dto.AuthenticationResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginResponseForDesktop {
    private boolean success;
    private String message;
    private AuthenticationResponse userInfo;
}
