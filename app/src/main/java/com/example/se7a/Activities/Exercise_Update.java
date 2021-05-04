package com.example.se7a.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
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

import com.example.se7a.Base.BaseActivity;
import com.example.se7a.DataHolder;
import com.example.se7a.Model.Exercise;
import com.example.se7a.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Exercise_Update extends BaseActivity implements View.OnClickListener {

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
    int selectedExerciseTypeIndex;
    String selectedExerciseType;
    String selectedExerciseId;
    List<Integer> exercise_days=new ArrayList<Integer>(); ;
    final String[] items = new String[]{"تدريبات اللياقة", "المشي", "العلاج الطبيعي", "الجري", "ركوب الدراجة", "سباحة", "كرة القدم", "كرة السلة", "تدريبات القوة"};
    private DatabaseReference mDatabaseRef;
    int hour;
    int minute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_exercise__update);
        initView();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("exercises");
        hour=getIntent().getIntExtra("exercise_hour",0);
        minute=getIntent().getIntExtra("exercise_minute",0);

        exercise.getEditText().setText(getIntent().getStringExtra("exercise_title"));
        selectedExerciseId=getIntent().getStringExtra("exercise_id");
        selectedExerciseTypeIndex=getIntent().getIntExtra("exercise_type_index",0);
        exerciseTime.setText(getIntent().getIntExtra("exercise_hour",0)+" : "+getIntent().getIntExtra("exercise_minute",0));
        exercise_days=getIntent().getIntegerArrayListExtra("exercise_days");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        exerciseType.setAdapter(adapter);
        exerciseType.setSelection(selectedExerciseTypeIndex);

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
        checkBoxIsChecked();
        disableCheckBox();
    }

    public  void checkBoxIsChecked(){
        if (exercise_days.contains(7)){
            satCheck.setChecked(true);
        }
        if (exercise_days.contains(1)){
            sunCheck.setChecked(true);
        }
        if (exercise_days.contains(2)){
            monCheck.setChecked(true);
        }
        if (exercise_days.contains(3)){
            tueCheck.setChecked(true);
        }
        if (exercise_days.contains(4)){
            wedCheck.setChecked(true);
        }
        if (exercise_days.contains(5)){
            thuCheck.setChecked(true);
        }
        if (exercise_days.contains(6)){
            friCheck.setChecked(true);
        }
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            finish();

        } else if (view.getId() == R.id.exercise_time) {
            //openDatePicker(view);

        } else if (view.getId() == R.id.add) {
            getCheckboxData();
            getTextData();
            if(exercise_name.length()==0){
                exercise.requestFocus();
                exercise.setError("لا تترك اسم الرياضة فارغ");
            }
            else if(exercise_name.length()<3){
                exercise.requestFocus();
                exercise.setError("اسم الرياضة قصير للغاية");
            }
            else {
                exercise.setError(null);
                showProgressBar(R.string.loading);
                updateExercise();
            }
        }

    }
    int hourOfDay=0;
    int minuteOfDay=0;

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
    public void disableCheckBox(){
        satCheck.setEnabled(false);
        sunCheck.setEnabled(false);
        monCheck.setEnabled(false);
        tueCheck.setEnabled(false);
        wedCheck.setEnabled(false);
        thuCheck.setEnabled(false);
        friCheck.setEnabled(false);
    }
    public void updateExercise(){
        Exercise exercise=new Exercise();
        exercise.setUser_id(DataHolder.currentUser.getId() + "");
        String uploadId = selectedExerciseId;
        exercise.setExercise_id(uploadId);
        exercise.setExercise_type(exercise_type);
        if(minuteOfDay!=0&&hourOfDay!=0){
            exercise.setExercise_minute(minuteOfDay);
            exercise.setExercise_hour(hourOfDay);
        }
        else
        {
            exercise.setExercise_minute(minute);
            exercise.setExercise_hour(hour);
        }
        exercise.setExercise_title(exercise_name);
        exercise.setExercise_type_index(selectedExerciseTypeIndex);
        exercise.setExercise_days(exercise_days);
        mDatabaseRef.child(uploadId).setValue(exercise);
        finish();
        hideProgressBar();
        Toast.makeText(this,"تم اضافة التمرين بنجاح",Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(Exercise_Update.this);
        exerciseTypeImage = (ImageView) findViewById(R.id.exercise_type_image);
        exercise = (TextInputLayout) findViewById(R.id.exercise);
        exerciseType = (Spinner) findViewById(R.id.exercise_type);
        exerciseTime = (TextView) findViewById(R.id.exercise_time);
        exerciseTime.setOnClickListener(Exercise_Update.this);
        satCheck = (CheckBox) findViewById(R.id.sat_check);
        sunCheck = (CheckBox) findViewById(R.id.sun_check);
        monCheck = (CheckBox) findViewById(R.id.mon_check);
        tueCheck = (CheckBox) findViewById(R.id.tue_check);
        wedCheck = (CheckBox) findViewById(R.id.wed_check);
        thuCheck = (CheckBox) findViewById(R.id.thu_check);
        friCheck = (CheckBox) findViewById(R.id.fri_check);
        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(Exercise_Update.this);
    }
}