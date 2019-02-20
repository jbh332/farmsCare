package com.example.hong.boaaproject.communityActivity;

public class BoardModel {

    String boardContent;
    String userID;
    String boardDate;
    String boardNum;
    String boardComment;
    String boardImgURL;
    String myNicName;

    public BoardModel(String userID, String boardContent, String boardImgURL, String boardNum, String boardDate, String myNicName) {
        this.userID = userID;
        this.myNicName = myNicName;
        this.boardContent = boardContent;
        this.boardDate = boardDate;
        this.boardImgURL = boardImgURL;
        this.boardNum = boardNum;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getBoardNum() {
        return boardNum;
    }

}
