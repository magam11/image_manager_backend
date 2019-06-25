package am.arssystems.image_manager_backend.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "user")
public class User {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String name;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column
    private String password;
    @Column(name = "register_activation_key")
    private String registerActivationKey;
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "user")
    private Set<UserImage>  userImages;
}
