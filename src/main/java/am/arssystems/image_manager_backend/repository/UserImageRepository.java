package am.arssystems.image_manager_backend.repository;

import am.arssystems.image_manager_backend.entity.User;
import am.arssystems.image_manager_backend.entity.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserImageRepository extends JpaRepository<UserImage, String> {
    UserImage findAllByUserAndAndPicName(User user, String pickname);

    @Query(value = "select  u.pic_name from user_image u where u.user_id=:userId",nativeQuery = true)
    List<String> findPickNamesByUserId(@Param("userId")String userId);

    @Query(value = "select e.picSize from UserImage e where e.picName=:picName")
    double getPicSizeByPicName(@Param("picName")String picName);

    int countAllByUser(User user);

    int countAllByUserAndAndDeletedAtIsNull(User user);

    @Query(value = "select ui from UserImage  ui where ui.user=:user")
    List<UserImage> getAllByUser(@Param("user") User user);

    @Query(value = "select ui.* from user_image  ui where ui.user_id=:userId and ui.deleted_at is null limit :pageNumber, :itemsCount",nativeQuery = true)
    List<UserImage> findAllByUser(@Param("userId") String userId,
                                  @Param("pageNumber")int pageNumber,
                                  @Param("itemsCount")int itemsCount);

    @Modifying
    @Transactional
    @Query(value = "update UserImage u set u.deletedAt=current_timestamp where u.user=:user and u.picName=:picName")
    void updateUserImageDeletedAtByUserAndPicName(@Param("user")User user,
                                                  @Param("picName")String picName);
    @Modifying
    @Transactional
    @Query(value = "update UserImage u set u.deletedAt=null where u.user=:user and u.picName=:picName")
    void setUserImageDeletedAtNulByUserAndPicName(@Param("user") User currentUser,
                                                  @Param("picName") String picName);
}
