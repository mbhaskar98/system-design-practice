package org.bhaskar.response;

import org.bhaskar.response.dto.Response;
import org.springframework.http.ResponseEntity;

public interface ResponseInterface<T> {
    ResponseEntity<Response<T>> getResponse();
}
