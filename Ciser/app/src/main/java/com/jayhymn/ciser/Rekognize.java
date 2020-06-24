package com.jayhymn.ciser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.TextView;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.S3Object;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
public class Rekognize extends AsyncTask<byte[], String, String> {
    private  AmazonRekognitionClient amazonRekognition;
    AWSCredentials awsCredentials;
    Context context;
    private URL url;
    private HttpURLConnection http;
    byte[] imageTosend;
    public String photoName;
    private final String bucket = "3controx-tunji";
    private TextView textView;
    private String response;
    private ProgressDialog myAuth;

    public Rekognize(Context context, String name, TextView textView, ProgressDialog myAuth) {
        if (context != null) { this.context = context; }
        if(name !=null){ this.photoName = name; }
        this.myAuth = myAuth;
        this.textView = textView;
    }
    @Override
    protected String doInBackground(byte[]... bytes) {
        awsCredentials = new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() { return "AKIAIBTPI2U5LZD4HNVQ"; }

            @Override
            public String getAWSSecretKey() { return "+P8SPAHaptP9bsxxEtDp9ER6GNiZTtMubLv2Mb3z"; }
        };

        amazonRekognition = new AmazonRekognitionClient(awsCredentials);
        amazonRekognition.setRegion(Region.getRegion(Regions.US_EAST_1));

        imageTosend = bytes[0];

        Image sentImage = new Image().withBytes(ByteBuffer.wrap(imageTosend));
        Image target = new Image()
                .withS3Object(new S3Object()
                        .withName(photoName+".jpg")
                        .withBucket(bucket));

        CompareFacesRequest request = new CompareFacesRequest()
                .withSourceImage(sentImage)
                .withTargetImage(target);

        // Call operation
        CompareFacesResult compareFacesResult = amazonRekognition.compareFaces(request);

        //Dismiss progress dialog upon getting response
        myAuth.dismiss();

        //provides a list of all the faces that macth in the pictrue sent to compare
        List<CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();

        double confidence = 0F;
        for (CompareFacesMatch match: faceDetails){
            do{
                confidence =  match.getFace().getConfidence();
            }
            while (confidence < 90F);
         }
        String res = String.valueOf(confidence);
        return res;
    }

    @Override
    protected void onPostExecute(String resposne) {
        response = resposne;
        String msg = "You have been successfully verified with "+resposne+"% confidence value";
        textView = (TextView)((Activity)context).findViewById(R.id.awsMessage);
        textView.setText(resposne);

        Intent intent = new Intent(context,MasterActivity.class);
        intent.putExtra("authenticated user",photoName);
        context.startActivity(intent);
    }
}
