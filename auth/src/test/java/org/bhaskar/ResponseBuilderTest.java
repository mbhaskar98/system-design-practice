package org.bhaskar;

import org.bhaskar.dto.Response;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class ResponseBuilderTest {
    String data;
    String message;
    HttpStatus httpStatus;
    Response.status status;
    ResponseEntity<Response<String>> actual;
    ResponseEntity<Response<String>> expectedResponseEntity;
    ResponseBuilder.ResponseBuilderBuilder<String> responseBuilder;

    @BeforeEach()
    void getErrorResponseBuilderSetup() {
        Response<String> expectedResponse = new Response<>(
                status,
                message,
                data
        );
        expectedResponseEntity = ResponseEntity
                .status(httpStatus)
                .body(expectedResponse);
    }

    @Nested
    class GetErrorResponseBuilderTests {
        GetErrorResponseBuilderTests() {
            message = "error";
            httpStatus = HttpStatus.UNAUTHORIZED;
            data = null;
            status = Response.status.ERROR;
        }

        @BeforeEach
        void buildResponseBuilder() {
            responseBuilder = ResponseBuilder.getErrorResponseBuilder();
            responseBuilder
                    .httpStatus(httpStatus)
                    .message(message)
                    .data(data)
                    .responeStatus(status)
                    .httpStatus(httpStatus);
        }

        @Test
        @DisplayName("Basic error response test")
        void basicErrorTest() {
            ResponseBuilder<String> builder = responseBuilder
                    .build();

            Assertions.assertEquals(data, builder.getData());
            Assertions.assertEquals(httpStatus, builder.getHttpStatus());
            Assertions.assertEquals(status, builder.getResponeStatus());
            Assertions.assertEquals(message, builder.getMessage());
            Assertions.assertEquals(expectedResponseEntity, builder.getResponse());
        }

        @Test
        @DisplayName("Negative case to test some value change")
        void someValueChangeTest() {
            actual = responseBuilder
                    .message("messageeee")
                    .build()
                    .getResponse();

            Assertions.assertNotEquals(expectedResponseEntity, actual);


            actual = responseBuilder
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .build()
                    .getResponse();

            Assertions.assertNotEquals(expectedResponseEntity, actual);


            actual = responseBuilder
                    .data("data")
                    .build()
                    .getResponse();

            Assertions.assertNotEquals(expectedResponseEntity, actual);

        }

        @Nested
        class ErrorsWithData {
            ErrorsWithData() {
                data = "some data";
            }

            @Test
            @DisplayName("Basic error response with some data test")
            void basicErrorWithDataTest() {
                actual = responseBuilder
                        .build()
                        .getResponse();
                Assertions.assertEquals(expectedResponseEntity, actual);
            }
        }

    }


    @Nested
    class GetSuccessResponseBuilderTests {
        GetSuccessResponseBuilderTests() {
            message = "";
            httpStatus = HttpStatus.OK;
            data = "data";
            status = Response.status.SUCCESS;
        }

        @BeforeEach
        void buildResponseBuilder() {
            responseBuilder = ResponseBuilder.getSuccessResponseBuilder();
            responseBuilder.data(data);
        }

        @Test
        @DisplayName("Basic success response test")
        void basicSuccessTest() {
            ResponseBuilder<String> builder = responseBuilder
                    .build();

            Assertions.assertEquals(data, builder.getData());
            Assertions.assertEquals(httpStatus, builder.getHttpStatus());
            Assertions.assertEquals(status, builder.getResponeStatus());
            Assertions.assertEquals(message, builder.getMessage());
            Assertions.assertEquals(expectedResponseEntity, builder.getResponse());

        }
    }
}