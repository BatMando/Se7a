package com.example.se7a.FirebaseUtils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyDatabase {

    private static FirebaseDatabase firebaseDatabase;
    public static FirebaseDatabase getInstance(){
        if (firebaseDatabase==null)
            firebaseDatabase= FirebaseDatabase.getInstance();
        return firebaseDatabase;
    }

    final static String Users="users";
    final static String Pills="pills";

    public static DatabaseReference getUsersBranch(){
        return getInstance().getReference(Users);
    }
    public static DatabaseReference getPillsBranch(){
        return getInstance().getReference(Pills);
    }
}
