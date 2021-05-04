package com.example.se7a.Broadcast;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.se7a.DataHolder;
import com.example.se7a.Model.Pill;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ExampleJopService extends JobService {

        private static final String TAG = "ExampleJobService";
        private boolean jobCancelled = false;
    Calendar calendar = Calendar.getInstance();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference PillDatabaseRef=FirebaseDatabase.getInstance().getReference("pills");



    @Override
        public boolean onStartJob(JobParameters params) {
            Log.d("mando", "Job started");
            doBackgroundWork(params);
            return true;
        }
          int hour;
          int min;
        private void doBackgroundWork(final JobParameters params) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    hour=  calendar.get(Calendar.HOUR_OF_DAY);
                    min=  calendar.get(Calendar.MINUTE);
                    for (int i = 0; i < 60; i++) {
                       // if (hour==00&&min==i){
                            if (hour==00&&min==i){
                                databaseReference.child("pills")
                                        .orderByChild("user_id")
                                        .equalTo(DataHolder.currentUser.getId()+"").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){

                                            for(DataSnapshot postSnapshot: snapshot.getChildren()){
                                                Pill pill =postSnapshot.getValue(Pill.class);
                                                PillDatabaseRef.child(pill.getPill_id()+"/pill_status").setValue(false);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                Log.d("mando", "Job finished");
                            jobFinished(params, false);
                            break;
                        }
                        else{
                                Log.d("mando", ""+i);

                                Log.d("mando", "time hasn't come yet");
                        }
                    }
                }
            }).start();
        }

        @Override
        public boolean onStopJob(JobParameters params) {
            Log.d(TAG, "Job cancelled before completion");
            jobCancelled = true;
            return true;
        }

}
