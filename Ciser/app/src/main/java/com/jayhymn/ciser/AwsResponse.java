package com.jayhymn.ciser;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

public class AwsResponse extends AppCompatActivity {
private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aws_response);

        String msg = getIntent().getExtras().getString("message");
        editText = findViewById(R.id.message_aws_response);
        editText.setText(msg);
    }
}
