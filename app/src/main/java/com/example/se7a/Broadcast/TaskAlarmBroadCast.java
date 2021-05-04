package com.example.se7a.Broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.se7a.R;

import java.util.Calendar;

import static com.example.se7a.Broadcast.MyApplication.CHANNEL_1_ID;
import static com.example.se7a.Broadcast.MyApplication.CHANNEL_2_ID;


public class TaskAlarmBroadCast extends BroadcastReceiver {
    private NotificationManagerCompat notificationManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        String title=intent.getStringExtra("title");
        String body=intent.getStringExtra("body");

        int id=intent.getIntExtra("id",0);
        int day=intent.getIntExtra("day",0);
        int min=intent.getIntExtra("min",0);
        int hour=intent.getIntExtra("hour",0);
        int cat =intent.getIntExtra("cat",0);
        //Toast.makeText(context,"Alarm id: "+id,Toast.LENGTH_SHORT).show();
        Calendar calendar=Calendar.getInstance();
        if (calendar.get (Calendar.DAY_OF_WEEK)==day){
            if (calendar.get(Calendar.HOUR_OF_DAY)==hour&&calendar.get(Calendar.MINUTE)==min){
               // Log.e("noti",id+"");
               // Toast.makeText(context,"Alarm id: "+id,Toast.LENGTH_SHORT).show();
            if (cat==1){
                Log.e("noti",id+" pill");

                sendOnChannel1(title,body,context,id);
            }
            else if (cat==2){
                Log.e("noti",id+" exercise");

                sendOnChannel2(title,body,context,id);
            }
                //showNotification(title,body,context,id);
            }
        }
        //Log.e("noti",id+"");
        //showNotification(title,body,context,id);




    }


    public static final String channelName = "Channel Name";
    public static final String channelID = "channelAlarms1";

   /* public void showNotification(String title,String desc,Context context,int id){
        NotificationManager notificationManager=(NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(context, channelID)
                .setContentTitle(title)
                .setContentText(desc)
                .setSmallIcon(R.drawable.ic_notification_on);
        Log.e("notification shown ",id+"");
        notificationManager.notify(id,builder.build());

    }*/
    public void sendOnChannel1(String title,String message,Context context,int id) {
        notificationManager = NotificationManagerCompat.from(context);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_notification_on)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                //.setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();
        notificationManager.notify(id, notification);
        Log.e("notification shown ",id+"");
    }
    public void sendOnChannel2(String title,String message,Context context,int id) {
        notificationManager = NotificationManagerCompat.from(context);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_notification_on)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        notificationManager.notify(id, notification);
        Log.e("notification shown ",id+"");
    }


}
