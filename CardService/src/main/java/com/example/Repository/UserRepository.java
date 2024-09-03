package com.example.Repository;

import com.example.Domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    @Query("{ 'userRole': { $ne: 'admin' } }")
    List<User> findAllByUserRoleNotAdmin();
}
