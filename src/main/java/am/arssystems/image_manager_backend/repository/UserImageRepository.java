package am.arssystems.image_manager_backend.repository;

import am.arssystems.image_manager_backend.entity.User;
import am.arssystems.image_manager_backend.entity.UserImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface UserImageRepository extends JpaRepository<UserImage, Integer> {
    UserImage findAllByUserAndAndPicName(User user, String pickname);

    @Query(value = "select  u.pic_name from user_image u where u.user_id=:userId", nativeQuery = true)
    List<String> findPickNamesByUserId(@Param("userId") int userId);

    @Query(value = "select e.picSize from UserImage e where e.picName=:picName")
    double getPicSizeByPicName(@Param("picName") String picName);

    int countAllByUser(User user);

    int countAllByUserAndDeletedAtIsNull(User user);

    @Query(value = "select ui from UserImage  ui where ui.user=:user")
    List<UserImage> getAllByUser(@Param("user") User user);

    @Query(value = "select ui.* from user_image  ui where ui.user_id=:userId and ui.deleted_at is null limit :pageNumber, :itemsCount", nativeQuery = true)
    List<UserImage> findAllByUser(@Param("userId") int userId,
                                  @Param("pageNumber") int pageNumber,
                                  @Param("itemsCount") int itemsCount);

    @Modifying
    @Transactional
    @Query(value = "update UserImage u set u.deletedAt=current_timestamp where u.user=:user and u.picName=:picName")
    void updateUserImageDeletedAtByUserAndPicName(@Param("user") User user,
                                                  @Param("picName") String picName);

    @Modifying
    @Transactional
    @Query(value = "update UserImage u set u.deletedAt=null where u.user=:user and u.picName=:picName")
    void setUserImageDeletedAtNulByUserAndPicName(@Param("user") User currentUser,
                                                  @Param("picName") String picName);

    @Modifying
    @Transactional
    @Query(value = "delete from UserImage u  where u.user=:user and u.picName in :picNames")
    void deleteInBatch(@Param("user") User currentUser,
                       @Param("picNames")Collection<String> picNames);

    @Query(value = "select ui from UserImage  ui where ui.user=:user and ui.deletedAt is null and " +
            "ui.createdAt > (select u.createdAt from UserImage  u where u.picName=:picName)")
    Page<UserImage> getUserImageByPreviusImageName(@Param("picName") String picName,
                                                   @Param("user") User user,
                                                   Pageable pageable);


    @Query(value = "select ui from UserImage  ui where ui.user=:user and ui.deletedAt is null and " +
            "ui.createdAt < (select u.createdAt from UserImage  u where u.picName=:picName) order by ui.createdAt desc ")
    Page<UserImage> getUserImageByNextImageName(@Param("picName") String picName,
                                                @Param("user") User user,
                                                Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "update UserImage u set u.deletedAt=current_timestamp where u.user=:user and u.picName in (:picNames)")
    void updateImageStatusInBatch(@Param("picNames") Collection<String> picNames,
                                  @Param("user") User user);

    @Transactional
    @Modifying
    @Query(value = "update UserImage u set u.deletedAt=null where u.user=:user and u.picName in (:picNames)")
    void recoverImageInBatch(@Param("picNames") Collection<String> picNames,
                             @Param("user") User user);


    @Query(value = "select ui.* from user_image ui where ui.user_id=:userId and ui.created_at<=:toDate limit :page, :itemsSize", nativeQuery = true)
    List<UserImage> getByUserAndCreatedAtLessThan(@Param("userId") int id,
                                                  @Param("toDate") String toDate,
                                                  @Param("page") int page,
                                                  @Param("itemsSize") int itemsSize);

    @Query(value = "select count(ui.pic_name) from user_image ui where ui.user_id=:currentUserId and ui.created_at<=:toDate", nativeQuery = true)
    int countAllByUserIdAndCreatedAtLessThan(@Param("currentUserId") int id,
                                             @Param("toDate") String toDate);

    @Query(value = "select ui.* from user_image ui where ui.user_id=:userId and ui.created_at>=:fromDate limit :page, :itemsSize", nativeQuery = true)
    List<UserImage> getByUserAndCreatedAtGreaterThan(@Param("userId") int currentUserId,
                                                     @Param("fromDate") String fromDate,
                                                     @Param("page") int page,
                                                     @Param("itemsSize") int itemsSize);

    @Query(value = "select count(ui.pic_name) from user_image ui where ui.user_id=:currentUserId and ui.created_at>=:fromDate", nativeQuery = true)
    int countAllByUserIdAndCreatedAtGreaterThan(@Param("currentUserId") int id,
                                                @Param("fromDate") String fromDate);

    @Query(value = "select ui.* from user_image ui where ui.user_id=:currentUserId and ui.created_at>=:fromDate and " +
            "ui.created_at<=:toDate limit :page, :itemsSize", nativeQuery = true)
    List<UserImage> getByUserAndCreatedAtGreaterThanAndLessThan(@Param("currentUserId") int id,
                                                                @Param("fromDate") String fromDate,
                                                                @Param("toDate") String toDate,
                                                                @Param("page") int page,
                                                                @Param("itemsSize") int itemsSize);

    @Query(value = "select count(ui.pic_name) from user_image ui where ui.user_id=:currentUserId and ui.created_at>=:fromDate" +
            " and ui.created_at<=:toDate", nativeQuery = true)
    int countAllByUserIdAndCreatedAtGreaterThanAndLessThan(@Param("currentUserId") int id,
                                                           @Param("toDate") String toDate,
                                                           @Param("fromDate") String fromDate);

    @Query(value = "select ui.* from user_image ui where  ui.user_id=:currentUserId and ui.deleted_at is not null limit :page, :itemsSize", nativeQuery = true)
    List<UserImage> findAllByUserAndCreatedAtIsNotNull(@Param("currentUserId") int userId,
                                                       @Param("page") int page,
                                                       @Param("itemsSize") int itemsSize);

    int countAllByUserAndDeletedAtIsNotNull(User user);

    @Query(value = "select ui.* from user_image ui where ui.user_id=:currentUserId and" +
            " ui.created_at like concat(:dateData,'%') and ui.deleted_at is null limit :page,:itemSize", nativeQuery = true)
    List<UserImage> getPicNamesByUserAndByCreatedAt(@Param("page") int page,
                                                 @Param("currentUserId") int userId,
                                                 @Param("dateData") String date,
                                                 @Param("itemSize") int itemSize);
    @Query(value = "select count(ui.pic_name )from user_image ui where ui.user_id=:currentUserId and" +
            " ui.created_at like concat(:dateData,'%') and ui.deleted_at is null", nativeQuery = true)
    int countByUserAndCreatedAtLike(@Param("currentUserId") int userId ,
                                    @Param("dateData") String date);
}
