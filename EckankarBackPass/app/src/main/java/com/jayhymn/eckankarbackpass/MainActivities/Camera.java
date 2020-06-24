package com.jayhymn.eckankarbackpass.MainActivities;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.jayhymn.eckankarbackpass.AsyncTasks.Rekognize;
import com.jayhymn.eckankarbackpass.R;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;


public class Camera extends AppCompatActivity {
    private static final String TAG = Camera.class.getSimpleName();
    final int REQUEST_IMAGE_CAPTURE = 101;
    ProgressDialog myAuth;
    private Button button;
    long timestamp;
    String currentPhotoPath;
    private ImageView imageView;
    String whatToDo, details = "";
    Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent.hasExtra("action")){
            whatToDo = intent.getStringExtra("action");
        }

        takePicture();

        ByteBuffer sourceImageBytes = null;
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
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        timestamp = System.currentTimeMillis();
        String imageFileName = "img_" + String.valueOf(timestamp);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //open camera intent
    public void takePicture() {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (picIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.jayhymn.eckankarbackpass.fileprovider",
                        photoFile);
                picIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(picIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onActivityResult(int requestCode, int resultStatus, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultStatus == RESULT_OK) {
            //Bundle extras = data.getExtras();

            File file = new File(currentPhotoPath);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media
                        .getBitmap(getContentResolver(), Uri.fromFile(file));
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Bitmap imgBit = (Bitmap) extras.get("data");
            imageView.setImageBitmap(bitmap);

            myAuth.setMessage("auhtenticaing...");
            myAuth.show();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] imageBytes = stream.toByteArray();

            // byte[] imgAuth = Base64.encode(imageBytes, Base64.DEFAULT);//(String.valueOf(imgBit));

            if (whatToDo.equals("Sign In")) {
                //create an async task object from class Rekognize
                Rekognize rekognize = new Rekognize(this, toast, myAuth);
                //execute the async task to compare the user's photo with the one on aws cloud service
                rekognize.execute(imageBytes);
                file.delete();//delete the file after use from the phone memory

                //tell media to remove cache file from the phone memory
                MediaScannerConnection.scanFile(getApplicationContext(),
                        new String[]{file.toString()}, null, null);
            } else
                if (whatToDo.equals("Register")) {
                    Intent intent = new Intent(this, Schedule_Activity.class);

                    //send the file path and file in as bytes of array as different intents
                    intent.putExtra("filepath", file.toString());
                    startActivity(intent);
                    finish();
            }
            else {
                Toast.makeText(getApplicationContext(), "something went wrong", Toast.LENGTH_SHORT).show();
            }

            return;
        }
    }
}