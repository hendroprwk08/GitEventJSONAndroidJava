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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
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
import java.util.Arrays;
import java.util.List;

class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.CardViewHolder> {
    AlertDialog.Builder dialog;
    private List<User> list;
    private Context context;
    private String url;
    private int pos;

    public UsersAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_layout, viewGroup, false);
        CardViewHolder viewHolder = new CardViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder cardViewHolder, int i) {
        pos = i;
        final String username = list.get(i).getUSERNAME();
        final String email = list.get(i).getEMAIL();
        final String phone = list.get(i).getPHONE();
        final String active = list.get(i).getACTIVE();
        final String type = list.get(i).getTYPE();

        cardViewHolder.tv_username.setText(username);
        cardViewHolder.tv_email.setText(email);
        cardViewHolder.tv_phone.setText(phone);
        cardViewHolder.tv_active.setText(active);
        cardViewHolder.tv_type.setText(type);

        cardViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            EditText et_username, et_email, et_phone, et_password;
            Spinner sp_active, sp_type;

            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                view = inflater.inflate(R.layout.users_form_layout, null);

                dialog.setView(view);
                dialog.setCancelable(true);
                dialog.setIcon(R.drawable.ic_action_event_note);
                dialog.setTitle("Event Form");

                et_username = (EditText) view.findViewById(R.id.et_form_user_username);
                et_email = (EditText) view.findViewById(R.id.et_form_user_email);
                et_password = (EditText) view.findViewById(R.id.et_form_user_password);
                et_phone = (EditText) view.findViewById(R.id.et_form_user_phone);
                sp_active = (Spinner) view.findViewById(R.id.sp_form_user_active);
                sp_type = (Spinner) view.findViewById(R.id.sp_form_user_type);

                et_username.setText(username);
                et_username.setEnabled(false);

                et_password.setText("[private]");
                et_email.setText(email);
                et_phone.setText(phone);

                //setup spinner
                String[] activeArr = {"Yes", "No"};
                ArrayAdapter<String> activeAdapter = new ArrayAdapter<>(
                        context,
                        android.R.layout.simple_spinner_dropdown_item,
                        activeArr);
                sp_active.setAdapter(activeAdapter);

                String[] typeArr = {"Admin", "User"};
                ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                        context,
                        android.R.layout.simple_spinner_dropdown_item,
                        typeArr);
                sp_type.setAdapter(typeAdapter);

                //set value to spinner
                sp_active.setSelection(Arrays.asList(activeArr).indexOf(active));
                sp_type.setSelection(Arrays.asList(typeArr).indexOf(type));

                dialog.setNeutralButton("SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            url = "http://event-lcc-me.000webhostapp.com/pengguna.php?action=2" +
                                    "&username=" + URLEncoder.encode(et_username.getText().toString(), "utf-8") +
                                    "&password=" + URLEncoder.encode(et_password.getText().toString(), "utf-8") +
                                    "&email=" + URLEncoder.encode(et_email.getText().toString(), "utf-8") +
                                    "&phone=" + URLEncoder.encode(et_phone.getText().toString(), "utf-8") +
                                    "&active=" + URLEncoder.encode(sp_active.getSelectedItem().toString(), "utf-8") +
                                    "&type=" + URLEncoder.encode(sp_type.getSelectedItem().toString(), "utf-8");

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
                                                    et_username.getText().toString() + " updated",
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
                        try {
                            url = "http://event-lcc-me.000webhostapp.com/pengguna.php?action=3&username=" + URLEncoder.encode(username, "utf-8");

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
                                                    username + " deleted",
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
                        }

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
        TextView tv_username, tv_email, tv_phone, tv_active, tv_type;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_username = (TextView) itemView.findViewById(R.id.tv_rv_username);
            tv_email = (TextView) itemView.findViewById(R.id.tv_rv_email);
            tv_phone = (TextView) itemView.findViewById(R.id.tv_rv_phone);
            tv_active = (TextView) itemView.findViewById(R.id.tv_rv_active);
            tv_type = (TextView) itemView.findViewById(R.id.tv_rv_type);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<User> users) {
        //list.addAll(list);
        list = users;
        notifyDataSetChanged();
    }
}