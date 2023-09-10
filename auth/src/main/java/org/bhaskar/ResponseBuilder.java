package org.bhaskar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.bhaskar.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Builder
@AllArgsConstructor
public class ResponseBuilder<T> implements ResponseInterface<T> {
    @Getter
    private HttpStatus httpStatus;
    @Getter
    private Response.status responeStatus = Response.status.SUCCESS;
    @Getter
    private T data = null;
    @Getter
    private String message = "";

    public static <E> ResponseBuilder.ResponseBuilderBuilder<E> getErrorResponseBuilder() {
        return ResponseBuilder
                .<E>builder()
                .responeStatus(Response.status.ERROR);
    }

    public static <E> ResponseBuilder.ResponseBuilderBuilder<E> getSuccessResponseBuilder() {
        return ResponseBuilder
                .<E>builder()
                .httpStatus(HttpStatus.OK)
                .message("")
                .responeStatus(Response.status.SUCCESS);
    }

    public ResponseEntity<Response<T>> getResponse() {
        Response<T> response = new Response<>(responeStatus, message, data);
        return ResponseEntity.status(httpStatus).body(response);
    }
}
