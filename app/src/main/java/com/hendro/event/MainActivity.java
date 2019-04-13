package com.hendro.event;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {

    public static Context MAIN_CONTEXT;
    public static String ACTIVE_FRAGMENT;
    public static View MAIN_VIEW;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_events:
                    fragment = new EventsFragment();
                    ACTIVE_FRAGMENT = "events";
                    break;
                case R.id.navigation_participants:
                    fragment = new ParticipantsFragment();
                    ACTIVE_FRAGMENT = "participants";
                    break;
                case R.id.navigation_users:
                    fragment = new UsersFragment();
                    ACTIVE_FRAGMENT = "users";
                    break;
            }

            return loadFragment(fragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MAIN_CONTEXT = getApplicationContext();
        MAIN_VIEW = getWindow().getDecorView().getRootView();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // kita set default nya Home Fragment
        loadFragment(new EventsFragment());
        ACTIVE_FRAGMENT = "events";
    }

    // method untuk load fragment yang sesuai
    public boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_container, fragment)
                    .commit();

            return true;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.menu_add:
                //composeMessage();
                if (ACTIVE_FRAGMENT.equals("events")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    View view = (View) getLayoutInflater().inflate(R.layout.events_add_form_layout, null);

                    dialog.setView(view);
                    dialog.setCancelable(true);
                    dialog.setIcon(R.drawable.ic_action_event_note);
                    dialog.setTitle("Event Form");

                    final EditText add_tv_event = (EditText) view.findViewById(R.id.et_add_form_event_event);
                    final EditText add_tv_description = (EditText) view.findViewById(R.id.et_add_form_event_description);
                    final EditText add_tv_date = (EditText) view.findViewById(R.id.et_add_form_event_date);
                    final EditText add_tv_time = (EditText) view.findViewById(R.id.et_add_form_event_time);

                    dialog.setNeutralButton("SAVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                String url = "http://event-lcc-me.000webhostapp.com/event.php?action=1" +
                                        "&event=" + URLEncoder.encode(add_tv_event.getText().toString(), "utf-8") +
                                        "&deskripsi=" + URLEncoder.encode(add_tv_description.getText().toString(), "utf-8") +
                                        "&tgl=" + URLEncoder.encode(add_tv_date.getText().toString(), "utf-8") +
                                        "&jam=" + URLEncoder.encode(add_tv_time.getText().toString(), "utf-8");

                                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                                JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                                        Request.Method.GET,
                                        url,
                                        null,
                                        new Response.Listener<JSONObject>() {

                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Log.d("Save: ", response.toString());

                                                Toast.makeText(getApplicationContext(),
                                                        add_tv_event.getText().toString() + " saved",
                                                        Toast.LENGTH_SHORT).show();

                                                //refresh dengan load ulang fragment
                                                //2-4-2019: dimatiin ada swipe to refresh
                                                //loadFragment(new EventFragment());
                                            }
                                        }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO Auto-generated method stub
                                        Log.d("Events: ", error.toString());

                                        Toast.makeText(getApplicationContext(),
                                                error.toString(),
                                                Toast.LENGTH_SHORT).show();

                                    }
                                });
                                queue.add(jsObjRequest);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();

                                String stackTrace = Log.getStackTraceString(e);

                                Toast.makeText(getApplicationContext(),
                                        stackTrace,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    dialog.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }else if (ACTIVE_FRAGMENT.equals("participants")){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    View view = getLayoutInflater().inflate(R.layout.participants_add_form_layout, null);

                    dialog.setView(view);
                    dialog.setCancelable(true);
                    dialog.setIcon(R.drawable.ic_action_card_membership);
                    dialog.setTitle("Participant Form");

                    final EditText d_tv_name = (EditText) view.findViewById(R.id.et_add_form_member_name);
                    final EditText d_tv_institution = (EditText) view.findViewById(R.id.et_add_form_member_institution);
                    final EditText d_tv_whatsapp = (EditText) view.findViewById(R.id.et_add_form_member_whatsapp);
                    final EditText d_tv_phone = (EditText) view.findViewById(R.id.et_add_form_member_phone);
                    final EditText d_tv_email = (EditText) view.findViewById(R.id.et_add_form_member_email);

                    dialog.setNeutralButton("SAVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                String url = "http://event-lcc-me.000webhostapp.com/peserta.php?action=1" +
                                        "&nama=" + URLEncoder.encode(d_tv_name.getText().toString(), "utf-8") +
                                        "&kampus=" + URLEncoder.encode(d_tv_institution.getText().toString(), "utf-8") +
                                        "&wa=" + URLEncoder.encode(d_tv_whatsapp.getText().toString(), "utf-8") +
                                        "&phone=" + URLEncoder.encode(d_tv_phone.getText().toString(), "utf-8") +
                                        "&email=" + URLEncoder.encode(d_tv_email.getText().toString(), "utf-8");

                                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                                JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                                        Request.Method.GET,
                                        url,
                                        null,
                                        new Response.Listener<JSONObject>() {

                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Log.d("Save: ", response.toString());

                                                Toast.makeText(getApplicationContext(),
                                                        d_tv_name.getText().toString() + " saved",
                                                        Toast.LENGTH_SHORT).show();

                                                //refresh dengan load ulang fragment
                                                //2-4-2019: dimatiin ada swipe to refresh
                                                //loadFragment(new EventFragment());
                                            }
                                        }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO Auto-generated method stub
                                        Log.d("Members ", error.toString());

                                        Toast.makeText(getApplicationContext(),
                                                error.toString(),
                                                Toast.LENGTH_SHORT).show();

                                    }
                                });
                                queue.add(jsObjRequest);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();

                                String stackTrace = Log.getStackTraceString(e);

                                Toast.makeText(getApplicationContext(),
                                        stackTrace,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    dialog.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }else if (ACTIVE_FRAGMENT.equals("users")){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    View view = getLayoutInflater().inflate(R.layout.users_add_form_layout, null);

                    dialog.setView(view);
                    dialog.setCancelable(true);
                    dialog.setIcon(R.drawable.ic_action_supervised_user_circle);
                    dialog.setTitle("User Form");

                    final EditText et_username = (EditText) view.findViewById(R.id.et_add_form_user_username);
                    final EditText et_password = (EditText) view.findViewById(R.id.et_add_form_user_password);
                    final EditText et_email = (EditText) view.findViewById(R.id.et_add_form_user_email);
                    final EditText et_phone = (EditText) view.findViewById(R.id.et_add_form_user_phone);
                    final Spinner sp_active = (Spinner) view.findViewById(R.id.sp_add_form_user_active);
                    final Spinner sp_type = (Spinner) view.findViewById(R.id.sp_add_form_user_type);

                    //setup spinner
                    String[] activeArr = {"Yes", "No"};
                    ArrayAdapter<String> activeAdapter = new ArrayAdapter<>(
                            getApplicationContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            activeArr);
                    sp_active.setAdapter(activeAdapter);

                    String[] typeArr = {"Admin", "User"};
                    ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(
                            getApplicationContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            typeArr);
                    sp_type.setAdapter(typeAdapter);

                    dialog.setNeutralButton("SAVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                String url = "http://event-lcc-me.000webhostapp.com/pengguna.php?action=1" +
                                        "&username=" + URLEncoder.encode(et_username.getText().toString(), "utf-8") +
                                        "&password=" + URLEncoder.encode(et_password.getText().toString(), "utf-8") +
                                        "&email=" + URLEncoder.encode(et_email.getText().toString(), "utf-8") +
                                        "&phone=" + URLEncoder.encode(et_phone.getText().toString(), "utf-8") +
                                        "&active=" + URLEncoder.encode(sp_active.getSelectedItem().toString(), "utf-8") +
                                        "&type=" + URLEncoder.encode(sp_type.getSelectedItem().toString(), "utf-8");

                                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                                JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                                        Request.Method.GET,
                                        url,
                                        null,
                                        new Response.Listener<JSONObject>() {

                                            @Override
                                            public void onResponse(JSONObject response) {
                                                Log.d("Save: ", response.toString());

                                                Toast.makeText(getApplicationContext(),
                                                        et_username.getText().toString() + " saved",
                                                        Toast.LENGTH_SHORT).show();

                                                //refresh dengan load ulang fragment
                                                //2-4-2019: dimatiin ada swipe to refresh
                                                //loadFragment(new EventFragment());
                                            }
                                        }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO Auto-generated method stub
                                        Log.d("Users ", error.toString());

                                        Toast.makeText(getApplicationContext(),
                                                error.toString(),
                                                Toast.LENGTH_SHORT).show();

                                    }
                                });
                                queue.add(jsObjRequest);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();

                                String stackTrace = Log.getStackTraceString(e);

                                Toast.makeText(getApplicationContext(),
                                        stackTrace,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    dialog.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }else{
                    Toast.makeText(getApplicationContext(), "Dalam Pengembangan", Toast.LENGTH_SHORT).show();
                };
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
