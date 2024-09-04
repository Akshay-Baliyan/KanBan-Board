package com.example.Service;

import com.example.Domain.*;
import com.example.Exception.*;

import com.example.Proxy.CardProxy;
import com.example.Repository.TaskRepository;
import com.example.Repository.UserCardRepository;
import com.example.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskServiceImpl implements TaskService {
    private final UserRepository userRepository;
    private final UserCardRepository userCardRepository;
    private final TaskRepository taskRepository;
    private CardProxy cardProxy;

    private final String managerId = "akshay@12.co";
    private final String card1 = "ON_HOLD";
    private final String card2 = "To_Do";
    private final String card3 = "In_Progress";
    private final String card4 = "REVIEWED";
    private final String card5 = "Done";

    @Autowired
    public TaskServiceImpl(UserRepository userRepository, UserCardRepository userCardRepository, TaskRepository taskRepository, CardProxy cardProxy) {
        this.userRepository = userRepository;
        this.userCardRepository = userCardRepository;
        this.taskRepository = taskRepository;
        this.cardProxy = cardProxy;
        initializeCards();
        initializeAdminUser();
    }
    private void initializeAdminUser() {
        Optional<User> existingUser = userRepository.findById(managerId);
        if (existingUser.isEmpty()) {
            User adminUser = new User();
            adminUser.setUserId(managerId);
            adminUser.setUserName("Admin");
            adminUser.setUserRole("admin");
            adminUser.setUserPassword("password123");
            userRepository.save(adminUser);
        }
    }

    @Override
    public List<User> getUsersWithRoleNotAdmin() {
        return userRepository.findAllByUserRoleNotAdmin();
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
                user.setUserRole("user");
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
            System.out.println("Card ID : "+card);
            if (card.getCardId() != null && isValidCardId(card.getCardId())) {
                // Check if tasks are provided, throw RemoveTaskException if true

                if (card.getTasks() != null && !card.getTasks().isEmpty()) {
                    throw new RemoveTaskException();
                }

                // Check if the card already exists in the repository
                String cId= String.valueOf(card.getCardId());
                System.out.println(cId);
                Optional<Card> foundCard = userCardRepository.findById(card.getCardId());
                if (foundCard.isPresent()) {
                    System.out.println("card not found creating it");
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
    private boolean isValidCardId(Status cardId) {
        return card1.equals(String.valueOf(cardId)) || card2.equals(String.valueOf(cardId)) || card3.equals(String.valueOf(cardId)) || card4.equals(String.valueOf(cardId)) || card5.equals(String.valueOf(cardId));
    }

    //-------------------normal if else for better understanding which satisfies all conditions--------
//    @Override
//    public Card addTaskToCard(String cardId, Task task, String managerId) throws EmployeeNotAssigned, UserNotFoundException, TaskAlreadyExistException, CardNotFoundException, TaskNotFoundException, TaskOverloadException, InvalidUser {
//        // Checking whether the user is the manager using managerId
//        if (this.managerId.equals(managerId)) {
//            // Check whether cardId is present in userCardRepository
//            Status cId=Status.valueOf(cardId);
//            Optional<Card> optionalCard = userCardRepository.findById(cId);
//            if (optionalCard.isPresent()) {
//                // If card exists, get the card
//                Card card = optionalCard.get();
//
//                // Ensure the tasks list is initialized
//                List<Task> taskList = card.getTasks();
//                if (taskList == null) {
//                    taskList = new ArrayList<>();
//                    card.setTasks(taskList);
//                }
//
//                // Check for the employees assigned to the task and get them
//                User assignee = task.getAssignedEmployees();
//                // It should not be null; the task should be assigned to someone
//                if (assignee != null) {
//                    List<String> ineligibleUsers = new ArrayList<>();
//
//                    // Check whether the assigned employee is a valid employee in userRepository
//
//                        if (userRepository.existsById(assignee.getUserId())) {
//                            // If valid employee, check how many tasks that employee has
//                            long inProgressTaskCount = 0;
//                            for (Task existingTask : taskList) {
//                                if (existingTask.getStatus() == Status.In_Progress) {
//                                    User assignedUser = existingTask.getAssignedEmployees();
//                                    if (assignedUser != null) {
//
//                                            // Employees in task card == given employee
//                                            if (assignedUser.getUserId().equals(assignee.getUserId())) {
//                                                inProgressTaskCount++;
//                                            }
//
//                                    }
//                                }
//                            }
//
//                            if (task.getStatus() == Status.In_Progress && inProgressTaskCount >= 3) {
//                                ineligibleUsers.add(assignee.getUserName() + " (ID: " + assignee.getUserId() + ")");
//                            }
//                        } else {
//                            throw new UserNotFoundException();
//                        }
//
//
//                    // If there are ineligible users, throw an exception and inform the manager
//                    if (!ineligibleUsers.isEmpty()) {
//                        throw new TaskOverloadException("Task cannot be assigned to the following user(s) due to overload: " + String.join(", ", ineligibleUsers));
//                    }
//
//                } else {
//                    throw new EmployeeNotAssigned();
//                }
//
//                // Check if the task already exists in the card
//                for (Task existingTask : taskList) {
//                    if (existingTask.getTaskId().equals(task.getTaskId())) {
//                        throw new TaskAlreadyExistException();
//                    }
//                }
//
//                // Add the new task to the card
////                taskRepository.save(task);
//                card.getTasks().add(task);
//                userCardRepository.save(card);
//
//                return card;
//
//            } else {
//                throw new CardNotFoundException();
//            }
//        } else {
//            throw new InvalidUser();
//        }
//    }

    @Override
    public Card addTaskToCard(String cardId, Task task, String managerId) throws EmployeeNotAssigned, UserNotFoundException, TaskAlreadyExistException, CardNotFoundException, TaskNotFoundException, TaskOverloadException, InvalidUser {
        // Checking whether the user is the manager using managerId
        if (this.managerId.equals(managerId)) {
            // Check whether cardId is present in userCardRepository
            Status cId = Status.valueOf(cardId);
            Optional<Card> optionalCard = userCardRepository.findById(cId);
            if (optionalCard.isPresent()) {
                // If card exists, get the card
                Card card = optionalCard.get();

                // Ensure the tasks list is initialized
                List<Task> taskList = card.getTasks();
                if (taskList == null) {
                    taskList = new ArrayList<>();
                    card.setTasks(taskList);
                }

                // Generate a unique ID for the new task
                String newTaskId = UUID.randomUUID().toString();
                task.setTaskId(newTaskId);

                // Check for the employees assigned to the task and get them
                User assignees = task.getAssignedEmployees();
                if (assignees != null) {
                    List<String> ineligibleUsers = new ArrayList<>();

                    // Check each assigned employee

                        if (userRepository.existsById(assignees.getUserId())) {
                            // If valid employee, check how many tasks that employee has
                            long inProgressTaskCount = 0;
                            for (Task existingTask : taskList) {
                                if (existingTask.getStatus() == Status.In_Progress) {
                                    User assignedUser = existingTask.getAssignedEmployees();
                                    if (assignedUser != null) {
                                        // Employees in task card == given employee
                                        if (assignedUser.getUserId().equals(assignees.getUserId())) {
                                            inProgressTaskCount++;
                                        }
                                    }
                                }
                            }

                            if (task.getStatus() == Status.In_Progress && inProgressTaskCount >= 3) {
                                ineligibleUsers.add(assignees.getUserName() + " (ID: " + assignees.getUserId() + ")");
                            }
                        } else {
                            throw new UserNotFoundException();
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
        Status cId=Status.valueOf(cardId);
        Optional<Card> optionalCard = userCardRepository.findById(cId);
        if (!optionalCard.isPresent()) {
            throw new CardNotFoundException();
        }
        Card card = optionalCard.get();

        // Validate assignees before looking for the task
        User assignee = updatedTask.getAssignedEmployees();
        if (assignee != null) {
            List<String> ineligibleUsers = new ArrayList<>();


                if (!userRepository.existsById(assignee.getUserId())) {
                    throw new UserNotFoundException();
                }

                long inProgressTaskCount = 0;
                for (Task t : card.getTasks()) {
                    if (t.getStatus() == Status.In_Progress) {
                        User assignedUsers = t.getAssignedEmployees();
                        if (assignedUsers != null) {

                                if (assignedUsers.getUserId().equals(assignee.getUserId())) {
                                    inProgressTaskCount++;
                            }
                        }
                    }
                }

                if (updatedTask.getStatus() == Status.In_Progress && inProgressTaskCount >= 3) {
                    ineligibleUsers.add(assignee.getUserName() + " (ID: " + assignee.getUserId() + ")");
                }


            // If there are ineligible users, throw an exception and inform the manager
            if (!ineligibleUsers.isEmpty()) {
                throw new TaskOverloadException("Task cannot be assigned to the following user(s) due to overload: " + String.join(", ", ineligibleUsers));
            }
        }

        System.out.println("in edit after in progress check");
        // Remove old task and add updated task
        List<Task> tasks = card.getTasks();
        Task oldTask = null;
        for (Task t : tasks) {
            if (t.getTaskId().equals(updatedTask.getTaskId())) {
                oldTask = t;
                break;
            }
        }




        // Update task attributes
        System.out.println("updating task");
//        if (updatedTask.getTaskName() != null && updatedTask.getPriority() != null && updatedTask.getStatus() != null && updatedTask.getAssignedEmployees() != null) {
            if (oldTask != null) {
                tasks.remove(oldTask);
            } else {
                throw new TaskNotFoundException();
            }
        System.out.println("task with updated details :: "+updatedTask);
            tasks.add(updatedTask);
//        }
        System.out.println("tasks in card ::: "+tasks);

        // Save the updated card

        card.setTasks(tasks);
        System.out.println("tasks in card after save ::: "+card.getTasks());
        userCardRepository.save(card);

        return updatedTask;
    }


    @Override
    public void deleteTask(String cardId, String taskId, String managerId)
            throws TaskNotFoundException, CardNotFoundException, InvalidUser {

        if (this.managerId.equals(managerId)) {
            // Find the card by its ID
            Status cId=Status.valueOf(cardId);
            Optional<Card> optionalCard = userCardRepository.findById(cId);
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
        Status cId=Status.valueOf(cardId);
        Optional<Card> foundCardOpt = userCardRepository.findById(cId);
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
        if(!user.getUserRole().equalsIgnoreCase("admin")){
            List<Task> filteredTasks=new ArrayList<>();
            for(Task task : tasks){
                System.out.println("in service getTask 3");
                //System.out.println(task.getAssignedEmployees());
                User assignedEmployees = task.getAssignedEmployees();
//                List<String> ids = new ArrayList<>();
//                assignedEmployees.forEach(userid -> ids.add(userid.getUserId()));
                String ids=assignedEmployees.getUserId();
                if(ids.equalsIgnoreCase(user.getUserId())){
                    System.out.println("match found for"+user);
                    System.out.println("task found for"+task);
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
                User assignedUser = task.getAssignedEmployees();
                    if (assignedUser.getUserId().equals(userId)) {
                        tasks.add(task);
                        break; // Break the loop once the user is found in the task
                    }

            }
        }

        if (tasks.isEmpty()) {
            throw new TaskNotFoundException();
        }

        return tasks;
    }

//    public void moveTask(String fromCardId, String toCardId, String taskId) throws TaskOverloadException, CardNotFoundException, TaskNotFoundException {
//        // Retrieve the "from" card
//        Status cId=Status.valueOf(fromCardId);
//        Optional<Card> fromCardOptional = userCardRepository.findById(cId);
//        Card fromCard;
//        if (fromCardOptional.isPresent()) {
//            fromCard = fromCardOptional.get();
//            System.out.println("From card before removal: " + fromCard);
//        } else {
//            // Handle the case where the "from" card doesn't exist
//            throw new CardNotFoundException();
//        }
//
//        // Retrieve the "to" card
//        Status cToId=Status.valueOf(toCardId);
//        Optional<Card> toCardOptional = userCardRepository.findById(cToId);
//        Card toCard;
//        if (toCardOptional.isPresent()) {
//            toCard = toCardOptional.get();
//            System.out.println("To card before adding: " + toCard);
//        } else {
//            // Handle the case where the "to" card doesn't exist
//            throw new CardNotFoundException();
//        }
//
//        // Find the task in the "from" card
//        Task taskToMove = null;
//        for (Task task : fromCard.getTasks()) {
//            if (task.getTaskId().equals(taskId)) {
//                taskToMove = task;
//                System.out.println("Task to move (before status change): " + taskToMove);
//                break;
//            }
//        }
//
//        if (taskToMove == null) {
//            // Handle the case where the task isn't found in the "from" card
//            throw new TaskNotFoundException();
//        }
//
//        // Update the task's status to match the "to" card ID
//        taskToMove.setStatus(cToId);
//        System.out.println("Task to move (after status change): " + taskToMove);
//
//        // Remove the task from the "from" card
//        fromCard.getTasks().remove(taskToMove);
//        System.out.println("From card after removal: " + fromCard);
//        if (!fromCard.getTasks().contains(taskToMove)) {
//            System.out.println("Task successfully removed from 'fromCard'");
//        } else {
//            System.out.println("Task removal failed from 'fromCard'");
//        }
//
//        // Add the task to the "to" card
//        toCard.getTasks().add(taskToMove);
//        System.out.println("To card after adding: " + toCard);
//        if (toCard.getTasks().contains(taskToMove)) {
//            System.out.println("Task successfully added to 'toCard'");
//        } else {
//            System.out.println("Task addition failed to 'toCard'");
//        }
//
//        // Save the updated "from" card
//        userCardRepository.save(fromCard);
//        Card savedFromCard = userCardRepository.findById(cId).orElse(null);
//        System.out.println("From card after save: " + savedFromCard);
//        if (savedFromCard != null && !savedFromCard.getTasks().contains(taskToMove)) {
//            System.out.println("Task is no longer in 'fromCard' after save");
//        } else {
//            System.out.println("Task is still in 'fromCard' after save or save operation failed");
//        }
//
//        // Save the updated "to" card
//        userCardRepository.save(toCard);
//        Card savedToCard = userCardRepository.findById(cToId).orElse(null);
//        System.out.println("To card after save: " + savedToCard);
//        if (savedToCard != null && savedToCard.getTasks().contains(taskToMove)) {
//            System.out.println("Task successfully moved to 'toCard' after save");
//        } else {
//            System.out.println("Task is not in 'toCard' after save or save operation failed");
//        }
//    }
//
public void moveTask(String fromCardId, String toCardId, String taskId)
        throws TaskOverloadException, CardNotFoundException, TaskNotFoundException, EmployeeNotAssigned {
    // Retrieve the "from" card
    Status fromStatus = Status.valueOf(fromCardId);
    Optional<Card> fromCardOptional = userCardRepository.findById(fromStatus);
    Card fromCard;
    if (fromCardOptional.isPresent()) {
        fromCard = fromCardOptional.get();
        System.out.println("From card before removal: " + fromCard);
    } else {
        // Handle the case where the "from" card doesn't exist
        throw new CardNotFoundException();
    }

    // Retrieve the "to" card
    Status toStatus = Status.valueOf(toCardId);
    Optional<Card> toCardOptional = userCardRepository.findById(toStatus);
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
        if (task.getTaskId().equals(taskId)) {
            taskToMove = task;
            System.out.println("Task to move (before status change): " + taskToMove);
            break;
        }
    }
    if (taskToMove == null) {
        // Handle the case where the task isn't found in the "from" card
        throw new TaskNotFoundException();
    }

    // Check for task ID conflict in the destination card
    boolean idConflict = toCard.getTasks().stream()
            .anyMatch(task -> task.getTaskId().equals(taskId));

    if (idConflict) {
        // Generate a new unique taskId
        String newTaskId = generateUniqueTaskId();
        taskToMove.setTaskId(newTaskId);
    }

    // Update the task's status to the name of the "to" card
    taskToMove.setStatus(toCard.getCardName());
    System.out.println("Task to move (after status change): " + taskToMove);


    if(String.valueOf(toCard.getCardId()).equals("In_Progress")) {
    User assignee = taskToMove.getAssignedEmployees();
    // It should not be null; the task should be assigned to someone
    if (assignee != null) {
        List<String> ineligibleUsers = new ArrayList<>();

        // Check whether the assigned employee is a valid employee in userRepository
        List<Task>taskList=toCard.getTasks();
        if (userRepository.existsById(assignee.getUserId())) {
            // If valid employee, check how many tasks that employee has
            long inProgressTaskCount = 0;
            for (Task existingTask : taskList) {
                if (existingTask.getStatus() == Status.In_Progress) {
                    User assignedUser = existingTask.getAssignedEmployees();
                    if (assignedUser != null) {

                        // Employees in task card == given employee
                        if (assignedUser.getUserId().equals(assignee.getUserId())) {
                            inProgressTaskCount++;
                        }

                    }
                }
            }

            if (taskToMove.getStatus() == Status.In_Progress && inProgressTaskCount >= 3) {
                ineligibleUsers.add(assignee.getUserName() + " (ID: " + assignee.getUserId() + ")");
            }
        } else {
            throw new UserNotFoundException();
        }


        // If there are ineligible users, throw an exception and inform the manager
        if (!ineligibleUsers.isEmpty()) {
            throw new TaskOverloadException("Task cannot be assigned to the following user(s) due to overload: " + String.join(", ", ineligibleUsers));
        }

    } else {
        throw new EmployeeNotAssigned();
    }
}




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
    Card savedFromCard = userCardRepository.findById(fromStatus).orElse(null);
    System.out.println("From card after save: " + savedFromCard);
    if (savedFromCard != null && !savedFromCard.getTasks().contains(taskToMove)) {
        System.out.println("Task is no longer in 'fromCard' after save");
    } else {
        System.out.println("Task is still in 'fromCard' after save or save operation failed");
    }

    // Save the updated "to" card
    userCardRepository.save(toCard);
    Card savedToCard = userCardRepository.findById(toStatus).orElse(null);
    System.out.println("To card after save: " + savedToCard);
    if (savedToCard != null && savedToCard.getTasks().contains(taskToMove)) {
        System.out.println("Task successfully moved to 'toCard' after save");
    } else {
        System.out.println("Task is not in 'toCard' after save or save operation failed");
    }
}
    private void initializeCards() {
        List<Status> predefinedCards = Arrays.asList(Status.To_Do, Status.In_Progress, Status.Done);
        for (Status status : predefinedCards) {
            Optional<Card> existingCard = userCardRepository.findById(status);
            if (existingCard.isEmpty()) {
                Card card = new Card(status,new ArrayList<Task>(),status);
                card.setCardId(status);
                card.setTasks(new ArrayList<>());
                userCardRepository.save(card);
            }
        }
    }

    // Method to generate a new unique taskId
    private String generateUniqueTaskId() {
        // Implement logic to generate a unique task ID. This could be a UUID or a sequence number.
        return UUID.randomUUID().toString();
    }

}




