package am.arssystems.image_manager_backend.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "user_image")
public class UserImage {

    @Id
    @Column
    private String id;
    @Column(name = "pic_name")
    private String picName;
    @Column(name = "pic_size")
    private double picSize;
    @ManyToOne
    private User user;



}
