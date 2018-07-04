package at.fhooe.mc.android;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class PlayerData implements  Serializable{
    private int mScore;
    private int mLevel;
    private int mTime;

    public PlayerData(int _s, int _l, int _t) {
        mScore = _s;
        mLevel = _l;
        mTime = _t;
    }

    public int getScore() {
        return  mScore;
    }

    public void setScore(int _s) {
        this.mScore = _s;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int _l) {
        this.mLevel = _l;
    }

    public int getTime() {
        return mTime;
    }

    public void setTime(int _t) {
        this.mTime = _t;
    }

    public String getDate() {
        return "";
    }


}

