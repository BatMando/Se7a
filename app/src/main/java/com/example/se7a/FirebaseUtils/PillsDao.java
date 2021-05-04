package com.example.se7a.FirebaseUtils;


import com.example.se7a.Model.Pill;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import static com.example.se7a.FirebaseUtils.MyDatabase.getPillsBranch;


public class PillsDao {

    public static void InsertUser(Pill pill, OnSuccessListener onSuccessListener, OnFailureListener onFailureListener){
        DatabaseReference pillNode= getPillsBranch().push() ;
        pill.setPill_id(pillNode.getKey());
        pillNode.setValue(pill)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }
    public static Query getPillByUserId(String userId){
        Query query=getPillsBranch().orderByChild("user_id").equalTo(userId);
        return query;
    }
}
