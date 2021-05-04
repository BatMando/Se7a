package com.example.se7a.Model;

import java.util.List;

public class Pill {
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


    public String getPill_name() {
        return pill_name;
    }

    public void setPill_name(String pill_name) {
        this.pill_name = pill_name;
    }

    public String getPill_type() {
        return pill_type;
    }

    public void setPill_type(String pill_type) {
        this.pill_type = pill_type;
    }

    public String getPill_dose() {
        return pill_dose;
    }

    public void setPill_dose(String pill_dose) {
        this.pill_dose = pill_dose;
    }

    public String getPill_instruction() {
        return pill_instruction;
    }

    public void setPill_instruction(String pill_instruction) {
        this.pill_instruction = pill_instruction;
    }

    public int getPill_minute() {
        return pill_minute;
    }

    public void setPill_minute(int pill_minute) {
        this.pill_minute = pill_minute;
    }

    public int getPill_hour() {
        return pill_hour;
    }

    public void setPill_hour(int pill_hour) {
        this.pill_hour = pill_hour;
    }


    public List<Integer> getPill_days() {
        return pill_days;
    }

    public void setPill_days(List<Integer> pill_days) {
        this.pill_days = pill_days;
    }
    String pill_id;
    String user_id;
    String pill_name;
    String pill_type;
    String pill_dose;
    String pill_instruction;
    List<Integer> pill_days;
    int pill_minute;
    int pill_hour;
    String pill_image;
    boolean pill_status;

    public boolean isPill_status() {
        return pill_status;
    }

    public void setPill_status(boolean pill_status) {
        this.pill_status = pill_status;
    }

    public int getPill_type_index() {
        return pill_type_index;
    }

    public void setPill_type_index(int pill_type_index) {
        this.pill_type_index = pill_type_index;
    }

    int pill_type_index;

    public String getPill_image() {
        return pill_image;
    }

    public void setPill_image(String pill_image) {
        this.pill_image = pill_image;
    }

    public Pill(){

    }
}