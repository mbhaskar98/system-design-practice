package org.bhaskar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record Response<T>(
        @Nonnull Response.status status,
        String message,
        @Nullable T data
) {
    public enum status {
        ERROR,
        SUCCESS,
    }
}
