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

public interface UserRepository extends JpaRepository<User, String> {
    User findAllByPhoneNumber(String phoneNumber);

    User findAllById(int id);

    @Modifying
    @Transactional
    @Query(value = "update User u set u.password =:password where u.id =:userId")
    void changeUserPasswordByUserId(@Param("password") String password,
                                    @Param("userId") int userId);

    @Modifying
    @Transactional
    @Query(value = "update User u set u.registerActivationKey =:newRegisterActivationKey where u.id =:userId")
    void changeUserRegisteredActivationCodeByUserId(@Param("newRegisterActivationKey") String newRegisterActivat,
                                                    @Param("userId") int userId);


}
