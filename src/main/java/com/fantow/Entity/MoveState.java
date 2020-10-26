package com.fantow.Entity;

public class MoveState {
    // 起始位置
    public float fromPosx;
    public float fromPosy;

    // 终止位置
    public float toPosx;
    public float toPosy;

    public long startTime;

    public float getFromPosx() {
        return fromPosx;
    }

    public void setFromPosx(float fromPosx) {
        this.fromPosx = fromPosx;
    }

    public float getFromPosy() {
        return fromPosy;
    }

    public void setFromPosy(float fromPosy) {
        this.fromPosy = fromPosy;
    }

    public float getToPosx() {
        return toPosx;
    }

    public void setToPosx(float toPosx) {
        this.toPosx = toPosx;
    }

    public float getToPosy() {
        return toPosy;
    }

    public void setToPosy(float toPosy) {
        this.toPosy = toPosy;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
