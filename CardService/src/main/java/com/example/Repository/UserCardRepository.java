package com.example.Repository;

import com.example.Domain.Card;
import com.example.Domain.Status;
import com.example.Domain.Task;
import com.example.Exception.CardNotFoundException;
import com.example.Exception.TaskNotFoundException;
import com.example.Exception.UserNotFoundException;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCardRepository extends MongoRepository<Card, Status> {
    @Query("{ 'tasks.assignedEmployees.userId': ?0 }")
    List<Card> findByAssignedEmployees_UserId(String userId);


    @Query("{ 'tasks.assignedEmployees.userId': ?0, 'tasks.status': ?1 }")
    int countByAssignedEmployees_UserIdAndTasks_Status(String userId, Status status);



}
