package CSE4186.interview.entity;

import CSE4186.interview.login.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private Boolean suspensionStatus = false;

    @Enumerated(EnumType.STRING)
    private Role authority;

    public User(String name, String email, String password, Role authority){
        this.name=name;
        this.email=email;
        this.password=password;
        this.authority=authority;
    }

    public User(String name, String email, Role authority){
        this.name=name;
        this.email=email;
        this.password="";
        this.authority=authority;
    }

    public boolean isSuspended() {
        return this.getAuthority() == Role.SUSPEND;
    }

    public void accountSuspension() {
        this.authority = Role.SUSPEND;
    }
}