package com.a45g.athena.connectivitymonitor;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class UploadDB {

    private static final String LOG_TAG = "UploadDB";


    public static void upload() {

        StorageReference mStorageRef;

        mStorageRef = FirebaseStorage.getInstance().getReference();

        final String uploadTime = HelperFunctions.getTime();

        Uri file = Uri.fromFile(
                new File("/sdcard/connectivitymonitor.db"));
        StorageReference storageReference =
                mStorageRef.child("databases/"+Singleton.getImei()+"/"+uploadTime+".db");

        storageReference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(LOG_TAG, "Uploaded database in the cloud");
                        Singleton.setLastUploadTime(uploadTime);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d(LOG_TAG, "Cannot upload database in the cloud");
                    }
                });
    }
}
