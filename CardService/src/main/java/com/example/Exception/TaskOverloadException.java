//package com.example.Exception;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//@ResponseStatus(value = HttpStatus.CONFLICT, reason = "The employee has reached the maximum number of work in progress tasks.")
//
//public class TaskOverloadException extends Exception{
//}
//package com.example.Exception;
//
//-------------------------------------------
//public class TaskOverloadException extends RuntimeException {
//    public TaskOverloadException() {
//        super("The employee has reached the maximum number of work in progress tasks.");
//    }
//}



package com.example.Exception;

public class TaskOverloadException extends RuntimeException {
    // Constructor with a custom message
    public TaskOverloadException(String message) {
        super(message);
    }

    // Default constructor with a predefined message
    public TaskOverloadException() {
        super("The employee has reached the maximum number of work in progress tasks.");
    }
}
