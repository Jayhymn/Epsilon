package com.jayhymn.ciser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AnonymousAWSCredentials;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;


public class Camera extends AppCompatActivity {
    private static final String TAG = Camera.class.getSimpleName();
    final int REQUEST_IMAGE_CAPTURE = 101;
    String user;
    ProgressDialog myAuth;
    private Button button;
    private ImageView imageView;
    private String sourceImage;
    private String targetImage;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ByteBuffer sourceImageBytes=null;
        getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imageView = (ImageView)findViewById(R.id.takenPic);
        button = (Button)findViewById(R.id.btnCamera);

        //enable button to open camera
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        //prepare a progress dialog
        myAuth = new ProgressDialog(this);

        //get the name of the user from previous activity and use it as the name of the picture
        if(getIntent().hasExtra("username")){
            user = getIntent().getExtras().getString("username");
            takePicture();
        }
        else {
            Toast.makeText(getApplicationContext(),"Nothing here",Toast.LENGTH_LONG).show();
        }
    }

    //open camera intent
    public void takePicture() {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(picIntent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(picIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultStatus, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultStatus == RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap imgBit = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imgBit);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imgBit.compress(Bitmap.CompressFormat.JPEG,100,stream);

            byte[] imageBytes = stream.toByteArray();

           // byte[] imgAuth = Base64.encode(imageBytes, Base64.DEFAULT);//(String.valueOf(imgBit));
            myAuth.setMessage("Auhtenticaing user "+user);
            myAuth.show();

            String username = user;
            //textView = findViewById(R.id.awsMessage);

            //create an async task object from class Rekognize
            Rekognize rekognize = new Rekognize(this,username,textView,myAuth);
            //execute the async task to compare the user's photo with the one on aws cloud service
            rekognize.execute(imageBytes);

            return;
            }
    }
}
