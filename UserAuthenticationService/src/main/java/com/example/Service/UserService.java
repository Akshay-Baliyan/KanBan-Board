package com.example.Service;

import com.example.Domain.User;
import com.example.Exception.InvalidCredentialsException;
import com.example.Exception.UserAlreadyExists;

public interface UserService {
    User saveUser(User user)throws UserAlreadyExists;
    User getUserByIdAndPassword(String userId, String userPassword)throws InvalidCredentialsException;
}
