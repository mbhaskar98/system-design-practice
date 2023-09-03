package org.bhaskar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBuilder<T> implements ResponseInterface<T> {
    private HttpStatus httpStatus;
    private Response.status responeStatus;
    private T data;
    private String message;

    public static <E> ResponseBuilder.ResponseBuilderBuilder<E> getErrorResponseBuilder() {
        return ResponseBuilder
                .<E>builder()
                .responeStatus(Response.status.ERROR);
    }

    public static <E> ResponseBuilder.ResponseBuilderBuilder<E> getSuccessResponseBuilder() {
        return ResponseBuilder
                .<E>builder()
                .httpStatus(HttpStatus.OK)
                .responeStatus(Response.status.SUCCESS);
    }

    public ResponseEntity<Response<T>> getResponse() {
        Response<T> response = Response
                .<T>builder()
                .data(data)
                .status(responeStatus)
                .message(message)
                .build();
        return ResponseEntity.status(httpStatus).body(response);
    }
}
