package am.arssystems.image_manager_backend.entity;


import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "user_image")
public class UserImage {


    @Id
    @Column(name = "pic_name")
    @JsonView(View.Base.class)
    private String picName;
    @Column(name = "pic_size")
    @JsonView(View.Base.class)
    private double picSize;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;
    @Column(name = "created_at",nullable = false,updatable = false)
    @JsonView(View.Base.class)
//    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Timestamp createdAt;
    @Column(name = "deleted_at")
    @JsonView(View.Base.class)
    private Date deletedAt;




//    @PrePersist
//    public void prePersist() {
//        createdAt =String.valueOf(Timestamp.valueOf(LocalDateTime.now()));
//    }


}
