package com.example.Repository;

import com.example.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUserIdAndUserPassword(String userId, String userPassword);
}
