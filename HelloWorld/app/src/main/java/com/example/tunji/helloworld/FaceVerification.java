package com.example.tunji.helloworld;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class FaceVerification extends AppCompatActivity {
private static final int CAMERA_PIC_REQUEST = 1337;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_verification);

        Button btnCamera = (Button) findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(openCamera, CAMERA_PIC_REQUEST);
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
            if (requestCode == CAMERA_PIC_REQUEST){
                Bitmap image = (Bitmap) data.getExtras().get("data");
                ImageView imgTaken = (ImageView) findViewById(R.id.imgTaken);
                imgTaken.setImageBitmap(image);
            }
    }
}
