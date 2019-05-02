package am.arssystems.image_manager_backend.config;

import am.arssystems.image_manager_backend.security.CurrentUserDetailServiceImpl;
import am.arssystems.image_manager_backend.security.JwtAuthenticationEntryPoint;
import am.arssystems.image_manager_backend.security.JwtAuthenticationTokenFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private CurrentUserDetailServiceImpl userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;
//    @Autowired
//    private AccessDeniedHandler accessDeniedHandler;


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                // don't create session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
//                .exceptionHandling().accessDeniedHandler(accessDeniedHandler)
//                .and()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)

                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/user/changepassword").hasAnyAuthority("user")
                .antMatchers(HttpMethod.POST, "/image/addImage").hasAnyAuthority("user")

                .anyRequest().permitAll();
        // Custom JWT based security filter
//        http.addFilterBefore(JwtExpiredFilter(),UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
//      disable page caching
        http.headers().cacheControl();
//        http
//        .exceptionHandling().accessDeniedHandler(accessDeniedHandler);
    }




    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);
    }

//    @Autowired
//    private DataSource dataSource;

//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .userDetailsService(userDetailsService)
//                .passwordEncoder(passwordEncoder)
//                .and()
//                .jdbcAuthentication()
//                .dataSource(dataSource)
//                .withDefaultSchema()
//                .withUser("user").password("password").roles("user").and()
//                .withUser("admin").password("password").roles("user", "admin");
//    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public FilterRegistrationBean<JwtExpiredFilter> loggingFilter() {
//        FilterRegistrationBean<JwtExpiredFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE -1);
//
//        registrationBean.setFilter(new JwtExpiredFilter());
////        registrationBean.addUrlPatterns("/users/Ex");
//        registrationBean.setUrlPatterns(Arrays.asList("/users/Ex"));
//        return registrationBean;
//    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }
//    @Bean
//    public JwtExpiredFilter JwtExpiredFilter() throws Exception {
//        return new JwtExpiredFilter();
//    }
}