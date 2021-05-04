package com.example.se7a.Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.se7a.Activities.AddNewPill;
import com.example.se7a.Activities.Pill_update;
import com.example.se7a.Activities.login;
import com.example.se7a.Adapters.PillsFragmentHomePageAdapter;
import com.example.se7a.Base.BaseFragment;
import com.example.se7a.Broadcast.TaskAlarmBroadCast;
import com.example.se7a.DataHolder;
import com.example.se7a.Model.Alarm;
import com.example.se7a.Model.Pill;
import com.example.se7a.Model.User;
import com.example.se7a.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Pills_Fragment extends BaseFragment implements View.OnClickListener {


    protected FloatingActionButton addPills;
    protected View rootView;
    protected ImageView logout;
    protected TextView noPills;
    ArrayList<Pill> pills = new ArrayList<Pill>();

    public Pills_Fragment() {
        // Required empty public constructor
    }

    PillsFragmentHomePageAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference();
    DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference();


    View view;
    FirebaseStorage mStorage = FirebaseStorage.getInstance();


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_pills_, container, false);
        initView(view);
        recyclerView = view.findViewById(R.id.recycler_pill_fragment);
        String userjson = getstring("user");
        Gson gson = new Gson();
        DataHolder.currentUser = gson.fromJson(userjson, User.class);
        layoutManager = new GridLayoutManager(getContext(), 2);
        showProgressBar(R.string.loading);

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
                    adapter = new PillsFragmentHomePageAdapter(getContext(), pills);

                    new Handler()
                            .postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgressBar();
                                }
                            }, 500);
                    adapter.setOnItemClickListener(new PillsFragmentHomePageAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            final Pill selectedItem = pills.get(position);
                            //Toast.makeText(getContext(),"normal click at position "+position,Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), Pill_update.class);
                            intent.putExtra("pill_id", selectedItem.getPill_id());
                            intent.putExtra("user_id", selectedItem.getUser_id());
                            intent.putExtra("pill_name", selectedItem.getPill_name());
                            intent.putExtra("pill_type", selectedItem.getPill_type());
                            intent.putExtra("pill_dose", selectedItem.getPill_dose());
                            intent.putExtra("pill_instruction", selectedItem.getPill_instruction());
                            intent.putIntegerArrayListExtra("pill_days", (ArrayList<Integer>) selectedItem.getPill_days());
                            intent.putExtra("pill_minute", selectedItem.getPill_minute());
                            intent.putExtra("pill_hour", selectedItem.getPill_hour());
                            intent.putExtra("pill_image", selectedItem.getPill_image());
                            intent.putExtra("pill_type_index", selectedItem.getPill_type_index());

                            startActivity(intent);
                        }

                        @Override
                        public void onDeleteClick(int position) {
                            final Pill selectedItem = pills.get(position);
                            final String selectedId = selectedItem.getPill_id();
                            StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getPill_image());
                            imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    databaseReference.child("pills/" + selectedId).removeValue();

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
                                    Toast.makeText(getContext(), " تم حذف دواء " + selectedItem.getPill_name() + " بنجاح  ", Toast.LENGTH_SHORT).show();
                                    pills.clear();
                                    adapter = new PillsFragmentHomePageAdapter(getContext(), pills);

                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    });

                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    new Handler()
                            .postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    hideProgressBar();
                                }
                            }, 500);
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_pills) {
            Log.e("fab", "pressed");
            Intent Intent = new Intent(getActivity(), AddNewPill.class);
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

        } else if (view.getId() == R.id.no_pills) {

        }
    }

    public void saveString(String key, String value) {
        SharedPreferences.Editor editor =
                getActivity().getSharedPreferences("LoginTest", MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void initView(View rootView) {
        addPills = (FloatingActionButton) rootView.findViewById(R.id.add_pills);
        addPills.setOnClickListener(Pills_Fragment.this);
        logout = (ImageView) rootView.findViewById(R.id.logout);
        logout.setOnClickListener(Pills_Fragment.this);
        noPills = (TextView) rootView.findViewById(R.id.no_pills);
        noPills.setOnClickListener(Pills_Fragment.this);
    }
}