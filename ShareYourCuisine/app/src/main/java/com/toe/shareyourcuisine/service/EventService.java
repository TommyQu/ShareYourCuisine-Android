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
import com.toe.shareyourcuisine.model.Post;
import com.toe.shareyourcuisine.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import id.zelory.compressor.Compressor;

/**
 * Created by HQu on 12/27/2016.
 */

public class EventService {

    private static final String TAG = "ToeEventService:";
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageRef;
    private StorageReference mImgStorageRef;
    private CreateEventListener mCreateEventListener;
    private GetAllEventsListener mGetAllEventsListener;
    private GetEventByIdListener mGetEventByIdListener;
    private Context mContext;
    private Event mEventToCreate;

    public interface CreateEventListener {
        public void createEventSucceed();
        public void createEventFail(String errorMsg);
    }

    public interface GetAllEventsListener {
        public void getAllEventsSucceed(List<Event> events);
        public void getAllEventsFail(String errorMsg);
    }

    public interface GetEventByIdListener {
        public void getEventByIdSucceed(Event event);
        public void getEventByIdFail(String errorMsg);
    }

    public EventService(Context context) {
        mContext = context;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
    }

    public void setCreateEventListener(CreateEventListener createEventListener) {
        mCreateEventListener = createEventListener;
    }

    public void setGetAllEventsListener(GetAllEventsListener getAllEventsListener) {
        mGetAllEventsListener = getAllEventsListener;
    }

    public void setGetEventByIdListener(GetEventByIdListener getEventByIdListener) {
        mGetEventByIdListener = getEventByIdListener;
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
        mImgStorageRef = mStorageRef.child("images/event/" + mEventToCreate.getCreatedUserId() + "/" + UUID.randomUUID().toString());
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
        DatabaseReference eventRef = mFirebaseDatabase.getReference("event");
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

    public void getAllEvents() {
        DatabaseReference eventRef = mFirebaseDatabase.getReference("event");
        eventRef.orderByChild("createdAt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Event> events = new ArrayList<Event>();
                for(DataSnapshot eventSnapShot: dataSnapshot.getChildren()) {
                    Event event = eventSnapShot.getValue(Event.class);
                    event.setUid(eventSnapShot.getKey());
                    events.add(event);

                }
                mGetAllEventsListener.getAllEventsSucceed(events);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mGetAllEventsListener.getAllEventsFail(databaseError.getMessage());
            }
        });
    }

    public void getEventById(String uid) {
        final DatabaseReference eventRef = mFirebaseDatabase.getReference("event");
        eventRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                mGetEventByIdListener.getEventByIdSucceed(event);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mGetEventByIdListener.getEventByIdFail(databaseError.getMessage());
            }
        });
    }
}
