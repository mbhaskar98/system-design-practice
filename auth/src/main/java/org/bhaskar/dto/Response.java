package org.bhaskar.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;


@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Response<T>(
        @Getter Response.status status,
        @Getter String message,
        @Getter @Nullable T data
) {
    public enum status {
        ERROR,
        SUCCESS,
    }
}
