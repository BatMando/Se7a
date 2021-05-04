package com.example.se7a.Base;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

public class BaseActivity extends AppCompatActivity {

     public  AppCompatActivity activity;
    MaterialDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=this;


    }

    public MaterialDialog showMessage(int titleResId,int messageRecId,int posRecTxt){


        dialog=new MaterialDialog.Builder(this).title(titleResId)
                .content(messageRecId)
                .positiveText(posRecTxt)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();

        return dialog;

    }
    public MaterialDialog showConfirmationMessage(int titleResId,int messageRecId,int posRecTxt,MaterialDialog.SingleButtonCallback onPosAction){


        dialog=new MaterialDialog.Builder(this).title(titleResId)
                .content(messageRecId)
                .positiveText(posRecTxt)
                .onPositive(onPosAction)
                .show();

        return dialog;

    }
    public MaterialDialog showMessage(String title,String message,String posTxt){

        dialog=new MaterialDialog.Builder(this).title(title)
                .content(message)
                .positiveText(posTxt)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
        return dialog;
    }
    public MaterialDialog showProgressBar(int message) {
        dialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .content(message)
                .cancelable(false)
                .show();

        return dialog;
    }
    public MaterialDialog hideProgressBar(){

        if(dialog!=null&&dialog.isShowing())
            dialog.dismiss();


        return dialog;
    }

}
