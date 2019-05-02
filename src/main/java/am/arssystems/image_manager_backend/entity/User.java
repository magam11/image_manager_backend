package am.arssystems.image_manager_backend.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "user")
public class User {
    @Id
    @Column
    private String id;
    @Column
    private String name;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column
    private String password;
    @Column(name = "register_activation_key")
    private String registerActivationKey;
}
