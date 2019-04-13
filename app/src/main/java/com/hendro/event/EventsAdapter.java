package com.hendro.event;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.CardViewHolder> {
    AlertDialog.Builder dialog;
    private List<Event> list;
    private Context context;
    private String url;
    private int pos;

    public EventsAdapter(Context context) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.events_layout, viewGroup, false);
        CardViewHolder viewHolder = new CardViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder cardViewHolder, int i) {
        pos = i;
        final String id = list.get(i).getId();
        final String event = list.get(i).getEvent();
        final String deskripsi = list.get(i).getDescription();
        final String tgl = list.get(i).getDate();
        final String jam = list.get(i).getTime();

        cardViewHolder.tv_event.setText(event);
        cardViewHolder.tv_tanggal.setText(tgl);
        cardViewHolder.tv_jam.setText(jam);

        cardViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            TextView d_tv_event, d_tv_description, d_tv_date, d_tv_time;

            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                view = inflater.inflate(R.layout.events_form_layout, null);

                dialog.setView(view);
                dialog.setCancelable(true);
                dialog.setIcon(R.drawable.ic_action_event_note);
                dialog.setTitle("Event Form");

                d_tv_event = (EditText) view.findViewById(R.id.et_form_event_event);
                d_tv_description = (EditText) view.findViewById(R.id.et_form_event_description);
                d_tv_date = (EditText) view.findViewById(R.id.et_form_event_date);
                d_tv_time = (EditText) view.findViewById(R.id.et_form_event_time);

                d_tv_event.setText(event);
                d_tv_description.setText(deskripsi);
                d_tv_date.setText(tgl);
                d_tv_time.setText(jam);

                dialog.setNeutralButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            url = "http://event-lcc-me.000webhostapp.com/event.php?action=2" +
                                    "&id=" + id +
                                    "&event=" + URLEncoder.encode(d_tv_event.getText().toString(), "utf-8") +
                                    "&deskripsi=" + URLEncoder.encode(d_tv_description.getText().toString(), "utf-8") +
                                    "&tgl=" + URLEncoder.encode(d_tv_date.getText().toString(), "utf-8") +
                                    "&jam=" + URLEncoder.encode(d_tv_time.getText().toString(), "utf-8");

                            RequestQueue queue = Volley.newRequestQueue(context);
                            JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                                    Request.Method.GET,
                                    url,
                                    null,
                                    new Response.Listener<JSONObject>() {

                                        @Override
                                        public void onResponse(JSONObject response) {
                                            Log.d("Save: ", response.toString());

                                            Toast.makeText(context,
                                                    d_tv_event.getText().toString() + " updated",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO Auto-generated method stub
                                    Log.d("Events: ", error.toString());

                                    Toast.makeText(context,
                                            error.toString(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                            queue.add(jsObjRequest);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();

                            String stackTrace = Log.getStackTraceString(e);

                            Toast.makeText(context,
                                    stackTrace,
                                    Toast.LENGTH_SHORT).show();
                        }

                        dialog.dismiss();
                    }
                });

                dialog.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        url = "http://event-lcc-me.000webhostapp.com/event.php?action=3&id=" + id;

                        RequestQueue queue = Volley.newRequestQueue(context);
                        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                null,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("Delete: ", response.toString());

                                        Toast.makeText(context,
                                                d_tv_event.getText().toString() + " deleted",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub
                                Log.d("Events: ", error.toString());

                                Toast.makeText(context,
                                        error.toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                        queue.add(jsObjRequest);

                        dialog.dismiss();
                    }
                });

                dialog.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView tv_event, tv_tanggal, tv_jam;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_event = (TextView) itemView.findViewById(R.id.tv_event_event);
            tv_tanggal = (TextView) itemView.findViewById(R.id.tv_event_date);
            tv_jam = (TextView) itemView.findViewById(R.id.tv_event_time);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Event> events) {
        //list.addAll(list);
        list = events;
        notifyDataSetChanged();
    }
}