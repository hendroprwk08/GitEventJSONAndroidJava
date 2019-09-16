package com.hendro.event;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.CardViewHolder> {
    AlertDialog.Builder dialog;
    private List<Participant> list;
    private Context context;
    private String url;
    private int pos;

    public ParticipantsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.participants_layout, viewGroup, false);
        CardViewHolder viewHolder = new CardViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder cardViewHolder, int i) {
        pos = i;

        final String id = list.get(i).getId();
        final String name = list.get(i).getName();
        final String institution = list.get(i).getInstitution();
        final String whatsapp = list.get(i).getWhatsapp();
        final String phone = list.get(i).getPhone();
        final String email = list.get(i).getEmail();
        final String input = list.get(i).getInput();

        cardViewHolder.tv_name.setText(name);
        cardViewHolder.tv_institution.setText(institution);
        cardViewHolder.tv_phone.setText(phone);
        cardViewHolder.tv_input.setText(input);

        cardViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            TextView d_tv_name, d_tv_institution, d_tv_phone, d_tv_whatsapp, d_tv_email;

            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                view = inflater.inflate(R.layout.participants_form_layout, null);

                dialog.setView(view);
                dialog.setCancelable(true);
                dialog.setIcon(R.drawable.ic_action_card_membership);
                dialog.setTitle("Participant Form");

                d_tv_name = (EditText) view.findViewById(R.id.et_form_member_name);
                d_tv_institution = (EditText) view.findViewById(R.id.et_form_member_institution);
                d_tv_whatsapp = (EditText) view.findViewById(R.id.et_form_member_whatsapp);
                d_tv_phone = (EditText) view.findViewById(R.id.et_form_member_phone);
                d_tv_email = (EditText) view.findViewById(R.id.et_form_member_email);

                d_tv_name.setText(name);
                d_tv_institution.setText(institution);
                d_tv_whatsapp.setText(whatsapp);
                d_tv_phone.setText(phone);
                d_tv_email.setText(email);

                dialog.setNeutralButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            url = "http://event-lcc-me.000webhostapp.com/peserta.php?action=2" +
                                    "&id=" + id +
                                    "&name=" + URLEncoder.encode(d_tv_name.getText().toString(), "utf-8") +
                                    "&institution=" + URLEncoder.encode(d_tv_institution.getText().toString(), "utf-8") +
                                    "&whatsapp=" + URLEncoder.encode(d_tv_whatsapp.getText().toString(), "utf-8") +
                                    "&phone=" + URLEncoder.encode(d_tv_phone.getText().toString(), "utf-8") +
                                    "&email=" + URLEncoder.encode(d_tv_email.getText().toString(), "utf-8");

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
                                                    d_tv_name.getText().toString() + " updated",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO Auto-generated method stub
                                    Log.d("Members ", error.toString());

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
                        showDeletionDialog(d_tv_name.getText().toString());
                        /*
                        url = "http://event-lcc-me.000webhostapp.com/peserta.php?action=3&id=" + id;

                        RequestQueue queue = Volley.newRequestQueue(context);
                        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("Delete ", response.toString());

                                        Toast.makeText(context,
                                                d_tv_name.getText().toString() + " deleted",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO Auto-generated method stub
                                Log.d("Members ", error.toString());

                                Toast.makeText(context,
                                        error.toString(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });

                        queue.add(jsObjRequest);

                        dialog.dismiss();
                        */
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

            private void showDeletionDialog(final String value) {
                new AlertDialog.Builder(context)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle("Warning")
                    .setMessage("Delete " + value +"?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        url = "http://event-lcc-me.000webhostapp.com/peserta.php?action=3&id=" + id;

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
                                            value + " deleted",
                                            Toast.LENGTH_SHORT).show();

                                    //refresh fragment
                                    ParticipantsFragment fgm = new ParticipantsFragment();
                                    FragmentTransaction transaction = ((MainActivity) context).getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fl_container, fgm);
                                    transaction.addToBackStack(null);
                                    transaction.commit();

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
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context,
                                    "Deletion canceled",
                                    Toast.LENGTH_SHORT).show();

                            dialog.dismiss();

                            //showDetailDialog(view);
                        }
                    })
                    .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_institution, tv_phone, tv_input;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = (TextView) itemView.findViewById(R.id.tv_rv_name);
            tv_institution = (TextView) itemView.findViewById(R.id.tv_rv_campus);
            tv_phone = (TextView) itemView.findViewById(R.id.tv_rv_phone);
            tv_input = (TextView) itemView.findViewById(R.id.tv_rv_input);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Participant> members) {
        list = members;
        notifyDataSetChanged();
    }
}