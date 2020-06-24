package com.jayhymn.eckankarbackpass.MainActivities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jayhymn.eckankarbackpass.Event.EventObject;
import com.jayhymn.eckankarbackpass.Event.Events;
import com.jayhymn.eckankarbackpass.Event.OnSwipeTouchListener;
import com.jayhymn.eckankarbackpass.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity{
    private Button registerBtn,
                    signInBtn;
    private String actionType;
    private BottomNavigationView navigation;
    private LinearLayout mainLayout;
    public static ArrayList<EventObject> myEvents;
    public static ArrayList<String> dropList;
    private RequestQueue mQueue;
    private Spinner spinner;
    ArrayAdapter spinnerAdapter;
    String date, event;
    private static final int REQUEST_CODE = 221;

    /*String[] permissions = new String[]{
            Manifest.permission_group.CAMERA,
            Manifest.permission_group.STORAGE
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dropList = new ArrayList<>();

        // Instantiate the RequestQueue.
        mQueue = Volley.newRequestQueue(this);

        spinner = findViewById(R.id.eventSpinner);
        spinnerAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, dropList);
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        myEvents = new ArrayList<>();

        showEvents();

        // the parent layout a touchSwip gesture can be attached
        mainLayout = findViewById(R.id.mainID);
        mainLayout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this){
            @Override
            public void onSwipeLeft() {
                Intent event = new Intent(getApplicationContext(), Events.class);
                startActivity(event);
            }
        });



        navigation = findViewById(R.id.nav_bottom);
        //navigation.setSelectedItemId(R.id.nav_home);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        break;
                    case R.id.nav_event:
                        Intent event = new Intent(getApplicationContext(),Events.class);
                        startActivity(event);

                        break;
                }
                return true;
            }
        });

        registerBtn = findViewById(R.id.register);
        signInBtn = findViewById(R.id.signIn);

        registerBtn.setOnClickListener(btnClick);
        signInBtn.setOnClickListener(btnClick);

    }

    //click listener method for both buttons
    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Button x = (Button)view;
            //get the text on the button clicked
            actionType = x.getText().toString();

            checkPermission();
        }
    };
    private void showEvents (){
        String url = "http://hr.watershedcorporation.com/php/eckEventRecord.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            date = response.getString("Date");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //loop through all the records from the response
                        for (int i = 1; i < response.length(); i++){
                            try {
                                JSONObject key = response.getJSONObject(String.valueOf(i));

                                //create EventObjects from the server response and add to the arrayList
                                EventObject object = new EventObject(key.getString("title"),
                                        key.getString("duration"),
                                        key.getString("details"),
                                        Integer.valueOf(key.getString("count")));

                                //add the newly created Event to the arrayList
                                myEvents.add(object);
                                dropList.add(object.getTitle());

                                spinnerAdapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                Log.d("error", e.toString());
                                Toast.makeText(getApplicationContext(), "check your internet connection",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    private void checkPermission(){
           if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
           !=  PackageManager.PERMISSION_GRANTED){
               ActivityCompat.requestPermissions(this, new String[]
                       {Manifest.permission.CAMERA}, REQUEST_CODE);
           }
           else
               click();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_CODE:{
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    click();
                }
                else
                    Toast.makeText(getApplicationContext(),
                            "You've denied access to use some of the app's key features",
                            Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void click (){
        //get the spinner selection
        try{
            event = spinner.getSelectedItem().toString();
        }
        catch (NullPointerException e){

        }

            /*find the index of the selection in the array and then use this index to get the object
            that contains it*/
        String details = myEvents.get(dropList.indexOf(event)).getDuration();
        String [] dates = details.split(" ");

        if (actionType.equals("Sign In")){
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date presentDate = null;
            try {
                //get satrt and end dates of the event
                Date eventDate1 = sdf.parse(dates[1]);
                Date eventDate2 = sdf.parse(dates[3]);

                presentDate = sdf.parse(date);

                if ((presentDate.after(eventDate1) && presentDate.before(eventDate2))
                        ||(presentDate.equals(eventDate1)||(presentDate.equals(eventDate1)))){

                    Intent intent = new Intent(getApplicationContext(), Camera.class);
                    intent.putExtra("action", actionType);

                    //start the camera activity
                    startActivity(intent);
                }
                else
                    Toast.makeText(getApplicationContext(),
                            "You can't sign In to " + event, Toast.LENGTH_SHORT).show();

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        else {
            Intent intent = new Intent(getApplicationContext(), Camera.class);
            intent.putExtra("action", actionType);

            //start the camera activity
            startActivity(intent);
        }
    }
    /*
    view.setOnTouchListener(new OnSwipeTouchListener(context) {
        @Override
        public void onSwipeLeft() {
            // Whatever
        }
    });*/
}