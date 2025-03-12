package org.example.spring_homework001.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> {
    private boolean success;
    private String message;
    private HttpStatus status;
    private T payload;
    private Timestamp timestamp;


    public Response(boolean success, String message, HttpStatus status, T payload) {
        this.success = success;
        this.message = message;
        this.status = status;
        this.payload = payload;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }


    public Response(boolean success, String message, HttpStatus status) {
        this.success = success;
        this.message = message;
        this.status = status;
        this.payload = null;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }
}