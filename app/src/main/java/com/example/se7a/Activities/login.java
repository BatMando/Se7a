package com.example.se7a.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.example.se7a.Base.BaseActivity;
import com.example.se7a.DataHolder;
import com.example.se7a.Model.User;
import com.example.se7a.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;


public class login extends BaseActivity implements View.OnClickListener {

    protected TextInputLayout Email;
    protected TextInputLayout Password;
    protected Button login;
    protected TextView Register;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_login);
        initView();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login) {

            final String email = Email.getEditText().getText().toString();
            final String password = Password.getEditText().getText().toString();

            if (email != null && password != null){
                databaseReference.child("users")
                        .orderByChild("email")
                        .equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        hideProgressBar();
                        Log.e("snap", snapshot + "");
                        if (!snapshot.hasChildren()) {
                            showMessage("خطأ", "البريد الالكتروني او كلمة السر خطأ", "حسنا");
                        } else {
                            for (DataSnapshot object : snapshot.getChildren()) {
                                User user = object.getValue(User.class);
                                if (user.getPassword().equals(password)) {

                                    DataHolder.currentUser = user;
                                    Gson gson = new Gson();
                                    String userJson = gson.toJson(user);
                                    saveString("user", userJson);

                                    Intent intent = new Intent(activity, MainActivity.class);
                                    intent.putExtra("login",true);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        hideProgressBar();
                        showMessage("خطأ", error.getMessage(), "حسنا");
                    }
                });
            }
            else {
                Toast.makeText(this,"رجاء املئ الخانات الفارغة", Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == R.id.Register) {
            startActivity(new Intent(activity,Register.class));
            finish();
        }
    }

    private void initView() {

        Email = (TextInputLayout) findViewById(R.id.Email);
        Password = (TextInputLayout) findViewById(R.id.Password);
        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(login.this);
        Register = (TextView) findViewById(R.id.Register);
        Register.setOnClickListener(login.this);
    }
    public  void saveString (String key ,String value){
        SharedPreferences.Editor editor=
                getSharedPreferences("LoginTest",MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.apply();
    }
}