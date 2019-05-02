package am.arssystems.image_manager_backend.security;


import am.arssystems.image_manager_backend.entity.User;
import am.arssystems.image_manager_backend.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Qualifier("currentUserDetailServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        final String authToken = request.getHeader(this.tokenHeader);
        String currentUserPhoneNumber = null;
        User currentUser = null;
        if (authToken != null) {
            try {
                 jwtTokenUtil.getPhoneNumberFromToken(authToken);
            }catch (ExpiredJwtException e){
                response.sendError(401,"Unauthorized");
                return;
            }
            String id = (String) jwtTokenUtil.getAllClaimsFromToken(authToken).get("id");
            currentUser = userRepository.findAllById(id);
            if (currentUser == null) {//երբ տվյալ օգտատերը արդեն ամբողջությամբ ջնջվել է մեր համակարգից՝ ջնջվել է ՏԲ-ից։
//                response.setStatus(102, "Դուք ջնջվել եք մեր համակարգից, խնդրում ենք նորից գրանցվել");
                response.sendError(410,"Դուք ջնջվել եք մեր համակարգից, խնդրում ենք նորից գրանցվել");
                return;
            } else {
                try {
                    currentUserPhoneNumber = jwtTokenUtil.getPhoneNumberFromToken(authToken);
                    String tokenPass = (String) jwtTokenUtil.getAllClaimsFromToken(authToken).get("password");
                    if (!currentUserPhoneNumber.equals(currentUser.getPhoneNumber()) || !currentUser.getPassword().equals(tokenPass)) {
                        response.sendError(403, "Փոփոխվել է հեռախոսի համար կամ գաղտնաբառ");
                        return;
                    }
                } catch (ExpiredJwtException e) {
//                // սա աշխատում է այն ժամանակ երբ թոկենի ժամանակը լրացել է, այսինքն թոկենը էլ ակտիվ չէ
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        } else {
            logger.warn("couldn't find * header, will ignore the header");
        }

        if (currentUser != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String usernamelowercase = currentUser.getPhoneNumber();
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(usernamelowercase);
            String tokenId = (String) jwtTokenUtil.getAllClaimsFromToken(authToken).get("id");

            System.out.println("-----1 "+ (currentUser.getId() == tokenId));
            System.out.println("-----2 "+ (currentUser.getPhoneNumber().equals(currentUserPhoneNumber)));
            if (currentUserPhoneNumber != null && jwtTokenUtil.validateToken(authToken, userDetails.getUsername().trim()) &&
                    userDetails.getPassword().equals(jwtTokenUtil.getAllClaimsFromToken(authToken).get("password")) &&
                    currentUser.getId().equals(tokenId ) && currentUser.getPhoneNumber().equals(currentUserPhoneNumber)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}

