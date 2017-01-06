package com.toe.shareyourcuisine.service;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.toe.shareyourcuisine.model.Event;
import com.toe.shareyourcuisine.model.EventItem;
import com.toe.shareyourcuisine.model.Post;
import com.toe.shareyourcuisine.model.PostItem;
import com.toe.shareyourcuisine.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import id.zelory.compressor.Compressor;

/**
 * Created by HQu on 12/27/2016.
 */

public class EventService {

    private static final String TAG = "ToeEventService:";
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageRef;
    private StorageReference mImgStorageRef;
    private CreateEventListener mCreateEventListener;
    private GetAllEventItemsListener mGetAllEventItemsListener;
    private Context mContext;
    private Event mEventToCreate;

    public interface CreateEventListener {
        public void createEventSucceed();
        public void createEventFail(String errorMsg);
    }

    public interface GetAllEventItemsListener {
        public void getAllEventItemsSucceed(List<EventItem> eventItems);
        public void getAllEventItemsFail(String errorMsg);
    }

    public EventService(Context context) {
        mContext = context;
    }

    public void setCreateEventListener(CreateEventListener createEventListener) {
        mCreateEventListener = createEventListener;
    }

    public void setGetAllEventItemsListener(GetAllEventItemsListener getAllEventItemsListener) {
        mGetAllEventItemsListener = getAllEventItemsListener;
    }

    public void createEvent(Event event) {
        mEventToCreate = event;
        uploadImg(mEventToCreate.getDisplayImgUrl());
    }

    public void uploadImg(String imgUrl) {
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = mFirebaseStorage.getReferenceFromUrl("gs://shareyourcuisine.appspot.com");
        File compressedImg = new Compressor.Builder(mContext).build().compressToFile(new File(imgUrl));
        Uri file = Uri.fromFile(compressedImg);
        mImgStorageRef = mStorageRef.child("images/event/" + mEventToCreate.getCreatedBy() + "/" + UUID.randomUUID().toString());
        UploadTask uploadTask = mImgStorageRef.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                mCreateEventListener.createEventFail(exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                mEventToCreate.setDisplayImgUrl(downloadUrl.toString());
                insertEventData();
            }
        });
    }

    public void insertEventData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference eventRef = firebaseDatabase.getReference("event");
        eventRef.push().setValue(mEventToCreate, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    mCreateEventListener.createEventFail(databaseError.getMessage());
                else
                    mCreateEventListener.createEventSucceed();
            }
        });
    }

    public void getAllEventItems() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference postRef = firebaseDatabase.getReference("event");
        postRef.orderByChild("createdAt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<EventItem> eventItems = new ArrayList<EventItem>();
                for(DataSnapshot eventSnapShot: dataSnapshot.getChildren()) {
                    EventItem eventItem = eventSnapShot.getValue(EventItem.class);
                    eventItem.setUid(eventSnapShot.getKey());
                    eventItems.add(eventItem);

                }
                mGetAllEventItemsListener.getAllEventItemsSucceed(eventItems);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mGetAllEventItemsListener.getAllEventItemsFail(databaseError.getMessage());
            }
        });
    }
}
