package com.example.Exception;

public class InvalidUser extends RuntimeException {
    public InvalidUser() {
        super("Invalid User");
    }
}
