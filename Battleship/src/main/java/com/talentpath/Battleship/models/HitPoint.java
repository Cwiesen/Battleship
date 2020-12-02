package com.talentpath.Battleship.models;

public class HitPoint extends Point {
    String hitStatus;

    public HitPoint() {

    }

    public HitPoint(HitPoint toCopy) {
        super.x = toCopy.x;
        super.y = toCopy.y;
        this.hitStatus = toCopy.hitStatus;
    }

    public HitPoint(int x, int y, String hitStatus) {
        super.x = x;
        super.y = y;
        this.hitStatus = hitStatus;
    }

    public String getHitStatus() {
        return hitStatus;
    }

    public void setHitStatus(String hitStatus) {
        this.hitStatus = hitStatus;
    }
}
