//package com.example.Exception;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "User Not Found")
//public class UserNotFoundException extends Exception {
//}
package com.example.Exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("User not found");
    }
}