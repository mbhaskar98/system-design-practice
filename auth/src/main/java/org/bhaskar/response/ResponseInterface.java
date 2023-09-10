package org.bhaskar;

import org.bhaskar.dto.Response;
import org.springframework.http.ResponseEntity;

public interface ResponseInterface<T> {
    ResponseEntity<Response<T>> getResponse();
}
