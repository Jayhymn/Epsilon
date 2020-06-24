package com.jayhymn.eckankarbackpass.Event;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.jayhymn.eckankarbackpass.MainActivities.MainActivity;
import com.jayhymn.eckankarbackpass.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Events extends AppCompatActivity {
    private BottomNavigationView navigation;
    private RelativeLayout mainLayout;
    private RecyclerView eventList;
    private RecyclerView.LayoutManager eventLayout;
    private RecyclerView.Adapter myEventAdapter;
    private TextView eventTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        // Instantiate the RequestQueue.
        //queue = Volley.newRequestQueue(getApplicationContext());

        //showEvents ();
        initialize();

        eventTxt = findViewById(R.id.newEvent);

        eventTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent createEvent = new Intent(getApplicationContext(), NewEvent.class);
                startActivity(createEvent);
            }
        });


        // the parent layout a touchSwip gesture can be attached
        mainLayout = findViewById(R.id.eventID);
        mainLayout.setOnTouchListener(new OnSwipeTouchListener(Events.this){
            @Override
            public void onSwipeRight() {
                Intent event = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(event);
                finish();
            }
        });

        navigation = findViewById(R.id.nav_bottom);
        navigation.setSelectedItemId(R.id.nav_event);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        //go back to the home screen
                        Intent goBackHome = new Intent(getApplicationContext(), MainActivity.class);
                        goBackHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(goBackHome);

                        break;
                    case R.id.nav_event:

                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.top_toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.deleteBar:
                if (!RecyclerEvents.selectedPos.isEmpty()){

                }
                return true;
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        if (!RecyclerEvents.selectedPos.isEmpty()){
            RecyclerEvents.selectedPos.clear();
            refresh();
        }
        else
            super.onBackPressed();
    }

    private void initialize(){
        eventList = findViewById(R.id.event_recycler);
        eventList.setNestedScrollingEnabled(false);
        eventList.setHasFixedSize(false);

        //prepare layout manager
        eventLayout = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL,false);

        //attach layout manager to the recycler view
        eventList.setLayoutManager(eventLayout);

        myEventAdapter = new RecyclerEvents(MainActivity.myEvents);
        eventList.setAdapter(myEventAdapter);
    }
    private void refresh(){
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}
