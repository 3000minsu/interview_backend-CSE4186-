package CSE4186.interview.config;

import CSE4186.interview.annotation.WithCustomMockUser;
import CSE4186.interview.entity.User;
import CSE4186.interview.login.Role;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithCustMockUserContext implements WithSecurityContextFactory<WithCustomMockUser> {

    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser annotation) {
        long id = annotation.id();
        String email = annotation.email();
        String name = annotation.name();
        String password = annotation.password();
        Role role = annotation.role();
        User user = User.builder().email(email).name(name).password(password).build();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user, "");
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        return context;
    }
}
