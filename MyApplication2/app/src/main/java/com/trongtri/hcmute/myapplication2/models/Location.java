package com.trongtri.hcmute.myapplication2.models;

/**
 * Created by Dell on 12/26/2017.
 */

public class Location {
    long time;
    float flat;
    float flon;

    public Location(){

    }

    public Location(long time, float flat, float flon) {
        this.time = time;
        this.flat = flat;
        this.flon = flon;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public float getFlat() {
        return flat;
    }

    public void setFlat(float flat) {
        this.flat = flat;
    }

    public float getFlon() {
        return flon;
    }

    public void setFlon(float flon) {
        this.flon = flon;
    }
}
