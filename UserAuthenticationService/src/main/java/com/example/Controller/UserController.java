package com.example.Controller;

import com.example.Domain.User;
import com.example.Exception.InvalidCredentialsException;
import com.example.Exception.UserAlreadyExists;
import com.example.Service.SecurityTokenGenerator;
import com.example.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private UserService userService;
    private ResponseEntity responseEntity;
    private SecurityTokenGenerator securityTokenGenerator;

    @Autowired
    public UserController(UserService userService, SecurityTokenGenerator securityTokenGenerator) {
        this.userService = userService;
        this.securityTokenGenerator = securityTokenGenerator;
    }

    @PostMapping("/saveUser")
    public ResponseEntity savingUser(@RequestBody User user)throws UserAlreadyExists{
        try{
            return responseEntity = new ResponseEntity(userService.saveUser(user), HttpStatus.CREATED);
        }
        catch(UserAlreadyExists e){
            throw e;
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) throws InvalidCredentialsException
    {
        // Generate the token on login,
        // return 200 status if customer is logged in  else 500 status
        try {
            User loggedIn = userService.getUserByIdAndPassword(user.getUserId(),user.getUserPassword());
            Map<String, String> token = securityTokenGenerator.generateToken(loggedIn);
            System.out.println(token);
            return responseEntity = new ResponseEntity<>(token, HttpStatus.OK);
        }
        catch (InvalidCredentialsException e) {
            throw e;
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}
