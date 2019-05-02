package am.arssystems.image_manager_backend.security;


import am.arssystems.image_manager_backend.entity.User;
import org.springframework.security.core.authority.AuthorityUtils;

public class CurrentUser extends org.springframework.security.core.userdetails.User {

    private User user;

    public CurrentUser(User user) {
        super(user.getPhoneNumber(), user.getPassword(),user.getRegisterActivationKey().equals(""),true,true,true, AuthorityUtils.createAuthorityList("user"));
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}
