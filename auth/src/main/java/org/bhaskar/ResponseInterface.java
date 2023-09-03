package org.bhaskar.dto;

import org.springframework.http.ResponseEntity;

public interface ResponseInterface<T> {
    ResponseEntity<Response<T>> getResponse();
}
