package com.example.Domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class User {
    @Id
        //private String managerId;
        private String userId;
        private String userName;
//        private String userEmail;
        private String userPassword;
        private String userRole;


        public User() {
        }


    public User(String userId, String userName, String userPassword, String userRole) {
        this.userId = userId;
        this.userName = userName;
//        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userRole = userRole;
        //this.managerId=managerId;
    }

    public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

//        public String getUserEmail() {
//            return userEmail;
//        }
//
//        public void setUserEmail(String userEmail) {
//            this.userEmail = userEmail;
//        }

        public String getUserPassword() {
            return userPassword;
        }

        public void setUserPassword(String userPassword) {
            this.userPassword = userPassword;
        }

        public String getUserRole() {
            return userRole;
        }

        public void setUserRole(String userRole) {
            this.userRole = userRole;
        }

//    public String getManagerId() {
//        return managerId;
//    }
//
//    public void setManagerId(String managerId) {
//        this.managerId = managerId;
//    }

    @Override
    public String toString() {
        return "User{" +
                " userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
//                ", userEmail='" + userEmail + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userRole='" + userRole + '\'' +
                '}';
    }
}

