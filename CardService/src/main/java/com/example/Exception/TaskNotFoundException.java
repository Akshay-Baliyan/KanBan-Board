//package com.example.Exception;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Task Not Found")
//public class TaskNotFoundException extends Exception {
//}
package com.example.Exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException() {
        super("Task Not Found");
    }
}
