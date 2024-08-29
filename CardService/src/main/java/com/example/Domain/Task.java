package com.example.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class Task {
    @Id
    private String taskId;
    private String taskName;
    private Priority priority;
    private Status status;
    private List<User> assignedEmployees;
    private String startDate;
    private String endDate;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Task(){}

    public Task(String taskId, String taskName, Priority priority, Status status, List<User> assignedEmployees) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.priority = priority;
        this.status = status;
        this.assignedEmployees = assignedEmployees;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }



    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<User> getAssignedEmployees() {
        return assignedEmployees;
    }

    public void setAssignedEmployees(List<User> assignedEmployees) {
        this.assignedEmployees = assignedEmployees;
    }


    @Override
    public String toString() {
        return "Task{" +
                "taskId='" + taskId + '\'' +
                ", taskName='" + taskName + '\'' +
                ", priority=" + priority +
                ", status=" + status +
                ", assignedEmployees=" + assignedEmployees +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
