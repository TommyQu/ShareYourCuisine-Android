package com.toe.shareyourcuisine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.activity.CreateEventActivity;
import com.toe.shareyourcuisine.activity.EventResultActivity;
import com.toe.shareyourcuisine.activity.MainActivity;
import com.toe.shareyourcuisine.activity.OneEventActivity;
import com.toe.shareyourcuisine.activity.RecipeResultActivity;
import com.toe.shareyourcuisine.adapter.EventRecyclerViewAdapter;
import com.toe.shareyourcuisine.model.Event;
import com.toe.shareyourcuisine.model.Recipe;
import com.toe.shareyourcuisine.service.EventService;
import com.toe.shareyourcuisine.service.RecipeService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HQu on 12/27/2016.
 */

public class EventFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, EventService.GetAllEventsListener, EventService.GetEventsByNameListener {

    private static final String TAG = "ToeEventFragment:";
    private RecyclerView mEventRV;
    private EventRecyclerViewAdapter mAdapter;
    private SwipeRefreshLayout mEventSRL;
    private FloatingActionButton mCreateEventFAB;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event, container, false);
        setHasOptionsMenu(true);
        MainActivity.mSearchView.setSuggestions(getResources().getStringArray(R.array.suggestions));
        mEventRV = (RecyclerView)rootView.findViewById(R.id.event_rv);
        mEventRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEventSRL = (SwipeRefreshLayout)rootView.findViewById(R.id.event_srl);

        mCreateEventFAB = (FloatingActionButton) rootView.findViewById(R.id.create_event_fab);
        mCreateEventFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((MainActivity)getActivity()).getAuth().getCurrentUser() != null) {
                    Intent intent = new Intent(getActivity(), CreateEventActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Please log in!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        mEventSRL.setOnRefreshListener(this);
        mEventSRL.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorRed), ContextCompat.getColor(getActivity(), R.color.colorAccent), ContextCompat.getColor(getActivity(), R.color.colorOrange));
        getAllEvents();
        MainActivity.mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getActivity(), EventResultActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getEventsByName(newText);
                return false;
            }
        });
        return rootView;
    }

    @Override
    public void onRefresh() {
        getAllEvents();
    }

    public void getAllEvents() {
        mEventSRL.setRefreshing(true);
        EventService eventService = new EventService(getActivity());
        eventService.setGetAllEventsListener(this);
        eventService.getAllEvents();
    }

    @Override
    public void getAllEventsSucceed(final List<Event> events) {
        mEventSRL.setRefreshing(false);
        mAdapter = new EventRecyclerViewAdapter(getActivity(), events);
        mAdapter.setEventItemClickListener(new EventRecyclerViewAdapter.EventClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(getActivity(), OneEventActivity.class);
                intent.putExtra("eventId", events.get(position).getUid());
                startActivity(intent);
            }
        });

        mEventRV.setAdapter(mAdapter);
        mEventSRL.setRefreshing(false);
    }

    @Override
    public void getAllEventsFail(String errorMsg) {
        mEventSRL.setRefreshing(false);
        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
    }

    public void getEventsByName(String name) {
        EventService eventService = new EventService(getActivity());
        eventService.setGetEventsByNameListener(this);
        eventService.getEventsByName(name);
    }

    @Override
    public void getEventsByNameSucceed(List<Event> events) {
        List<String> namesList = new ArrayList<>();
        for(Event event: events) {
            namesList.add(event.getTitle());
        }
        String[] names = namesList.toArray(new String[0]);
        MainActivity.mSearchView.setSuggestions(names);
    }

    @Override
    public void getEventsByNameFail(String errorMsg) {
        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
    }
}
