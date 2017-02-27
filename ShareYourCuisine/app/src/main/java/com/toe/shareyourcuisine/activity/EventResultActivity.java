package com.toe.shareyourcuisine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.adapter.EventRecyclerViewAdapter;
import com.toe.shareyourcuisine.adapter.RecipeRecyclerViewAdapter;
import com.toe.shareyourcuisine.model.Event;
import com.toe.shareyourcuisine.model.Recipe;
import com.toe.shareyourcuisine.service.EventService;
import com.toe.shareyourcuisine.service.RecipeService;

import org.parceler.Parcels;

import java.util.List;

/**
 * Created by HQu on 2/26/2017.
 */

public class EventResultActivity extends BaseActivity implements EventService.GetEventsByNameListener {

    private RecyclerView mEventRV;
    private EventRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mEventRV = (RecyclerView)findViewById(R.id.event_rv);
        mEventRV.setLayoutManager(new LinearLayoutManager(EventResultActivity.this));

        String query = getIntent().getStringExtra("query");
        setTitle(query);

        EventService eventService = new EventService(EventResultActivity.this);
        eventService.setGetEventsByNameListener(EventResultActivity.this);
        eventService.getEventsByName(query);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void getEventsByNameSucceed(final List<Event> events) {
        mAdapter = new EventRecyclerViewAdapter(EventResultActivity.this, events);
        mAdapter.setEventItemClickListener(new EventRecyclerViewAdapter.EventClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(EventResultActivity.this, OneEventActivity.class);
                intent.putExtra("eventId", events.get(position).getUid());
                startActivity(intent);
            }
        });
        mEventRV.setAdapter(mAdapter);
    }

    @Override
    public void getEventsByNameFail(String errorMsg) {
        Toast.makeText(EventResultActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
    }
}
