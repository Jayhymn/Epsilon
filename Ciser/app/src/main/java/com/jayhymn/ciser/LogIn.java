package com.jayhymn.ciser;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LogIn extends AppCompatActivity {
    private TextInputLayout editText;
    //private EditText editText;
    private Button userbtn, adminBtn;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth adminAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        editText = (TextInputLayout) findViewById(R.id.Edit);
        userbtn = (Button) findViewById(R.id.userBtn);
        adminBtn = (Button) findViewById(R.id.adminBtn);

        userbtn.setOnClickListener(btnClick);
        adminBtn.setOnClickListener(btnClick);
    }

    private View.OnClickListener btnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String username = String.valueOf(editText.getEditText().getText()).trim().toUpperCase();
            if(username.isEmpty()){
                editText.setError("Please type a valid username");
                editText.setErrorEnabled(true);
            }
            else{
                editText.setError(null);
                editText.setErrorEnabled(false);

                if (v == userbtn) {
                    DatabaseReference userRef = database.getReference("username/"+username);
                    final Intent intent = new Intent(getApplicationContext(), Camera.class);
                    intent.putExtra("username", username);

                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                //if the user exits then start camera activity
                                startActivity(intent);
                            }
                            else {
                                editText.setError("user not found!");
                                editText.setErrorEnabled(true);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            String error = databaseError.getDetails();
                            Toast.makeText(LogIn.this, error, Toast.LENGTH_LONG).show();
                        }
                    });
                }
                //if the admin button is clicked, take to Admin activity
                else {
                    String theToast = "Sorry, " + username + ", this feature has not been added";
                    Toast.makeText(LogIn.this, theToast, Toast.LENGTH_LONG).show();
                }
            }

        }

    };
}