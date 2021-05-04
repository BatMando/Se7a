package com.example.se7a.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
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
import com.example.se7a.Model.Pill;
import com.example.se7a.Model.User;
import com.example.se7a.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class AddNewPill extends BaseActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST =1 ;
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
    protected Button login;
    protected ImageView back;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private DatabaseReference mDatabaseRef2;
    String uploadId;
    boolean flag=false;

    private StorageTask mUploadTask;
    String selectedPillType;
    int selectedPillTypeIndex;
    List<Integer> pill_days=new ArrayList<Integer>(); ;
    List<Integer> pill_days_alarm=new ArrayList<Integer>(); ;
    final String[] items = new String[]{"كبسولة", "تحميلة", "مليجرام","حقنة","قرص","لاصق","كيس","ملعقة","قطرة","أمبول","بخة"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_add_new_pill);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        mStorageRef = FirebaseStorage.getInstance().getReference("pills");


        String userjson = getstring("user");
        Gson gson = new Gson();
        DataHolder.currentUser = gson.fromJson(userjson, User.class);

        initView();

        PillType.setAdapter(adapter);
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


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).fit().centerInside().transform(new CropCircleTransformation()).into(pillTypeImage);
        }
    }

    int hourOfDay = 0;
    int minuteOfDay = 0;

    public void openDatePicker(View view) {
        Calendar calendar = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                hourOfDay = hour;
                minuteOfDay = minute;
                pillTime.setText(hourOfDay + " : " + minuteOfDay);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY)
                , calendar.get(Calendar.MINUTE)
                , true);
        timePickerDialog.show();
        flag=true;
    }
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    String pill_instruction;
    String pill_type;
    String pill_dose;
    String pill_name;
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.pill_time) {
            openDatePicker(view);
        }
        else if (view.getId() == R.id.add) {
            getTextData();
            getCheckboxData();

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
            else if (!flag){
                pillName.setError(null);
                pillDose.setError(null);
                pillInstructions.setError(null);
                Toast.makeText(AddNewPill.this,"لا يوجد وقت محدد لأخذ الدواء",Toast.LENGTH_SHORT).show();
            }
            else if (pill_days.isEmpty()){
                pillName.setError(null);
                pillDose.setError(null);
                pillInstructions.setError(null);
                Toast.makeText(AddNewPill.this,"لا يوجد ايام محددة لأخذ الدواء",Toast.LENGTH_SHORT).show();
            }
            else {
                flag=false;
                showProgressBar(R.string.loading);
                uploadFile();
            }

              //

            //


        } else if (view.getId() == R.id.back) {
            finish();

        } else if (view.getId() == R.id.pill_type_image) {
            openFileChooser();
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
    String photoLink;
    Random random = new Random();


    private void uploadFile() {
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("pills");
        uploadId = mDatabaseRef.push().getKey();
        if (mImageUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //  Toast.makeText(MainActivity.this, "Upload successful", Toast.LENGTH_LONG).show();
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    photoLink = uri.toString();
                                    Log.e("link",photoLink);
                                    final Pill pill= new Pill();

                                    pill.setPill_image(photoLink);
                                    pill.setUser_id(DataHolder.currentUser.getId() + "");
                                    pill.setPill_id(uploadId);
                                    pill.setPill_type(pill_type);
                                    pill.setPill_hour(hourOfDay);
                                    pill.setPill_minute(minuteOfDay);
                                    pill.setPill_name(pill_name);
                                    pill.setPill_instruction(pill_instruction);
                                    pill.setPill_dose(pill_dose);
                                    pill.setPill_days(pill_days);
                                    pill.setPill_type_index(selectedPillTypeIndex);
                                    pill.setPill_status(false);
                                    mDatabaseRef.child(uploadId).setValue(pill, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            mDatabaseRef= FirebaseDatabase.getInstance().getReference("Alarms");
                                            for (int i : pill_days) {
                                                Alarm alarm=new Alarm();
                                                Log.e("days",i+"");
                                                int y = random.nextInt(10000000);
                                               // int id=(int)System.currentTimeMillis();
                                             //   Log.e("new id",y+"");
                                                alarm.setAlarm_id(y);
                                                alarm.setPill_id(uploadId);
                                                alarm.setTitle("تنبيه بمعاد الدواء");
                                                alarm.setBody("تناول"+" "+pill_dose+" "+pill_type+" من دواء "+ pill_name);
                                                alarm.setDay(i);
                                                alarm.setHour(hourOfDay);
                                                alarm.setMin(minuteOfDay);
                                                alarm.setUser_id(DataHolder.currentUser.getId() + "");
                                                setAlarmForTest(y,i,
                                                        "تنبيه بمعاد الدواء",
                                                        "تناول"+" "+pill_dose+" "+pill_type+" من دواء "+ pill_name,
                                                        hourOfDay,
                                                        minuteOfDay);
                                                //  pill_days_alarm.add(pill_days.get(i));
                                                mDatabaseRef.child(y+"").setValue(alarm);
                                            }

                                            finish();
                                            // finish();
                                            hideProgressBar();
                                            Toast.makeText(AddNewPill.this,"تم اضافة الدواء بنجاح",Toast.LENGTH_SHORT).show();


                                        }
                                    });
                                }
                            });
                           // addAlarms();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddNewPill.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
        else {
            Toast.makeText(this, "اختر صورة الدواء", Toast.LENGTH_SHORT).show();
        }

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

        Log.e("Alarm","pill Alarm "+id+" set day "+day+" at "+hour+" : "+min );
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
   /* public void addAlarms(){
        Alarm alarm=new Alarm();
        for(int i=0; i<pill_days.size();i++){
            Log.e("days",pill_days.size()+"");
            int id=(int)System.currentTimeMillis();
            alarm.setAlarm_id(id);
            alarm.setPill_id(uploadId);
            alarm.setTitle("تنبيه بمعاد الدواء");
            alarm.setBody(pill_name+" "+pill_type+" "+pill_dose);
            pill_days_alarm.add(pill_days.get(i));
            mDatabaseRef2.child(id+"").setValue(alarm);
        }
    }*/

    public String getstring(String key) {
        SharedPreferences sharedPreferences =
                getSharedPreferences("LoginTest", MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    private void initView() {
        pillTypeImage = (ImageView) findViewById(R.id.pill_type_image);
        pillTypeImage.setOnClickListener(AddNewPill.this);
        pillName = (TextInputLayout) findViewById(R.id.pill_name);
        PillType = (Spinner) findViewById(R.id.Pill_type);
        pillDose = (TextInputLayout) findViewById(R.id.pill_dose);
        pillInstructions = (TextInputLayout) findViewById(R.id.pill_instructions);
        pillTime = (TextView) findViewById(R.id.pill_time);
        pillTime.setOnClickListener(AddNewPill.this);
        satCheck = (CheckBox) findViewById(R.id.sat_check);
        sunCheck = (CheckBox) findViewById(R.id.sun_check);
        monCheck = (CheckBox) findViewById(R.id.mon_check);
        tueCheck = (CheckBox) findViewById(R.id.tue_check);
        wedCheck = (CheckBox) findViewById(R.id.wed_check);
        thuCheck = (CheckBox) findViewById(R.id.thu_check);
        friCheck = (CheckBox) findViewById(R.id.fri_check);
        login = (Button) findViewById(R.id.add);
        login.setOnClickListener(AddNewPill.this);
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(AddNewPill.this);
    }
}