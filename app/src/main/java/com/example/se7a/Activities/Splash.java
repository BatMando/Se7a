package com.example.se7a.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.se7a.Base.BaseActivity;
import com.example.se7a.Broadcast.ExampleJopService;
import com.example.se7a.DataHolder;
import com.example.se7a.Model.User;
import com.example.se7a.R;
import com.google.gson.Gson;


public class Splash extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        String userjson=getstring("user");
        Log.e("user",userjson+" ");
        if (!isOnline())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("لا يوجد اتصال بالانترنت");
            builder.setMessage("يرجي التاكد من الاتصال بشبكة الانترنت واعادة تشغيل البرنامج");
            builder.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        else if (userjson!=null){
            Gson gson=new Gson();
            DataHolder.currentUser=gson.fromJson(userjson, User.class);

            new Handler()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(activity,MainActivity.class));
                            finish();
                        }
                    },2000);
        }
        else {
            new Handler()
                    .postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(activity,login.class));
                            finish();
                        }
                    },2000);
        }

    }
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
    public String getstring(String key){
        SharedPreferences sharedPreferences=
                getSharedPreferences("LoginTest",MODE_PRIVATE);
        return sharedPreferences.getString(key,null);
    }
}