package com.example.Domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document
public class Card {
    @Id
//    private String cardId;
//    private String cardName;
    private Status cardId;
    private Status cardName;
    private List<Task> tasks;

    public Card(Status cardId, List<Task> tasks, Status cardName) {
        this.cardId = cardId;
        this.tasks = tasks;
        this.cardName = cardName;
    }



//    public Card(){}

//    public Card(String cardId, String cardName, List<Task> tasks) {
//        this.cardId = cardId;
//        this.cardName = cardName;
//        this.tasks = tasks;
//    }

//    public String getCardId() {
//        return cardId;
//    }
//
//    public void setCardId(String cardId) {
//        this.cardId = cardId;
//    }
//
//    public String getCardName() {

    public Status getCardId() {
        return cardId;
    }

    public void setCardId(Status cardId) {
        this.cardId = cardId;
    }

    public Status getCardName() {
        return cardName;
    }

    public void setCardName(Status cardName) {
        this.cardName = cardName;
    }
//        return cardName;
//    }
//

    @Override
    public String toString() {
        return "Card{" +
                "cardId=" + cardId +
                ", cardName=" + cardName +
                ", tasks=" + tasks +
                '}';
    }
//    public void setCardName(String cardName) {
//        this.cardName = cardName;
//    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

}
