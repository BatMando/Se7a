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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se7a.Activities.AddNewExercise;
import com.example.se7a.Activities.Exercise_Update;
import com.example.se7a.Activities.login;
import com.example.se7a.Adapters.ExercisesFragmentHomePageAdapter;
import com.example.se7a.Broadcast.TaskAlarmBroadCast;
import com.example.se7a.DataHolder;
import com.example.se7a.Model.Alarm;
import com.example.se7a.Model.Exercise;
import com.example.se7a.Model.User;
import com.example.se7a.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExerciseFragment extends Fragment implements View.OnClickListener {


    protected View rootView;
    protected FloatingActionButton addExercise;
    protected ImageView logout;
    ArrayList<Exercise> exercises = new ArrayList<Exercise>();
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference();

    public ExerciseFragment() {
        // Required empty public constructor
    }

    ExercisesFragmentHomePageAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;

    View view;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_exercise, container, false);
        initView(view);
        String userjson = getstring("user");
        Gson gson = new Gson();
        DataHolder.currentUser = gson.fromJson(userjson, User.class);
        recyclerView = view.findViewById(R.id.recycler_exercise_fragment);
        layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        databaseReference.child("exercises").orderByChild("user_id").equalTo(DataHolder.currentUser.getId() + "").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    exercises.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Exercise e = postSnapshot.getValue(Exercise.class);
                        exercises.add(e);
                    }
                    adapter = new ExercisesFragmentHomePageAdapter(getContext(), exercises);
                    adapter.setOnItemClickListener(new ExercisesFragmentHomePageAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            final Exercise selectedItem = exercises.get(position);
                            Intent intent = new Intent(getActivity(), Exercise_Update.class);
                            intent.putExtra("exercise_id", selectedItem.getExercise_id());
                            intent.putExtra("user_id", selectedItem.getUser_id());
                            intent.putExtra("exercise_title", selectedItem.getExercise_title());
                            intent.putExtra("exercise_hour", selectedItem.getExercise_hour());
                            intent.putExtra("exercise_minute", selectedItem.getExercise_minute());
                            intent.putExtra("exercise_type_index", selectedItem.getExercise_type_index());
                            intent.putExtra("exercise_type", selectedItem.getExercise_type());
                            intent.putIntegerArrayListExtra("exercise_days", (ArrayList<Integer>) selectedItem.getExercise_days());
                            startActivity(intent);
                        }

                        @Override
                        public void onDeleteClick(int position) {
                            final Exercise selectedItem = exercises.get(position);
                            final String selectedId = selectedItem.getExercise_id();
                            databaseReference.child("exercises/" + selectedId).removeValue();
                            databaseReference2.child("Alarms")
                                    .orderByChild("pill_id")
                                    .equalTo(selectedId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                                        Alarm alarm = postSnapshot.getValue(Alarm.class);
                                        databaseReference2.child("Alarms/" + alarm.getAlarm_id()).removeValue();
                                        cancelAlarmForTest(alarm.getAlarm_id());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            Toast.makeText(getContext(), " تم حذف رياضة " + selectedItem.getExercise_title() + " بنجاح ", Toast.LENGTH_SHORT).show();
                            exercises.clear();
                            adapter = new ExercisesFragmentHomePageAdapter(getContext(), exercises);
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
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

    public String getstring(String key) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LoginTest", MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public void saveString(String key, String value) {
        SharedPreferences.Editor editor =
                getActivity().getSharedPreferences("LoginTest", MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_exercises) {
            Log.e("fab", "pressed");
            Intent Intent = new Intent(getActivity(), AddNewExercise.class);
            startActivity(Intent);
        } else if (view.getId() == R.id.logout) {
            saveString("user", null);
            databaseReference3.child("Alarms")
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

    private void initView(View rootView) {
        addExercise = (FloatingActionButton) rootView.findViewById(R.id.add_exercises);
        addExercise.setOnClickListener(ExerciseFragment.this);
        logout = (ImageView) rootView.findViewById(R.id.logout);
        logout.setOnClickListener(ExerciseFragment.this);
    }
}