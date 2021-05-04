package com.example.se7a.FirebaseUtils;


import com.example.se7a.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import static com.example.se7a.FirebaseUtils.MyDatabase.getUsersBranch;


public class UsersDao {

    public static void InsertUser(User user, OnSuccessListener onSuccessListener, OnFailureListener onFailureListener){
        DatabaseReference userNode= getUsersBranch().push() ;
        user.setId(userNode.getKey());
        userNode.setValue(user)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }
    public static Query getUserByEmail(String Email){
        Query query=getUsersBranch().orderByChild("email").equalTo(Email);
        return query;
    }
}
