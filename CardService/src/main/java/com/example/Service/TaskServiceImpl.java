package com.example.Service;

import com.example.Domain.*;
import com.example.Exception.*;

import com.example.Proxy.CardProxy;
import com.example.Repository.TaskRepository;
import com.example.Repository.UserCardRepository;
import com.example.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class TaskServiceImpl implements TaskService {
    private final UserRepository userRepository;
    private final UserCardRepository userCardRepository;
    private final TaskRepository taskRepository;
    private CardProxy cardProxy;

    private final String managerId = "akshay@12.co";
    private final String card1 = "ON_HOLD";
    private final String card2 = "To Do";
    private final String card3 = "In Progress";
    private final String card4 = "REVIEWED";
    private final String card5 = "Done";

    @Autowired
    public TaskServiceImpl(UserRepository userRepository, UserCardRepository userCardRepository, TaskRepository taskRepository, CardProxy cardProxy) {
        this.userRepository = userRepository;
        this.userCardRepository = userCardRepository;
        this.taskRepository = taskRepository;
        this.cardProxy = cardProxy;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User registerManager(User user) throws UserAlreadyExistException, InvalidUser {
        if (user.getUserId() != null && this.managerId.equals(user.getUserId())) {
            Optional<User> foundUser = userRepository.findById(user.getUserId());
            if (foundUser.isPresent()) {
                System.out.println("User found: " + foundUser.get().getUserId());
                throw new UserAlreadyExistException();

            } else {
                System.out.println("User not found.");
            }

            cardProxy.saveUser(user);
            return userRepository.save(user);

        } else {
            throw new InvalidUser();
        }
    }

    @Override
    public User registerUser(String managerId, User user) throws UserAlreadyExistException, InvalidUser {
        if (managerId != null && this.managerId.equals(managerId)) {
            Optional<User> foundUser = userRepository.findById(user.getUserId());
            if (foundUser.isPresent()) {
                System.out.println("User found: " + foundUser.get().getUserId());
                throw new UserAlreadyExistException();

            } else {
                System.out.println("User not found.");
            }


            cardProxy.saveUser(user);
            return userRepository.save(user);


        } else {
            throw new InvalidUser();
        }
    }


    @Override
    public Card createCard(Card card, String managerId) throws InvalidUser, CardNotFoundException, RemoveTaskException, CardAlreadyExistException {
        if (this.managerId.equals(managerId)) {
            // Check if the provided cardId matches one of the predefined card values
            if (card.getCardId() != null && isValidCardId(card.getCardId())) {
                // Check if tasks are provided, throw RemoveTaskException if true
                if (card.getTasks() != null && !card.getTasks().isEmpty()) {
                    throw new RemoveTaskException();
                }

                // Check if the card already exists in the repository
                Optional<Card> foundCard = userCardRepository.findById(card.getCardId());
                if (foundCard.isPresent()) {
                    throw new CardAlreadyExistException();
                }

                // Save the card to the repository if it's a new card and the cardId is valid
                return userCardRepository.save(card);
            } else {
                // If the cardId is invalid, throw a CardNotFoundException
                throw new CardNotFoundException();
            }
        } else {
            // If the managerId does not match, throw an InvalidUser exception
            throw new InvalidUser();
        }
    }


    // Helper method to validate the cardId
    private boolean isValidCardId(String cardId) {
        return card1.equals(cardId) || card2.equals(cardId) || card3.equals(cardId) || card4.equals(cardId) || card5.equals(cardId);
    }
//......................used stream() little bit complex---------------------------------

//    @Override
//    public Card addTaskToCard(String cardId, Task task, String managerId) throws EmployeeNotAssigned, UserNotFoundException, TaskAlreadyExistException, CardNotFoundException, TaskNotFoundException, TaskOverloadException, InvalidUser {
//
//        if (this.managerId.equals(managerId)) {
//            Card card = userCardRepository.findById(cardId)
//                    .orElseThrow(() -> new CardNotFoundException());
////
//
//            List<User> assignees = task.getAssignedEmployees();
//            if (assignees == null) {
//                throw new EmployeeNotAssigned();
//            }
//            for (User assignee : assignees) {
//                if (!userRepository.existsById(assignee.getUserId())) {
//                    throw new UserNotFoundException();
//                }
//                long inProgressTaskCount = card.getTasks().stream()
//                        .filter(t -> t.getStatus() == Status.IN_PROGRESS)
//                        .flatMap(t -> Optional.ofNullable(t.getAssignedEmployees()).orElse(List.of()).stream())
//                        .filter(u -> u.getUserId().equals(assignee.getUserId()))
//                        .count();
//
//
//
//
//                if (task.getStatus() == Status.IN_PROGRESS && inProgressTaskCount >= 3) {
//                    throw new TaskOverloadException();
//                }
//            }
//
//            //check if card exist to which the task is added
//            Optional<Card> foundCard = userCardRepository.findById(cardId);
//            if (foundCard.isEmpty()) {
//                throw new CardNotFoundException();
//            }
//
//            Card fnCard = foundCard.get();
//
//            //check task list in the card
//            List<Task> taskList = fnCard.getTasks();
//
//            if (taskList == null) {
//                taskList = new ArrayList<>();
//                fnCard.setTasks(taskList);
//            }
//
//            //Check task exist in the card or not
//            for (Task task1 : taskList) {
//                if (task1.getTaskId().equals(task.getTaskId())) {
//                    throw new TaskAlreadyExistException();
//                }
//            }
//            taskRepository.save(task);
//            fnCard.getTasks().add(task);
//            userCardRepository.save(fnCard);
//
//            return fnCard;
//        }else {
//            throw new InvalidUser();
//        }
//
//    }


    //-------------------normal if else for better understanding which satisfies all conditions--------
    @Override
    public Card addTaskToCard(String cardId, Task task, String managerId) throws EmployeeNotAssigned, UserNotFoundException, TaskAlreadyExistException, CardNotFoundException, TaskNotFoundException, TaskOverloadException, InvalidUser {
        // Checking whether the user is the manager using managerId
        if (this.managerId.equals(managerId)) {
            // Check whether cardId is present in userCardRepository
            Optional<Card> optionalCard = userCardRepository.findById(cardId);
            if (optionalCard.isPresent()) {
                // If card exists, get the card
                Card card = optionalCard.get();

                // Ensure the tasks list is initialized
                List<Task> taskList = card.getTasks();
                if (taskList == null) {
                    taskList = new ArrayList<>();
                    card.setTasks(taskList);
                }

                // Check for the employees assigned to the task and get them
                List<User> assignees = task.getAssignedEmployees();
                // It should not be null; the task should be assigned to someone
                if (assignees != null) {
                    List<String> ineligibleUsers = new ArrayList<>();

                    // Check whether the assigned employee is a valid employee in userRepository
                    for (User assignee : assignees) {
                        if (userRepository.existsById(assignee.getUserId())) {
                            // If valid employee, check how many tasks that employee has
                            long inProgressTaskCount = 0;
                            for (Task existingTask : taskList) {
                                if (existingTask.getStatus() == Status.IN_PROGRESS) {
                                    List<User> assignedUsers = existingTask.getAssignedEmployees();
                                    if (assignedUsers != null) {
                                        for (User assignedUser : assignedUsers) {
                                            // Employees in task card == given employee
                                            if (assignedUser.getUserId().equals(assignee.getUserId())) {
                                                inProgressTaskCount++;
                                            }
                                        }
                                    }
                                }
                            }

                            if (task.getStatus() == Status.IN_PROGRESS && inProgressTaskCount >= 3) {
                                ineligibleUsers.add(assignee.getUserName() + " (ID: " + assignee.getUserId() + ")");
                            }
                        } else {
                            throw new UserNotFoundException();
                        }
                    }

                    // If there are ineligible users, throw an exception and inform the manager
                    if (!ineligibleUsers.isEmpty()) {
                        throw new TaskOverloadException("Task cannot be assigned to the following user(s) due to overload: " + String.join(", ", ineligibleUsers));
                    }

                } else {
                    throw new EmployeeNotAssigned();
                }

                // Check if the task already exists in the card
                for (Task existingTask : taskList) {
                    if (existingTask.getTaskId().equals(task.getTaskId())) {
                        throw new TaskAlreadyExistException();
                    }
                }

                // Add the new task to the card
                taskRepository.save(task);
                card.getTasks().add(task);
                userCardRepository.save(card);

                return card;

            } else {
                throw new CardNotFoundException();
            }
        } else {
            throw new InvalidUser();
        }
    }


    @Override
    public Task editTask(String cardId, Task updatedTask)
            throws TaskNotFoundException, UserNotFoundException, TaskOverloadException, CardNotFoundException {

        // Find the card by its ID
        Optional<Card> optionalCard = userCardRepository.findById(cardId);
        if (!optionalCard.isPresent()) {
            throw new CardNotFoundException();
        }
        Card card = optionalCard.get();

        // Validate assignees before looking for the task
        List<User> assignees = updatedTask.getAssignedEmployees();
        if (assignees != null) {
            List<String> ineligibleUsers = new ArrayList<>();

            for (User assignee : assignees) {
                if (!userRepository.existsById(assignee.getUserId())) {
                    throw new UserNotFoundException();
                }

                long inProgressTaskCount = 0;
                for (Task t : card.getTasks()) {
                    if (t.getStatus() == Status.IN_PROGRESS) {
                        List<User> assignedUsers = t.getAssignedEmployees();
                        if (assignedUsers != null) {
                            for (User u : assignedUsers) {
                                if (u.getUserId().equals(assignee.getUserId())) {
                                    inProgressTaskCount++;
                                }
                            }
                        }
                    }
                }

                if (updatedTask.getStatus() == Status.IN_PROGRESS && inProgressTaskCount >= 3) {
                    ineligibleUsers.add(assignee.getUserName() + " (ID: " + assignee.getUserId() + ")");
                }
            }

            // If there are ineligible users, throw an exception and inform the manager
            if (!ineligibleUsers.isEmpty()) {
                throw new TaskOverloadException("Task cannot be assigned to the following user(s) due to overload: " + String.join(", ", ineligibleUsers));
            }
        }

        // Find the task in the card's task list
        Task task = null;
        for (Task t : card.getTasks()) {
            if (t.getTaskId().equals(updatedTask.getTaskId())) {
                task = t;
                break;
            }
        }
        if (task == null) {
            throw new TaskNotFoundException();
        }

        // Update task attributes
        if (updatedTask.getTaskName() != null) {
            task.setTaskName(updatedTask.getTaskName());
        }
        if (updatedTask.getPriority() != null) {
            task.setPriority(updatedTask.getPriority());
        }
        if (updatedTask.getStatus() != null) {
            task.setStatus(updatedTask.getStatus());
        }
        if (updatedTask.getAssignedEmployees() != null) {
            task.setAssignedEmployees(updatedTask.getAssignedEmployees());
        }

        // Save the updated card
        userCardRepository.save(card);

        return task;
    }


    @Override
    public void deleteTask(String cardId, String taskId, String managerId)
            throws TaskNotFoundException, CardNotFoundException, InvalidUser {

        if (this.managerId.equals(managerId)) {
            // Find the card by its ID
            Optional<Card> optionalCard = userCardRepository.findById(cardId);
            if (optionalCard.isPresent()) {
                Card card = optionalCard.get();

                // Find the task in the card's task list
                Task taskToDelete = null;
                for (Task t : card.getTasks()) {
                    if (t.getTaskId().equals(taskId)) {
                        taskToDelete = t;
                        break;
                    }
                }

                // If the task is found, remove it from the card's task list
                if (taskToDelete != null) {
                    card.getTasks().remove(taskToDelete);

                    // Save the updated card
                    userCardRepository.save(card);
                } else {
                    throw new TaskNotFoundException();
                }
            } else {
                throw new CardNotFoundException();
            }
        } else {
            throw new InvalidUser();
        }
    }


    @Override
    public List<Task> getTaskByCardId(String cardId,User user ) throws CardNotFoundException, TaskNotFoundException {
        System.out.println("in service getTask");
        Optional<Card> foundCardOpt = userCardRepository.findById(cardId);
        if (foundCardOpt.isEmpty()) {
            throw new CardNotFoundException();
        }
        System.out.println("in service getTask 1");
        Card foundCard = foundCardOpt.get();
        List<Task> tasks = foundCard.getTasks();
        if (tasks == null || tasks.isEmpty()) {
            throw new TaskNotFoundException();
        }
        System.out.println("in service getTask 2");
        if(user.getUserRole().equalsIgnoreCase("user")){
            List<Task> filteredTasks=null;
            for(Task task : tasks){
                System.out.println("in service getTask 3");
                if(task.getAssignedEmployees().contains(user)){
                    filteredTasks.add(task);
                }
            }
            System.out.println(filteredTasks);
            return filteredTasks;
        }
        //if admin then return all tasks in the card
        //if user then check list of assignees and only add task where user is present to the list of tasks and return the list
        System.out.println(tasks);
        return tasks;
    }

    @Override
    public List<Task> getTaskByAssignedEmployees(String userId) throws UserNotFoundException, TaskNotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException();
        }

        // Retrieve the list of cards that contain tasks assigned to the user
        List<Card> cards = userCardRepository.findByAssignedEmployees_UserId(userId);
        List<Task> tasks = new ArrayList<>();

        // Iterate through the cards
        for (Card card : cards) {
            // Iterate through the tasks in each card
            for (Task task : card.getTasks()) {
                // Check if the task is assigned to the user with the given userId
                for (User assignedUser : task.getAssignedEmployees()) {
                    if (assignedUser.getUserId().equals(userId)) {
                        tasks.add(task);
                        break; // Break the loop once the user is found in the task
                    }
                }
            }
        }

        if (tasks.isEmpty()) {
            throw new TaskNotFoundException();
        }

        return tasks;
    }
    public void moveTask(MoveTaskRequest moveTaskRequest) throws TaskOverloadException, CardNotFoundException, TaskNotFoundException {
        // Retrieve the "from" card
        Optional<Card> fromCardOptional = userCardRepository.findById(moveTaskRequest.getFromCardId());
        Card fromCard;
        if (fromCardOptional.isPresent()) {
            fromCard = fromCardOptional.get();
            System.out.println("From card before removal: " + fromCard);
        } else {
            // Handle the case where the "from" card doesn't exist
            throw new CardNotFoundException();
        }

        // Retrieve the "to" card
        Optional<Card> toCardOptional = userCardRepository.findById(moveTaskRequest.getToCardId());
        Card toCard;
        if (toCardOptional.isPresent()) {
            toCard = toCardOptional.get();
            System.out.println("To card before adding: " + toCard);
        } else {
            // Handle the case where the "to" card doesn't exist
            throw new CardNotFoundException();
        }

        // Find the task in the "from" card
        Task taskToMove = null;
        for (Task task : fromCard.getTasks()) {
            if (task.getTaskId().equals(moveTaskRequest.getTaskId())) {
                taskToMove = task;
                System.out.println("Task to move (before status change): " + taskToMove);
                break;
            }
        }

        if (taskToMove == null) {
            // Handle the case where the task isn't found in the "from" card
            throw new TaskNotFoundException();
        }

        // Update the task's status to match the "to" card ID
        taskToMove.setStatus(Status.valueOf(moveTaskRequest.getToCardId()));
        System.out.println("Task to move (after status change): " + taskToMove);

        // Remove the task from the "from" card
        fromCard.getTasks().remove(taskToMove);
        System.out.println("From card after removal: " + fromCard);
        if (!fromCard.getTasks().contains(taskToMove)) {
            System.out.println("Task successfully removed from 'fromCard'");
        } else {
            System.out.println("Task removal failed from 'fromCard'");
        }

        // Add the task to the "to" card
        toCard.getTasks().add(taskToMove);
        System.out.println("To card after adding: " + toCard);
        if (toCard.getTasks().contains(taskToMove)) {
            System.out.println("Task successfully added to 'toCard'");
        } else {
            System.out.println("Task addition failed to 'toCard'");
        }

        // Save the updated "from" card
        userCardRepository.save(fromCard);
        Card savedFromCard = userCardRepository.findById(moveTaskRequest.getFromCardId()).orElse(null);
        System.out.println("From card after save: " + savedFromCard);
        if (savedFromCard != null && !savedFromCard.getTasks().contains(taskToMove)) {
            System.out.println("Task is no longer in 'fromCard' after save");
        } else {
            System.out.println("Task is still in 'fromCard' after save or save operation failed");
        }

        // Save the updated "to" card
        userCardRepository.save(toCard);
        Card savedToCard = userCardRepository.findById(moveTaskRequest.getToCardId()).orElse(null);
        System.out.println("To card after save: " + savedToCard);
        if (savedToCard != null && savedToCard.getTasks().contains(taskToMove)) {
            System.out.println("Task successfully moved to 'toCard' after save");
        } else {
            System.out.println("Task is not in 'toCard' after save or save operation failed");
        }
    }


}




