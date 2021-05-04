package com.example.se7a.Base;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;

public class BaseFragment extends Fragment {

    BaseActivity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity=(BaseActivity)context;
    }

    public MaterialDialog showMessage(int titleResId, int messageRecId, int posRecTxt){

        return activity.showMessage(titleResId,messageRecId,posRecTxt);
    }
    public MaterialDialog showConfirmationMessage(int titleResId,int messageRecId,int posRecTxt,MaterialDialog.SingleButtonCallback onPosAction){


        return activity.showConfirmationMessage(titleResId,messageRecId,posRecTxt,onPosAction);

    }
    public MaterialDialog showMessage(String title,String message,String posTxt){

        return  activity.showMessage(title,message,posTxt);
    }
    public MaterialDialog showProgressBar(int message){

        return  activity.showProgressBar(message);
    }
    public MaterialDialog hideProgressBar(){

        return activity.hideProgressBar();
    }

}
