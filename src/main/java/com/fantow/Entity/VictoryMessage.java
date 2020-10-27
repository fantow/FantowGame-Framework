package com.fantow.Entity;

public class VictoryMessage {

    public int WinnerId;

    public int LoserId;

    public VictoryMessage(int winnerId, int loserId) {
        WinnerId = winnerId;
        LoserId = loserId;
    }

    public int getWinnerId() {
        return WinnerId;
    }

    public void setWinnerId(int winnerId) {
        WinnerId = winnerId;
    }

    public int getLoserId() {
        return LoserId;
    }

    public void setLoserId(int loserId) {
        LoserId = loserId;
    }
}
