package CSE4186.interview.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewDto {
    private List<Integer> verbal;
    @JsonProperty("nonverbal")
    private List<Integer> nonVerbal;
    private String review;

    public ReviewDto(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ReviewDto reviewDto = objectMapper.readValue(jsonString, ReviewDto.class);
            this.verbal = reviewDto.getVerbal();
            this.nonVerbal = reviewDto.getNonVerbal();
            this.review = reviewDto.getReview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
