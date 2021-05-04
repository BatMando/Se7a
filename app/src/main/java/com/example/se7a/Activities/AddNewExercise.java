package com.example.se7a.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.se7a.Base.BaseActivity;
import com.example.se7a.Broadcast.TaskAlarmBroadCast;
import com.example.se7a.DataHolder;
import com.example.se7a.Model.Alarm;
import com.example.se7a.Model.Exercise;
import com.example.se7a.Model.User;
import com.example.se7a.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class AddNewExercise extends BaseActivity implements View.OnClickListener {

    protected ImageView back;
    protected ImageView exerciseTypeImage;
    protected TextInputLayout exercise;
    protected Spinner exerciseType;
    protected TextView exerciseTime;
    protected CheckBox satCheck;
    protected CheckBox sunCheck;
    protected CheckBox monCheck;
    protected CheckBox tueCheck;
    protected CheckBox wedCheck;
    protected CheckBox thuCheck;
    protected CheckBox friCheck;
    protected Button add;
    boolean flag=false;
    int selectedExerciseTypeIndex;
    String selectedExerciseType;
    List<Integer> exercise_days=new ArrayList<Integer>(); ;
    final String[] items = new String[]{"تدريبات اللياقة", "المشي", "العلاج الطبيعي", "الجري", "ركوب الدراجة", "سباحة", "كرة القدم", "كرة السلة", "تدريبات القوة"};
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_add_new_exercise);
        initView();


        String userjson = getstring("user");
        Gson gson = new Gson();
        DataHolder.currentUser = gson.fromJson(userjson, User.class);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        exerciseType.setAdapter(adapter);
        exerciseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedExerciseType=items[position];
                selectedExerciseTypeIndex=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    int hourOfDay=0;
    int minuteOfDay=0;

    public void openDatePicker(View view) {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                hourOfDay = hour;
                minuteOfDay = minute;
                exerciseTime.setText(hourOfDay+" : "+minuteOfDay);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE)
                , true);
        timePickerDialog.show();
        flag=true;
    }
    String exercise_name;
    String exercise_type;

    public void getCheckboxData(){
        exercise_days.clear();

        if(satCheck.isChecked()){
            exercise_days.add(7);
            Log.e("checkbox1","checked");
        }
        if (sunCheck.isChecked()){
            exercise_days.add((1));
            Log.e("checkbox2","checked");
        }
        if (monCheck.isChecked()){
            exercise_days.add((2));
            Log.e("checkbox3","checked");
        }
        if (tueCheck.isChecked()){
            exercise_days.add((3));
            Log.e("checkbox4","checked");
        }
        if (wedCheck.isChecked()){
            exercise_days.add((4));
            Log.e("checkbox5","checked");
        }
        if (thuCheck.isChecked()){
            exercise_days.add((5));
            Log.e("checkbox6","checked");
        }
        if (friCheck.isChecked()){
            exercise_days.add((6));
            Log.e("checkbox7","checked");
        }
    }
    public void getTextData(){
        exercise_name=exercise.getEditText().getText().toString();
        exercise_type=selectedExerciseType;
    }
    private void setAlarmForTest(int id, int day, String title, String body,int hour,int min) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        AlarmManager alarmManager2 = (AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        // calendar.set(Calendar.SECOND, 0);
        // calendar.set(Calendar.MILLISECOND,00);
        calendar.set(Calendar.DAY_OF_WEEK,day);

        Log.e("Alarm","Exercise Alarm "+id+" set day "+day+" at "+hour+" : "+min );
        Intent alarmIntent = new Intent(this, TaskAlarmBroadCast.class);
        alarmIntent.putExtra("title", title);
        alarmIntent.putExtra("body", body);
        alarmIntent.putExtra("id", id);
        alarmIntent.putExtra("day",day);
        alarmIntent.putExtra("min",min);
        alarmIntent.putExtra("hour",hour);
        alarmIntent.putExtra("cat",1);

        //alarmIntent.addCategory("alarm" + id);
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(this, id, alarmIntent, 0);
        PendingIntent pendingIntent2 = PendingIntent
                .getBroadcast(this, id+1, alarmIntent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        //alarmManager2.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent2);
    }
    Random random = new Random();
    String uploadId;
    public void uploadExercise(){
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("exercises");
        Exercise exercise=new Exercise();
        exercise.setUser_id(DataHolder.currentUser.getId() + "");
         uploadId = mDatabaseRef.push().getKey();
        exercise.setExercise_id(uploadId);
        exercise.setExercise_type(exercise_type);
        exercise.setExercise_minute(minuteOfDay);
        exercise.setExercise_hour(hourOfDay);
        exercise.setExercise_title(exercise_name);
        exercise.setExercise_type_index(selectedExerciseTypeIndex);
        exercise.setExercise_days(exercise_days);
        mDatabaseRef.child(uploadId).setValue(exercise, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                mDatabaseRef= FirebaseDatabase.getInstance().getReference("Alarms");
                for (int i : exercise_days) {
                    Alarm alarm=new Alarm();
                    Log.d("days",i+"");
                    int y = random.nextInt(10000000);
                    // int id=(int)System.currentTimeMillis();
                    //   Log.e("new id",y+"");
                    alarm.setAlarm_id(y);
                    alarm.setPill_id(uploadId);
                    alarm.setTitle("تنبيه بمعاد الرياضة");
                    alarm.setBody("قم ب اداء"+" "+exercise_name+" من رياضة "+exercise_type);
                    alarm.setDay(i);
                    alarm.setHour(hourOfDay);
                    alarm.setMin(minuteOfDay);
                    alarm.setUser_id(DataHolder.currentUser.getId() + "");
                    setAlarmForTest(y,i, "تنبيه بمعاد الرياضة", "قم ب اداء"+" "+exercise_name+" من رياضة "+exercise_type, hourOfDay, minuteOfDay);
                    //  pill_days_alarm.add(pill_days.get(i));
                    mDatabaseRef.child(y+"").setValue(alarm);
                }

            }
        });
        finish();
        hideProgressBar();
        Toast.makeText(this,"تم اضافة التمرين بنجاح",Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {

            finish();
        } else if (view.getId() == R.id.exercise_time) {
            openDatePicker(view);}


        else if (view.getId() == R.id.add) {
            getTextData();
            getCheckboxData();
            if(exercise_name.length()==0){
                exercise.requestFocus();
                exercise.setError("لا تترك اسم الرياضة فارغ");
            }
            else if(exercise_name.length()<3){
                exercise.requestFocus();
                exercise.setError("اسم الرياضة قصير للغاية");
            }
            else if (!flag){
                exercise.setError(null);
                Toast.makeText(AddNewExercise.this,"لا يوجد وقت محدد لأداء الرياضة",Toast.LENGTH_SHORT).show();
            }
            else if (exercise_days.isEmpty()){
                exercise.setError(null);
                Toast.makeText(AddNewExercise.this,"لا يوجد ايام محددة لأداء الرياضة",Toast.LENGTH_SHORT).show();
            }
            else {
                flag=false;
                showProgressBar(R.string.loading);
                uploadExercise();
            }

        }
    }
    public String getstring(String key) {
        SharedPreferences sharedPreferences =
                getSharedPreferences("LoginTest", MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(AddNewExercise.this);
        exerciseTypeImage = (ImageView) findViewById(R.id.exercise_type_image);
        exercise = (TextInputLayout) findViewById(R.id.exercise);
        exerciseType = (Spinner) findViewById(R.id.exercise_type);
        exerciseTime = (TextView) findViewById(R.id.exercise_time);
        exerciseTime.setOnClickListener(AddNewExercise.this);
        satCheck = (CheckBox) findViewById(R.id.sat_check);
        satCheck.setOnClickListener(AddNewExercise.this);
        sunCheck = (CheckBox) findViewById(R.id.sun_check);
        sunCheck.setOnClickListener(AddNewExercise.this);
        monCheck = (CheckBox) findViewById(R.id.mon_check);
        monCheck.setOnClickListener(AddNewExercise.this);
        tueCheck = (CheckBox) findViewById(R.id.tue_check);
        tueCheck.setOnClickListener(AddNewExercise.this);
        wedCheck = (CheckBox) findViewById(R.id.wed_check);
        wedCheck.setOnClickListener(AddNewExercise.this);
        thuCheck = (CheckBox) findViewById(R.id.thu_check);
        thuCheck.setOnClickListener(AddNewExercise.this);
        friCheck = (CheckBox) findViewById(R.id.fri_check);
        friCheck.setOnClickListener(AddNewExercise.this);
        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(AddNewExercise.this);
    }
}