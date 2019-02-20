package com.example.hong.boaaproject.mainActivity;

public class UserInformationModel {


    String userNicName;
    String userHeight;
    String userWeight;
    String userImgURL;

    public UserInformationModel(String userNicName, String userHeight, String userWeight, String userImgURL) {

        this.userNicName = userNicName;
        this.userHeight = userHeight;
        this.userWeight = userWeight;
        this.userImgURL = userImgURL;
    }

    public String getUserNicName() {
        return userNicName;
    }

    public String getUserHeight() {
        return userHeight;
    }

    public String getUserWeight() {
        return userWeight;
    }

}
