package com.example.Service;

import com.example.Domain.Card;
import com.example.Domain.MoveTaskRequest;
import com.example.Domain.Task;
import com.example.Domain.User;
import com.example.Exception.*;

import java.util.List;

public interface TaskService {
    List<User> getAllUsers();
    public List<User> getUsersWithRoleNotAdmin();
    User registerManager(User user) throws UserAlreadyExistException, InvalidUser;
    User registerUser(String managerId,User user) throws UserAlreadyExistException, InvalidUser;
    Card createCard(Card card, String managerId)throws InvalidUser, RemoveTaskException, CardNotFoundException;//for manager //check card already existing and add task to it.
    Card addTaskToCard(String cardId, Task task, String managerId) throws InvalidUser, EmployeeNotAssigned, UserNotFoundException, CardNotFoundException, TaskAlreadyExistException, TaskOverloadException, TaskNotFoundException;
    public Task editTask(String cardId, Task updatedTask) throws TaskNotFoundException, UserNotFoundException, TaskOverloadException, CardNotFoundException;
    public void deleteTask(String cardId, String taskId,String managerId) throws TaskNotFoundException, CardNotFoundException, InvalidUser;
    List<Task> getTaskByCardId(String cardId,User user) throws CardNotFoundException, TaskNotFoundException;
    List<Task> getTaskByAssignedEmployees(String userId)throws UserNotFoundException, TaskNotFoundException, CardNotFoundException;
    public void moveTask(String fromCardId, String toCardId, String taskId) throws TaskOverloadException, CardNotFoundException, TaskNotFoundException, EmployeeNotAssigned;





}
