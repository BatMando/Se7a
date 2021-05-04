package com.example.se7a.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.se7a.Model.Alarm;
import com.example.se7a.Model.Pill;
import com.example.se7a.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class Pill_update extends BaseActivity implements View.OnClickListener {

    protected ImageView back;
    protected ImageView pillTypeImage;
    protected TextInputLayout pillName;
    protected Spinner PillType;
    protected TextInputLayout pillDose;
    protected TextInputLayout pillInstructions;
    protected TextView pillTime;
    protected CheckBox satCheck;
    protected CheckBox sunCheck;
    protected CheckBox monCheck;
    protected CheckBox tueCheck;
    protected CheckBox wedCheck;
    protected CheckBox thuCheck;
    protected CheckBox friCheck;
    protected Button add;
    String selectedPillType;
    int selectedPillTypeIndex;
    List<Integer> pill_days=new ArrayList<Integer>(); ;
    List<Integer> pill_days_before=new ArrayList<Integer>(); ;


    final String[] items = new String[]{"كبسولة", "تحميلة", "مليجرام","حقنة","قرص","لاصق","كيس","ملعقة","قطرة","أمبول","بخة"};
    private DatabaseReference mDatabaseRef;
    String selectedPillId;
    int hour;
    int minute;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_pill_update);
        initView();

        pillName.getEditText().setText(getIntent().getStringExtra("pill_name"));
        selectedPillId=getIntent().getStringExtra("pill_id");
        pillDose.getEditText().setText(getIntent().getStringExtra("pill_dose"));
        selectedPillTypeIndex=getIntent().getIntExtra("pill_type_index",0);
        pillInstructions.getEditText().setText(getIntent().getStringExtra("pill_instruction"));
        pillTime.setText(getIntent().getIntExtra("pill_hour",0)+" : "+getIntent().getIntExtra("pill_minute",0));
        pill_days=getIntent().getIntegerArrayListExtra("pill_days");
        pill_days_before=getIntent().getIntegerArrayListExtra("pill_days");
        Picasso.get().load(getIntent().getStringExtra("pill_image")).fit().centerInside().transform(new CropCircleTransformation()).into(pillTypeImage);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        PillType.setAdapter(adapter);
        PillType.setSelection(selectedPillTypeIndex);
        PillType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("pill type", items[position]);
                selectedPillType=items[position];
                selectedPillTypeIndex=position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        checkBoxIsChecked();
        disableCheckBox();




    }
    public  void checkBoxIsChecked(){
        if (pill_days.contains(7)){
            satCheck.setChecked(true);

        }
        if (pill_days.contains(1)){
            sunCheck.setChecked(true);
        }
        if (pill_days.contains(2)){
            monCheck.setChecked(true);
        }
        if (pill_days.contains(3)){
            tueCheck.setChecked(true);
        }
        if (pill_days.contains(4)){
            wedCheck.setChecked(true);
        }
        if (pill_days.contains(5)){
            thuCheck.setChecked(true);
        }
        if (pill_days.contains(6)){
            friCheck.setChecked(true);
        }
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

    int hourOfDay = 0;
    int minuteOfDay = 0;

    public  void updateData(){
        Pill pill= new Pill();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("pills");
        pill.setPill_image(getIntent().getStringExtra("pill_image"));
        pill.setUser_id(DataHolder.currentUser.getId() + "");
        String uploadId = selectedPillId;
        pill.setPill_id(uploadId);
        pill.setPill_type(pill_type);
        if(minuteOfDay!=0&&hourOfDay!=0){
            pill.setPill_hour(hourOfDay);
            pill.setPill_minute(minuteOfDay);
        }
        else
        {
            pill.setPill_hour(hour);
            pill.setPill_minute(minute);
        }
        pill.setPill_name(pill_name);
        pill.setPill_instruction(pill_instruction);
        pill.setPill_dose(pill_dose);
        pill.setPill_days(pill_days);
        pill.setPill_type_index(selectedPillTypeIndex);
        //history check
        pill.setPill_status(false);
        mDatabaseRef.child(uploadId).setValue(pill);
        finish();
        // finish();
        hideProgressBar();
        Toast.makeText(Pill_update.this,"تم تعديل الدواء بنجاح",Toast.LENGTH_SHORT).show();


    }


    String pill_instruction;
    String pill_type;
    String pill_dose;
    String pill_name;
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            Intent Intent = new Intent(this, MainActivity.class);
            startActivity(Intent);
        }
        else if (view.getId() == R.id.add) {
            getTextData();
            if (pill_name.length()==0){
                pillName.requestFocus();
                pillName.setError("لا تترك اسم الدواء فارغ");
            }
            else if (pill_name.length()<3){
                pillName.requestFocus();
                pillName.setError("اسم الدواء قصير للغاية");
            }
            else if (pill_dose.length()==0){
                pillName.setError(null);
                pillDose.requestFocus();
                pillDose.setError("لا تترك جرعة الدواء فارغة");
            }
            else if (pill_instruction.length()==0){
                pillName.setError(null);
                pillDose.setError(null);
                pillInstructions.requestFocus();
                pillInstructions.setError("لا تترك تعليمات الدواء فارغة");

            }
            else {
                showProgressBar(R.string.loading);
                updateData();
            }


        }
        else if (view.getId() == R.id.pill_time) {
            //openDatePicker(view);
        }
    }
    public void getTextData(){
        pill_name=pillName.getEditText().getText().toString();
        pill_type=selectedPillType;
        pill_dose=pillDose.getEditText().getText().toString();
        pill_instruction=pillInstructions.getEditText().getText().toString();

    }

    public void getCheckboxData(){
        pill_days.clear();

        if(satCheck.isChecked()){
            pill_days.add(7);
            Log.e("checkbox1","checked");
        }
        if (sunCheck.isChecked()){
            pill_days.add((1));
            Log.e("checkbox2","checked");
        }
        if (monCheck.isChecked()){
            pill_days.add((2));
            Log.e("checkbox3","checked");
        }
        if (tueCheck.isChecked()){
            pill_days.add((3));
            Log.e("checkbox4","checked");
        }
        if (wedCheck.isChecked()){
            pill_days.add((4));
            Log.e("checkbox5","checked");
        }
        if (thuCheck.isChecked()){
            pill_days.add((5));
            Log.e("checkbox6","checked");
        }
        if (friCheck.isChecked()){
            pill_days.add((6));
            Log.e("checkbox7","checked");
        }
    }
    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(Pill_update.this);
        pillTypeImage = (ImageView) findViewById(R.id.pill_type_image);
        pillName = (TextInputLayout) findViewById(R.id.pill_name);
        PillType = (Spinner) findViewById(R.id.Pill_type);
        pillDose = (TextInputLayout) findViewById(R.id.pill_dose);
        pillInstructions = (TextInputLayout) findViewById(R.id.pill_instructions);
        pillTime = (TextView) findViewById(R.id.pill_time);
        pillTime.setOnClickListener(Pill_update.this);
        satCheck = (CheckBox) findViewById(R.id.sat_check);
        sunCheck = (CheckBox) findViewById(R.id.sun_check);
        monCheck = (CheckBox) findViewById(R.id.mon_check);
        tueCheck = (CheckBox) findViewById(R.id.tue_check);
        wedCheck = (CheckBox) findViewById(R.id.wed_check);
        thuCheck = (CheckBox) findViewById(R.id.thu_check);
        friCheck = (CheckBox) findViewById(R.id.fri_check);
        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(Pill_update.this);
    }
}