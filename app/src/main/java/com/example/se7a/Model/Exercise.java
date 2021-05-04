package com.example.se7a.Model;
import java.util.List;

public class Exercise {
    String exercise_id;
    String user_id;

    public List<Integer> getExercise_days() {
        return exercise_days;
    }

    public void setExercise_days(List<Integer> exercise_days) {
        this.exercise_days = exercise_days;
    }

    List<Integer> exercise_days;
    int exercise_minute;
    int exercise_hour;
    String exercise_title;
    String exercise_type;

    public int getExercise_type_index() {
        return exercise_type_index;
    }

    public void setExercise_type_index(int exercise_type_index) {
        this.exercise_type_index = exercise_type_index;
    }

    int exercise_type_index;
    public Exercise(){

    }

    public String getExercise_id() {
        return exercise_id;
    }

    public void setExercise_id(String exercise_id) {
        this.exercise_id = exercise_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }



    public int getExercise_minute() {
        return exercise_minute;
    }

    public void setExercise_minute(int exercise_minute) {
        this.exercise_minute = exercise_minute;
    }

    public int getExercise_hour() {
        return exercise_hour;
    }

    public void setExercise_hour(int exercise_hour) {
        this.exercise_hour = exercise_hour;
    }

    public String getExercise_title() {
        return exercise_title;
    }

    public void setExercise_title(String exercise_title) {
        this.exercise_title = exercise_title;
    }

    public String getExercise_type() {
        return exercise_type;
    }

    public void setExercise_type(String exercise_type) {
        this.exercise_type = exercise_type;
    }
}
