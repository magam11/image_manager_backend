package am.arssystems.image_manager_backend.entity;


import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "user_image")
public class UserImage {

    //    @Id
//    @Column
//    private String id;
    @Id
    @Column(name = "pic_name")
    @JsonView(View.Base.class)
    private String picName;
    @Column(name = "pic_size")
    @JsonView(View.Base.class)
    private double picSize;
    @ManyToOne
    private User user;
    @Column(name = "created_at",updatable = false)
    @JsonView(View.Base.class)
    private String createdAt;
    @Column(name = "deleted_at")
    private Date deletedAt;


//    @PrePersist
//    public void prePersist() {
//        createdAt =String.valueOf(Timestamp.valueOf(LocalDateTime.now()));
//    }


}
