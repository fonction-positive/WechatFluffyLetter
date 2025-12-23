package com.fluffyletter.dto;

public class ApiResponse {

    private boolean success;

    public ApiResponse() {
    }

    public ApiResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public static ApiResponse ok() {
        return new ApiResponse(true);
    }
}
