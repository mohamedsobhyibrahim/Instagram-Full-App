package com.instagram.network.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Firebase {


    //Database
    private static FirebaseDatabase database;
    private static DatabaseReference databaseReference;

    //Authentication
    private static FirebaseAuth auth;

    //Storage
    private static FirebaseStorage storage;
    private static StorageReference storageReference;


    public static DatabaseReference getDatabaseRef(){
        if (database == null){
            database = FirebaseDatabase.getInstance();
            //database.setPersistenceEnabled(true);
            databaseReference = database.getReference();
            //reference.keepSynced(true);
        }

        return databaseReference;
    }

    public static StorageReference getStorageRef(){
        if (storage == null){
            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();
        }

        return storageReference;
    }

    public static FirebaseAuth getAuthenticationRef(){
        if (auth == null){
            auth = FirebaseAuth.getInstance();
        }

        return auth;
    }
}
