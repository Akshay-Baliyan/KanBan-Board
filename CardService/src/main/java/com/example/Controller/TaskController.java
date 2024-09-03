package com.example.Controller;

import com.example.Domain.Card;
import com.example.Domain.MoveTaskRequest;
import com.example.Domain.Task;
import com.example.Domain.User;
import com.example.Exception.*;
import com.example.Service.TaskService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v2")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/registerManager")//-------working------
    public ResponseEntity registerManager(@RequestBody User user) throws UserAlreadyExistException {
        try {
            User createdUser = taskService.registerManager(user);
            return new ResponseEntity<>("Manager Created", HttpStatus.CREATED);
        } catch (UserAlreadyExistException e) {
            throw e;
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/user/register")//-----working------
    public ResponseEntity<?> registerUser(HttpServletRequest request, @RequestBody User user) throws UserAlreadyExistException {
        try {
            String userId = getUserId(request);
            User createdUser = taskService.registerUser(userId,user);
            return new ResponseEntity<>("User Created", HttpStatus.CREATED);
        } catch (UserAlreadyExistException e) {
            throw e;
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/nonAdminUsers")
    public List<User> getUsersWithRoleNotAdmin() {
        return taskService.getUsersWithRoleNotAdmin();
    }

    @PostMapping("/user/{cardId}/addTask")//----working----
    public ResponseEntity<?> addTask(HttpServletRequest request, @PathVariable String cardId, @RequestBody Task task) throws UserNotFoundException, TaskAlreadyExistException, CardNotFoundException, TaskOverloadException {
        try {
            // Optional: Extract user role or ID from the request if needed for additional logic
             String userId = getUserId(request);
            // String userRole = getUserRole(request);

            Card updatedCard = taskService.addTaskToCard(cardId, task, userId);
            return new ResponseEntity<>("Task added", HttpStatus.CREATED);
        } catch (UserNotFoundException|CardNotFoundException|TaskAlreadyExistException|TaskOverloadException e) {
throw e;
        }  catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/user/card")//----working-----
    public ResponseEntity<?> addCard(@RequestBody Card card, HttpServletRequest request)throws CardNotFoundException, CardAlreadyExistException {
        try {
            String userId = getUserId(request);

            Card createdCard = taskService.createCard(card, userId);
            return new ResponseEntity<>("Card Created", HttpStatus.CREATED);
        }
        catch(CardAlreadyExistException|CardNotFoundException e){
            throw e;
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //@PutMapping("/user/{taskId}")
    @PutMapping("user/{cardId}/tasks")
    public ResponseEntity<?> editTask(HttpServletRequest request, @PathVariable String cardId, @RequestBody Task task) throws TaskNotFoundException, UserNotFoundException, TaskOverloadException {
        try {
            System.out.println("testing");
            //task.setTaskId(taskId);
            String userId = getUserId(request);
            System.out.println("cardId :::: "+cardId);
            System.out.println("task to update ::: "+ task);
            Task updatedTask=taskService.editTask(cardId,task);
            //Task updatedTask = taskService.editTask(task, userId);
            System.out.println("again testing");
            return new ResponseEntity<>(updatedTask, HttpStatus.OK);
        } catch (TaskNotFoundException| UserNotFoundException|TaskOverloadException e) {
        throw e;
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/user/{cardId}/tasks/{taskId}")
    public ResponseEntity<?> deleteTaskForUser(@PathVariable String cardId,@PathVariable String taskId, HttpServletRequest request) throws TaskNotFoundException, CardNotFoundException {
        try {
            String userId = getUserId(request);

            taskService.deleteTask(cardId,taskId, userId);
            return new ResponseEntity<>("Deleted", HttpStatus.NO_CONTENT);
        } catch (TaskNotFoundException |CardNotFoundException e) {
throw e;
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/card/{cardId}")//----working--------
    public ResponseEntity<List<Task>> getTasksByCardId(HttpServletRequest request,@PathVariable String cardId)throws CardNotFoundException, TaskNotFoundException {
        try {
            System.out.println("in getTask");
            User user=new User();
            System.out.println(getUserId(request));
            System.out.println(getUserRole(request));
            user.setUserId(getUserId(request));
            user.setUserRole(getUserRole(request));
            System.out.println(user);
            List<Task> tasks = taskService.getTaskByCardId(cardId,user);
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (CardNotFoundException|TaskNotFoundException e) {
throw e;
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/assigned/{userId}")//--------working----
    public ResponseEntity<List<Task>> getTasksByAssignedEmployees(@PathVariable String userId)throws UserNotFoundException, CardNotFoundException, TaskNotFoundException {
        try {
            List<Task> tasks = taskService.getTaskByAssignedEmployees(userId);
            return new ResponseEntity<>(tasks, HttpStatus.OK);
        } catch (UserNotFoundException|CardNotFoundException|TaskNotFoundException e) {
throw e;
        }  catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/user/moveTask")
    public ResponseEntity<Map<String, String>> moveTask(@RequestBody MoveTaskRequest moveTaskRequest)throws CardNotFoundException, TaskNotFoundException, TaskOverloadException {
        try {

            taskService.moveTask(
                    moveTaskRequest.getFromCardId(),
                    moveTaskRequest.getToCardId(),
                    moveTaskRequest.getTaskId()
            );
            Map<String, String> response = new HashMap<>();
            response.put("message", "Task moved successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (CardNotFoundException | TaskNotFoundException | TaskOverloadException e) {
            throw e;
        } catch (Exception e) {
            // Return a JSON response with an error message
            Map<String, String> response = new HashMap<>();
            response.put("message", "An error occurred");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private Claims getClaims(HttpServletRequest request) {
        Claims claims = (Claims) request.getAttribute("claims");
        if (claims != null) {
            return claims;
        }
        throw new RuntimeException("Claims not found in request");
    }

    private String getUserId(HttpServletRequest request) {
        Claims claims = getClaims(request);
        return claims.get("userId", String.class);
    }

    private String getUserRole(HttpServletRequest request) {
        Claims claims = getClaims(request);
        return claims.get("userRole", String.class);
    }
}
