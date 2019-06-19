package am.arssystems.image_manager_backend.repository;

import am.arssystems.image_manager_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, String> {
    User findAllByPhoneNumber(String phoneNumber);
    User findAllById(String id);

    @Modifying
    @Transactional
    @Query(value = "update User u set u.password =:password where u.id =:userId")
    void changeUserPasswordByUserId(@Param("password")String password,
                                    @Param("userId")String userId);
    @Modifying
    @Transactional
    @Query(value = "update User u set u.registerActivationKey =:newRegisterActivationKey where u.id =:userId")
    void changeUserRegisteredActivationCodeByUserId( @Param("newRegisterActivationKey") String newRegisterActivat,
            @Param("userId") String userId);

}
