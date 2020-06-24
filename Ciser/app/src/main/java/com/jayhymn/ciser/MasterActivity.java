package com.jayhymn.ciser;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MasterActivity extends AppCompatActivity {
    RadioButton breakBtn;
    RadioGroup breakGroup;
private String authUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);

        breakBtn = findViewById(R.id.break_btn);
        breakGroup = findViewById(R.id.break_id);

        breakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (breakGroup.getVisibility() == RadioButton.GONE)
                    breakGroup.setVisibility(RadioButton.VISIBLE);
                else
                    breakGroup.setVisibility(RadioButton.GONE);
            }
        });
        authUser = getIntent().getExtras().getString("authenticated user");
    }
}
