package com.example.vihaan.whatsappclone.ui.verifyPhoneScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.vihaan.whatsappclone.R;
import com.example.vihaan.whatsappclone.ui.createUserScreen.CreateUserActivity;

/**
 * Created by vihaan on 16/06/17.
 */

public class VerifyPhoneActivity extends AppCompatActivity implements View.OnClickListener {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        findViewById(R.id.nextBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(this, CreateUserActivity.class);
        startActivity(intent);

    }
}


