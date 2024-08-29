//package com.example.Exception;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Task Already Exists")
//public class TaskAlreadyExistException extends Exception {
//}
package com.example.Exception;

public class TaskAlreadyExistException extends RuntimeException {
    public TaskAlreadyExistException() {
        super("Task Already Exists");
    }
}
