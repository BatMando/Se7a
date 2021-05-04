package com.example.se7a.Model;

public class History {
    String pill_id;
    String user_id;
    String history_id;
    String date_taken;
    int hour_taken;
    int minute;

    public History() {

    }

    public String getPill_id() {
        return pill_id;
    }

    public void setPill_id(String pill_id) {
        this.pill_id = pill_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getHistory_id() {
        return history_id;
    }

    public void setHistory_id(String history_id) {
        this.history_id = history_id;
    }


    public int getHour_taken() {
        return hour_taken;
    }

    public void setHour_taken(int hour_taken) {
        this.hour_taken = hour_taken;
    }

    public String getDate_taken() {
        return date_taken;
    }

    public void setDate_taken(String date_taken) {
        this.date_taken = date_taken;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}