package com.example.classcompass;

public class ChatAdapterItem {

    private String memberID = "";
    private String memberIcon = "";
    private String memberName = "";
    private String message = "";


    public ChatAdapterItem(String memberID, String memberIcon, String memberName, String message){
        this.memberID = memberID;
        this.memberIcon = memberIcon;
        this.memberName = memberName;
        this.message = message;
    }


    public String getMemberID() {
        return memberID;
    }
    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }
    public String getMemberIcon() {
        return memberIcon;
    }
    public void setMemberIcon(String memberIcon) {
        this.memberIcon = memberIcon;
    }
    public String getMemberName() {
        return memberName;
    }
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }



}
