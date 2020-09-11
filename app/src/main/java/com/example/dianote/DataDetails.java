package com.example.dianote;

public class DataDetails {

    private String points;
    private String date;
    private String time;
    private String eatTime;


    private String note = "";

    public DataDetails()
    {

    }

    public DataDetails(String points, String date, String time, String eatTime) {
        this.points = points;
        this.date = date;
        this.time = time;
        this.eatTime = eatTime;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEatTime() {
        return eatTime;
    }

    public void setEatTime(String eatTime) {
        this.eatTime = eatTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
