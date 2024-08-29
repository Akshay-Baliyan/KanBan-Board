package com.example.Domain;

public class MoveTaskRequest {
    private String fromCardId;
    private String toCardId;
    private String taskId;

    public MoveTaskRequest(String fromCardId, String toCardId, String taskId) {
        this.fromCardId = fromCardId;
        this.toCardId = toCardId;
        this.taskId = taskId;
    }

    public String getFromCardId() {
        return fromCardId;
    }

    public void setFromCardId(String fromCardId) {
        this.fromCardId = fromCardId;
    }

    public String getToCardId() {
        return toCardId;
    }

    public void setToCardId(String toCardId) {
        this.toCardId = toCardId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public String toString() {
        return "MoveTaskRequest{" +
                "fromCardId='" + fromCardId + '\'' +
                ", toCardId='" + toCardId + '\'' +
                ", taskId='" + taskId + '\'' +
                '}';
    }
}
