package com.hendro.event;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ParticipantsFragment extends Fragment {

    View fragment_view;
    private SwipeRefreshLayout swipeContainer;
    public List<Participant> members;

    ArrayList<String> arr;
    HashMap<String, String> mapData;

    ProgressDialog pDialog;
    Spinner sp;
    int EVENT_ID;

    public ParticipantsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_participants, container, false);

        fragment_view = view;

        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Loading...");
        pDialog.show();

        loadEvent();

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.participantSwipeRefreshLayout);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                load(EVENT_ID);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
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

        sp = (Spinner) view.findViewById(R.id.sp_participants);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String eventVal = adapterView.getItemAtPosition(i).toString();
                String idVal =  mapData.get(eventVal);

                Log.d(Cons.MY_TAG, "onItemSelected: " + idVal);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    void load(int id) {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "http://event-lcc-me.000webhostapp.com/peserta.php?action=4";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Members: ", response.toString());
                        String id, nama, kampus, whatsapp, phone, email, input;

                        members = new ArrayList<>();

                        try {
                            JSONArray jsonArray = response.getJSONArray("result");
                            members.clear();
                            if (jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject data = jsonArray.getJSONObject(i);

                                    id = data.getString("ID").toString().trim();
                                    nama = data.getString("NAME").toString().trim();
                                    kampus = data.getString("INSTITUTION").toString().trim();
                                    whatsapp = data.getString("WHATSAPP").toString().trim();
                                    phone = data.getString("PHONE").toString().trim();
                                    email = data.getString("EMAIL").toString().trim();
                                    input = data.getString("INPUT").toString().trim();

                                    members.add(new Participant(id, nama, kampus, whatsapp, phone, email, input));
                                }

                                RecyclerView recyclerView = (RecyclerView) fragment_view.findViewById(R.id.rv_participants);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                                ParticipantsAdapter mAdapter = new ParticipantsAdapter(getContext());
                                mAdapter.addAll(members);
                                recyclerView.setAdapter(mAdapter);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());

                                if (pDialog.isShowing()) pDialog.dismiss();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (members.size() == 0)
                            if (pDialog.isShowing()) pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.d("Members ", error.toString());

                Toast.makeText(getContext(),
                        error.toString(),
                        Toast.LENGTH_SHORT).show();

            }
        });

        queue.add(jsObjRequest);
    }

    void loadEvent() {
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = "https://event-lcc-me.000webhostapp.com/load_event.php";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Event: ", response.toString());
                        String id, event;

                        try {
                            JSONArray jsonArray = response.getJSONArray("result");

                            //set hashMap u/ spinner
                            mapData = new HashMap<String, String>();
                            arr = new ArrayList<String>();

                            if (jsonArray.length() != 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject data = jsonArray.getJSONObject(i);

                                    id = data.getString("id").toString().trim();
                                    event = data.getString("event").toString().trim();

                                    //taruh di hashMap u/ spinner
                                    mapData.put(event, id);
                                    arr.add(event);
                                }

                                //persiapan array adapter
                                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                        getContext(),
                                        R.layout.my_spinner, //spinner layout diubah
                                        arr);

                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                sp.setAdapter(arrayAdapter);

                            }

                            if (pDialog.isShowing()) pDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO Auto-generated method stub
                Log.d("Members ", error.toString());

                Toast.makeText(getContext(),
                        error.toString(),
                        Toast.LENGTH_SHORT).show();

            }
        });

        queue.add(jsObjRequest);
    }


}
