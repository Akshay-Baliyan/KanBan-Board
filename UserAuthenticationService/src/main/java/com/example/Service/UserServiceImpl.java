package com.example.Service;

import com.example.Domain.User;
import com.example.Exception.InvalidCredentialsException;
import com.example.Exception.UserAlreadyExists;
import com.example.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User saveUser(User user) throws UserAlreadyExists {
        if (userRepository.findById(user.getUserId()).isPresent()) {
            throw new UserAlreadyExists();
        }
        return userRepository.save(user);
    }

    @Override
    public User getUserByIdAndPassword(String userId, String userPassword) throws InvalidCredentialsException {

        User loggedInUser = userRepository.findByUserIdAndUserPassword(userId, userPassword);

        if (loggedInUser == null) {
            throw new InvalidCredentialsException();
        }
        return loggedInUser;
    }
}
