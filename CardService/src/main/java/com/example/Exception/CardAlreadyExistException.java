//package com.example.Exception;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Card Already Exists")
//
//public class CardAlreadyExistException extends Exception{
//}
package com.example.Exception;

public class CardAlreadyExistException extends RuntimeException {
    public CardAlreadyExistException() {
        super("Card already exist");
    }
}