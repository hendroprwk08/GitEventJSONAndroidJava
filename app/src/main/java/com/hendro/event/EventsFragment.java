package com.hendro.event;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {
    View fragment_view;
    private SwipeRefreshLayout swipeContainer;
    public List<Event> events;
    ProgressDialog pDialog;

    public EventsFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_events, container, false);

        fragment_view = view;


        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.eventSwipeRefreshLayout);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                load();

                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        // Stop animation (This will be after 3 seconds)
                        swipeContainer.setRefreshing(false);
                    }
                }, 2000); // Delay in millis
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        return view;
    }

    void load() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://event-lcc-me.000webhostapp.com/event.php?action=4";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Events: ", response.toString());
                        String id, event, description, date, time;
                        events = new ArrayList<>();

                        try {
                            JSONArray jsonArray = response.getJSONArray("result");
                            events.clear();

                            if (jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject data = jsonArray.getJSONObject(i);

                                    id = data.getString("id").toString().trim();
                                    event = data.getString("event").toString().trim();
                                    description = data.getString("description").toString().trim();
                                    date = data.getString("date").toString().trim();
                                    time = data.getString("time").toString().trim();

                                    events.add(new Event(id, event, description, date, time));
                                }

                                RecyclerView recyclerView = (RecyclerView) fragment_view.findViewById(R.id.rv_events);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                                EventsAdapter mAdapter = new EventsAdapter(getContext());
                                mAdapter.addAll(events);
                                recyclerView.setAdapter(mAdapter);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());

                                if (pDialog.isShowing()) pDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (events.size() == 0)
                            if (pDialog.isShowing()) pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.d("Events: ", error.toString());

                Toast.makeText(getContext(),
                        error.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsObjRequest);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Loading...");
        pDialog.show();

        load();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.d("fevents", "dettach");
    }
}
