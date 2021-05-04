package com.example.se7a.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.se7a.Base.BaseActivity;
import com.example.se7a.DataHolder;
import com.example.se7a.FirebaseUtils.UsersDao;
import com.example.se7a.Model.User;
import com.example.se7a.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

public class Register extends BaseActivity implements View.OnClickListener {

    protected ImageView back;
    protected TextInputLayout username;
    protected TextInputLayout Email;
    protected TextInputLayout Password;
    protected Button Register;
    User user;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_register);
        initView();
    }

    OnSuccessListener onSuccessListener=new OnSuccessListener() {
        @Override
        public void onSuccess(Object o) {
            hideProgressBar();
            showConfirmationMessage(R.string.success, R.string.User_Registered, R.string.ok, new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    startActivity( new Intent(activity,MainActivity.class));

                    DataHolder.currentUser=user;
                    Gson gson=new Gson();
                    String userJson= gson.toJson(user);
                    saveString("user",userJson);

                    finish();
                }
            });

        }
    };
    OnFailureListener onFailureListener=new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {

            hideProgressBar();
            showMessage(getString(R.string.error),e.getMessage(),getString(R.string.ok));
        }
    };



    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            startActivity( new Intent(activity,login.class));
            finish();
        } else if (view.getId() == R.id.Register) {
            final String userName = username.getEditText().getText().toString();
            final String email = Email.getEditText().getText().toString();
            final String password = Password.getEditText().getText().toString();
            final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            user =new User();
            user.setEmail(email);
            user.setUsername(userName);
            user.setPassword(password);
//checking account registered before firebase
            databaseReference.child("users")
                    .orderByChild("email")
                    .equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        username.requestFocus();
                        showMessage(R.string.error, R.string.email_register_before, R.string.ok);
                        Password.setError(null);
                        Email.setError(null);
                        username.setError(null);
                    }
                    else{
                        if(userName.length()==0){
                            username.requestFocus();
                            username.setError("اسم المستخدم فارغ");
                        }
                        else if (email.length()==0){
                            username.setError(null);
                            Email.requestFocus();
                            Email.setError("البريد الالكتروني فارغ");
                        }
                        else if(!email.matches(emailPattern))
                        {
                            username.setError(null);
                            Email.requestFocus();
                            Email.setError("بريد الكتروني خطأ");
                        }
                        else if(password.length()<5){
                            Email.setError(null);
                            username.setError(null);
                            Password.requestFocus();
                            Password.setError("كلمة السر قصيرة");
                        }
                        else {
                            Password.setError(null);
                            Email.setError(null);
                            username.setError(null);

                            UsersDao.getUserByEmail(email).addListenerForSingleValueEvent
                                    (new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChildren()) {
                                                showMessage(R.string.error, R.string.email_register_before, R.string.ok);
                                            } else {
                                                showProgressBar(R.string.loading);
                                                UsersDao.InsertUser(user, onSuccessListener, onFailureListener);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                            hideProgressBar();
                                            showMessage(getString(R.string.error), databaseError.getMessage(), getString(R.string.ok));
                                        }
                                    });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void saveString (String key ,String value){
        SharedPreferences.Editor editor=
                getSharedPreferences("LoginTest",MODE_PRIVATE).edit();
        editor.putString(key,value);
        editor.apply();
    }
    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(Register.this);
        username = (TextInputLayout) findViewById(R.id.username);
        Email = (TextInputLayout) findViewById(R.id.Email);
        Password = (TextInputLayout) findViewById(R.id.Password);
        Register = (Button) findViewById(R.id.Register);
        Register.setOnClickListener(Register.this);
    }
}