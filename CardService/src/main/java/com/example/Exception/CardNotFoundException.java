//package com.example.Exception;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Card Not Found")
//public class CardNotFoundException extends Exception {
//}
package com.example.Exception;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException() {
        super("Card not found");
    }
}
