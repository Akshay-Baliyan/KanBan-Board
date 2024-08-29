package com.example.Exception;

public class RemoveTaskException extends RuntimeException {
    public RemoveTaskException() {
        super("Task details are not needed while creating the card");
    }
}
