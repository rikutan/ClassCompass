package com.example.classcompass;

public class LotteryItem {

    private String lotteryID = "";


    public LotteryItem(String lotteryID){
        this.lotteryID = lotteryID;
    }


    public String getLotteryID() {
        return lotteryID;
    }
    public void setLotteryID(String lotteryID) {
        this.lotteryID = lotteryID;
    }

}
