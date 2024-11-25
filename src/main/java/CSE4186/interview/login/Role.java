package CSE4186.interview.login;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER ("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    SUSPEND("ROLE_SUSPEND");

    private final String roles;
    public static String getIncludingRoles(String role){
        return Role.valueOf(role).getRoles();
    }
    public static String addRole(Role role, String addRole){
        String priorRoles=role.getRoles();
        priorRoles+=","+addRole;
        return priorRoles;
    }
    public static String addRole(String roles, Role role){
        return roles+","+role.getRoles();
    }
}
