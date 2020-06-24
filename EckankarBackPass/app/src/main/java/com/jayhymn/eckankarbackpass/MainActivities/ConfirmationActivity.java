package com.jayhymn.eckankarbackpass.MainActivities;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jayhymn.eckankarbackpass.AsyncTasks.AsyncResponse;
import com.jayhymn.eckankarbackpass.AsyncTasks.Submit;
import com.jayhymn.eckankarbackpass.Event.EventObject;
import com.jayhymn.eckankarbackpass.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class ConfirmationActivity extends AppCompatActivity {
    TextView message;
    Button goBackToHome;
    private RequestQueue mQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comfirmation);

        message = findViewById(R.id.success_message);

        String response = getIntent().getStringExtra("name");

        String name = response.substring(0, response.indexOf("0"));
        String role = response.substring(response.indexOf("0")+1, response.indexOf("_"));

        String WEB_ADDRESS = "http://hr.watershedcorporation.com/php/eckLogin.php?";
        String THE_WEB_ADDRESS = WEB_ADDRESS +"name="+name;
        //store the from information in a database
        Submit submit = new Submit(getApplicationContext(), message);

        String key = "P";
        String value = "XemPl1fy";

        submit.execute(THE_WEB_ADDRESS, key, value, name, role);

        goBackToHome = findViewById(R.id.home);
        goBackToHome.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View view) {
                //go back to the home screen
                Intent goBackHome = new Intent(getApplicationContext(), MainActivity.class);
                goBackHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(goBackHome);
            }
        });

    }
}
