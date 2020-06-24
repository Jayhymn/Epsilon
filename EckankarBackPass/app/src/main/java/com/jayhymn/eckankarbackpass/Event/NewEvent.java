package com.jayhymn.eckankarbackpass.Event;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jayhymn.eckankarbackpass.AsyncTasks.Submit;
import com.jayhymn.eckankarbackpass.BetaClasses.Cycler_Form;
import com.jayhymn.eckankarbackpass.BetaClasses.Cycler_Form.ViewHolder;
import com.jayhymn.eckankarbackpass.DateText;
import com.jayhymn.eckankarbackpass.MainActivities.MainActivity;
import com.jayhymn.eckankarbackpass.R;
import com.jayhymn.eckankarbackpass.TimeText;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class NewEvent extends AppCompatActivity {
    private BottomNavigationView navigation;
    private TextInputLayout title;
    private EditText numberOfEvents;
    private Button submitBtn;
    public ArrayList<EventObject> newAdd;
    private LinearLayout parentFormLayout;
    private static final int ID1 = 3, ID2 = 4, ID3 = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        parentFormLayout = findViewById(R.id.title_form);

        title = findViewById(R.id.event_title);
        submitBtn = findViewById(R.id.submitForm);
        numberOfEvents = findViewById(R.id.numberOfEvent);
        //intitialize the recycler view

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*if user didn't input any title, show a required message*/

                if (title.getEditText().getText().toString().trim().equals("")){
                    title.setError("Required!");
                    title.setErrorEnabled(true);
                }
                else {
                    title.setError(null);
                    title.setErrorEnabled(false);

                    String eventTag = title.getEditText().getText().toString();

                    LinearLayout start = (LinearLayout) parentFormLayout.getChildAt(1);
                    EditText startEdit = (EditText) start.getChildAt(1);

                    LinearLayout end =
                            (LinearLayout) parentFormLayout.getChildAt(parentFormLayout.getChildCount()-1);
                    EditText endEdit = (EditText) end.getChildAt(1);

                    String eventDuration = "From " + startEdit.getText().toString()
                            + "To " + endEdit.getText().toString();
                    //Toast.makeText(getApplicationContext(), eventDuration, Toast.LENGTH_LONG).show();

                    String totalEvent = "";
                    String eventDetails = "";

                    //loop through the input filed of the from and get their input texts
                    for (int j = 1; j < parentFormLayout.getChildCount(); j++){
                        LinearLayout currentLayout = (LinearLayout) parentFormLayout.getChildAt(j);

                        EditText datetext = (EditText) currentLayout.getChildAt(1);
                        EditText starttext = (EditText) currentLayout.getChildAt(2);
                        EditText endtext = (EditText) currentLayout.getChildAt(3);

                        if (!(datetext.getText().toString().equals("") &&
                                starttext.getText().toString().equals("") &&
                                endtext.getText().toString().equals(""))){

                            eventDetails += datetext.getText().toString() +
                                    ", " + starttext.getText().toString() + ", " +
                                    endtext.getText().toString() + "/n ";
                        }
                    }
                    try {
                        if (eventDetails.equals("")){
                            Toast.makeText(getApplicationContext(), "Fill at least one of the day's event",
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            //encode all the parameters that will be passed to the url
                            String titleparam = URLEncoder.encode("title", "UTF-8") + "="
                                    + URLEncoder.encode(eventTag, "UTF-8");
                            String alldaysParam = URLEncoder.encode("alldays", "UTF-8") + "="
                                    + URLEncoder.encode(eventDetails, "UTF-8");
                            String durationParam = URLEncoder.encode("duration", "UTF-8") + "="
                                    + URLEncoder.encode(eventDuration, "UTF-8");

                            Log.d("tag",titleparam + alldaysParam + durationParam);

                            String WEB_ADDRESS = "http://hr.watershedcorporation.com/php/eckEvents.php?";
                            String THE_WEB_ADDRESS = WEB_ADDRESS + titleparam + "&"+ alldaysParam
                                    +"&"+ durationParam;

                            Submit submitEvt = new Submit(getApplicationContext());
                            String key = "P";
                            String value = "XemPl1fy";

                            submitEvt.execute(THE_WEB_ADDRESS, key, value);
                        }

                        refresh();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
             }
        });

        numberOfEvents.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int input = 1;
                try {
                    input = Integer.parseInt(String.valueOf(charSequence));
                    if(input == 0){
                        numberOfEvents.setText("1");
                        input +=1;
                    }
                }
                catch (NumberFormatException e){
                    input = 1;
                }
                if (!(parentFormLayout.getChildCount() == 0)){
                    parentFormLayout.removeViews(1, parentFormLayout.getChildCount()-1);
                }
                for (i = 1; i < input + 1; i++) {
                    moreEvents(i);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        numberOfEvents.setText("1");

        navigation = findViewById(R.id.nav_bottom);
        navigation.setSelectedItemId(R.id.nav_event);//set the selected item to event

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
                        newGuysAdded();
                        break;
                }
                return true;
            }
        });
    }

    private void newGuysAdded(){
        //go back to the home screen
        Intent goBack = new Intent(getApplicationContext(), Events.class);
        /*if (eventCount > 0){
            Bundle bundle = new Bundle();
            bundle.putSerializable("NEWADD",(Serializable)newAdd);

            goBack.putExtra("newEvent",bundle);
        }*/
        goBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(goBack);
    }
    @Override
    public void onBackPressed() {
        newGuysAdded();
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void moreEvents(int ID){
        LinearLayout layout = new LinearLayout(this);
        layout.setId(ID);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.HORIZONTAL);


        //define layout parameters for the child elements (input fileds)
        LinearLayout.LayoutParams childParam = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        childParam.weight = 0.1f;

        //create all the input fields and attach to the layout
        TextView textView = new TextView(this);
        textView.setLayoutParams(childParam);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setText("Day" + ID);

        LinearLayout.LayoutParams childParam2 = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        childParam2.weight = 0.30f;

        //let date input field have a layout paramter of 0.4
        EditText dateInput = new EditText(this);
        dateInput.setLayoutParams(childParam2);
        dateInput.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        dateInput.setHint("dd/mm/yyyy");
        dateInput.setFocusable(false);

        LinearLayout.LayoutParams childParam3 = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        childParam3.weight = 0.25f;

        EditText startTime = new EditText(this);
        startTime.setLayoutParams(childParam3);
        startTime.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        startTime.setHint("hh:mm");
        startTime.setFocusable(false);

        LinearLayout.LayoutParams childParam4 = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        childParam4.weight = 0.35f;

        EditText endTime = new EditText(this);
        endTime.setLayoutParams(childParam);
        endTime.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        endTime.setHint("hh:mm");
        endTime.setFocusable(false);

        //add event text description to the layout
        layout.addView(textView);

        //add all input fields to the initially created layout
        layout.addView(dateInput);
        layout.addView(startTime);
        layout.addView(endTime);

        new DateText(this, dateInput);
        new TimeText(this, startTime);
        new TimeText(this, endTime);

        parentFormLayout.addView(layout);
    }
    private void refresh(){
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}
