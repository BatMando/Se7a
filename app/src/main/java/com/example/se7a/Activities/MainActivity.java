package com.example.se7a.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.example.se7a.Base.BaseActivity;
import com.example.se7a.Broadcast.ExampleJopService;
import com.example.se7a.Broadcast.TaskAlarmBroadCast;
import com.example.se7a.DataHolder;
import com.example.se7a.Fragments.ExerciseFragment;
import com.example.se7a.Fragments.FollowUpFragment;
import com.example.se7a.Fragments.Pills_Fragment;
import com.example.se7a.Model.Alarm;
import com.example.se7a.Model.User;
import com.example.se7a.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.Calendar;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    boolean flag;
    protected BottomNavigationView bottomNav;
    BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id =item.getItemId();
                    Fragment fragment=null;
                    if(id==R.id.pills){
                        fragment=new Pills_Fragment();
                    }
                    else if(id==R.id.follow_up){
                        fragment=new FollowUpFragment();
                    }
                    else if(id ==R.id.exercise){
                        fragment=new ExerciseFragment();
                    }
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container,fragment)
                            .commit();
                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);
        String userjson = getstring("user");
        Gson gson = new Gson();
        DataHolder.currentUser = gson.fromJson(userjson, User.class);
        // Log.e("id", DataHolder.currentUser.getId() + "");
        Log.e("shared", getstring("user"));
        Log.e("dataholder", DataHolder.currentUser.getId() + "");
        Log.e("dataholder", DataHolder.currentUser.getUsername() + "");
        flag=getIntent().getBooleanExtra("login",false);
        initView();
        Log.e("flag",flag+"");
        if(flag){
       databaseReference.child("Alarms")
                .orderByChild("user_id")
                .equalTo(DataHolder.currentUser.getId() + "").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Alarm alarm = postSnapshot.getValue(Alarm.class);
                    //databaseReference2.child("Alarms/" + alarm.getAlarm_id()).removeValue();
                    //cancelAlarmForTest(alarm.getAlarm_id());
                    setAlarmForTest(alarm.getAlarm_id(), alarm.getDay(),alarm.getTitle(),alarm.getBody(),alarm.getHour(),alarm.getMin());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        }
        //cancelJob();
        scheduleJob();
    }

    private void cancelAlarmForTest(int id) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        AlarmManager alarmManager2 = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, TaskAlarmBroadCast.class);
        alarmIntent.putExtra("id", id);
        // alarmIntent.addCategory("Pill Alarm");
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(this, id, alarmIntent, 0);
        PendingIntent pendingIntent2 = PendingIntent
                .getBroadcast(this, id + 1, alarmIntent, 0);
        Log.e("notification", "cancelAlarmForTest: " + id);
        alarmManager.cancel(pendingIntent);
        alarmManager2.cancel(pendingIntent2);


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
    public String getstring(String key) {
        SharedPreferences sharedPreferences =
                getSharedPreferences("LoginTest", MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }
    public void saveString (String key ,String value){
        SharedPreferences.Editor editor=
                getSharedPreferences("LoginTest",MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.apply();
    }
    @Override
    public void onClick(View view) {
        /*if (view.getId() == R.id.logout) {
            saveString("user",null);
            startActivity(new Intent(activity,login.class));
            finish();
        }*/
    }

    public void scheduleJob() {
        ComponentName componentName = new ComponentName(this, ExampleJopService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000L)
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d("mando", "Job scheduled");
        } else {
            Log.d("mando", "Job scheduling failed");
        }
    }

    public void cancelJob() {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.d("mando", "Job cancelled");
    }

    private void initView() { bottomNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
        bottomNav.setSelectedItemId(R.id.pills);
    }
}