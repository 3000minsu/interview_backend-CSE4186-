package CSE4186.interview.annotation;

import CSE4186.interview.config.WithCustMockUserContext;
import CSE4186.interview.login.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustMockUserContext.class)
public @interface WithCustomMockUser {

    long id() default 1L;

    String name() default "test";

    String email() default "test@gmail.com";

    String password() default "password";
    Role role() default Role.USER;

}
