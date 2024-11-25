package CSE4186.interview.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobField {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String field;

    private String symbol;

    public JobField(String field, String symbol) {
        this.field = field;
        this.symbol = symbol;
    }
}
