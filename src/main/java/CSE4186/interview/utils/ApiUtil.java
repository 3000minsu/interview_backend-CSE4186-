package CSE4186.interview.utils;

import CSE4186.interview.controller.dto.BaseResponseDto;
import lombok.Getter;

public class ApiUtil {

    /**
     * Api 응답 성공 시 사용하는 메서드 응답 body를 담고있다.
     * @param body
     * @return
     */
    public static <T> ApiSuccessResult<T> success(T body) {
        return new ApiSuccessResult<>("success", body);
    }


    /**
     * Api 응답 실패 시 사용하는 메서드 errorCode와 message를 담고있다.
     * @param errorCode
     * @param mesasge
     * @return
     */
    public static <T> ApiErrorResult<T> error(int errorCode, T mesasge) {
        return new ApiErrorResult<>("fail", errorCode, mesasge);
    }


    /**
     * Api 성공 시 응답 형식
     * @param <T>
     */
    @Getter
    public  static class ApiSuccessResult<T> {

        private final String result;
        private final T body;

        public ApiSuccessResult(String result, T body) {
            this.result = result;
            this.body = body;
        }
    }

    /**
     * Api 실패 시 응답 형식
     * @param <T>
     */
    @Getter
    public  static class ApiErrorResult<T> {

        private final String result;
        private final int errorCode;
        private final T message;

        public ApiErrorResult(String result, int errorCode, T message) {
            this.result = result;
            this.errorCode = errorCode;
            this.message = message;
        }
    }
}
