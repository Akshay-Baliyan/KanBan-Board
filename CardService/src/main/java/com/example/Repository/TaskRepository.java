package com.example.Repository;

import com.example.Domain.Status;
import com.example.Domain.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
    @Query("{'assignedEmployees._id': ?0}")
    List<Task> findByAssignedEmployees(String userId);
    @Query("{'assignedEmployees._id': ?0, 'status': ?1}")
    Long countByAssignedEmployeesAndStatus(String userId, Status status);
}
