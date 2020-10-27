package com.fantow.Entity;

public class RankItemEntity {

    public int randId;

    public int userId;

    public int score;

    public String userName;

    public String heroAvatar;

    public int getRandId() {
        return randId;
    }

    public void setRandId(int randId) {
        this.randId = randId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeroAvatar() {
        return heroAvatar;
    }

    public void setHeroAvatar(String heroAvatar) {
        this.heroAvatar = heroAvatar;
    }
}
