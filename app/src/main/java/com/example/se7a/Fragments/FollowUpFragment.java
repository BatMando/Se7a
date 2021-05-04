package com.example.se7a.Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se7a.Activities.login;
import com.example.se7a.Adapters.PillAtExactTimeFollowUpAdapter;
import com.example.se7a.Broadcast.TaskAlarmBroadCast;
import com.example.se7a.DataHolder;
import com.example.se7a.Model.Alarm;
import com.example.se7a.Model.History;
import com.example.se7a.Model.Pill;
import com.example.se7a.Model.User;
import com.example.se7a.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;


/**
 *
 */
public class FollowUpFragment extends Fragment implements View.OnClickListener {
    protected View rootView;
    protected ImageView logout;
    ArrayList<History> histories = new ArrayList<>();
    ArrayList<Pill> pills = new ArrayList<Pill>();
    PillAtExactTimeFollowUpAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference HistoryDatabaseRef;
    private DatabaseReference PillDatabaseRef;
    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference();


    View view;


    public FollowUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_follow_up, container, false);
        recyclerView = view.findViewById(R.id.recycler_follow_up_fragment);
        HistoryDatabaseRef = FirebaseDatabase.getInstance().getReference("History");
        PillDatabaseRef = FirebaseDatabase.getInstance().getReference("pills");
        initView(view);
        String userjson = getstring("user");
        Gson gson = new Gson();
        DataHolder.currentUser = gson.fromJson(userjson, User.class);
        layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        databaseReference.child("pills")
                .orderByChild("user_id")
                .equalTo(DataHolder.currentUser.getId() + "").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    pills.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Pill pill = postSnapshot.getValue(Pill.class);
                        pills.add(pill);
                    }

                    adapter = new PillAtExactTimeFollowUpAdapter(getContext(), pills);
                    adapter.setOnYesClickListener(new PillAtExactTimeFollowUpAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position, Pill pill) {
                            addToHistory(pill);
                        }
                    });
                    adapter.setOnNoClickListener(new PillAtExactTimeFollowUpAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position, Pill pill) {
                            Toast.makeText(getContext(),"ضروري تناول الدواء",Toast.LENGTH_SHORT).show();

                        }
                    });
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    public void addToHistory(Pill pill) {
        String uploadId = HistoryDatabaseRef.push().getKey();
        History history = new History();
        Calendar calendar = Calendar.getInstance();
        int min = calendar.get(Calendar.MINUTE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        history.setDate_taken(currentDate);
        history.setHistory_id(uploadId);
        history.setMinute(min);
        history.setHour_taken(hour);
        history.setPill_id(pill.getPill_id());
        history.setUser_id(pill.getUser_id());
        HistoryDatabaseRef.child(uploadId).setValue(history);
        PillDatabaseRef.child(pill.getPill_id() + "/pill_status").setValue(true);
    }


    public String getstring(String key) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginTest", MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.logout) {
            saveString("user", null);
            databaseReference2.child("Alarms")
                    .orderByChild("user_id")
                    .equalTo(DataHolder.currentUser.getId() + "").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Alarm alarm = postSnapshot.getValue(Alarm.class);
                        //databaseReference2.child("Alarms/" + alarm.getAlarm_id()).removeValue();
                        cancelAlarmForTest(alarm.getAlarm_id());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            startActivity(new Intent(getContext(), login.class));
            getActivity().finish();
        }
    }
    public void saveString(String key, String value) {
        SharedPreferences.Editor editor =
                getActivity().getSharedPreferences("LoginTest", MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }
    private void cancelAlarmForTest(int id) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        AlarmManager alarmManager2 = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        Intent alarmIntent = new Intent(getContext(), TaskAlarmBroadCast.class);
        alarmIntent.putExtra("id", id);
        // alarmIntent.addCategory("Pill Alarm");
        PendingIntent pendingIntent = PendingIntent
                .getBroadcast(getContext(), id, alarmIntent, 0);
        PendingIntent pendingIntent2 = PendingIntent
                .getBroadcast(getContext(), id + 1, alarmIntent, 0);
        Log.e("notification", "cancelAlarmForTest: " + id);
        alarmManager.cancel(pendingIntent);
        alarmManager2.cancel(pendingIntent2);


    }

    private void initView(View rootView) {
        logout = (ImageView) rootView.findViewById(R.id.logout);
        logout.setOnClickListener(FollowUpFragment.this);
    }
}