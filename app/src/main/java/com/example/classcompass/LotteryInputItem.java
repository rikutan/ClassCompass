package com.example.classcompass;

public class LotteryInputItem {
    private String lotteryItem;
    private String itemName;
    private String itemWinner;

    public LotteryInputItem(String lotteryItem, String itemName, String itemWinner) {
        this.lotteryItem = lotteryItem;
        this.itemName = itemName;
        this.itemWinner = itemWinner;
    }


    public String getLotteryItem() {
        return lotteryItem;
    }

    public void setLotteryItem(String lotteryItem) {
        this.lotteryItem = lotteryItem;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemWinner() {
        return itemWinner;
    }

    public void setItemWinner(String itemWinner) {
        this.itemWinner = itemWinner;
    }
}
