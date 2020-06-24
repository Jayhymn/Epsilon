package com.jayhymn.eckankarbackpass.AsyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;


import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.FaceMatch;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.SearchFacesByImageRequest;
import com.amazonaws.services.rekognition.model.SearchFacesByImageResult;
import com.jayhymn.eckankarbackpass.MainActivities.ConfirmationActivity;

import java.nio.ByteBuffer;
import java.util.List;

public class Rekognize extends AsyncTask<byte[], String, String> {
    private  AmazonRekognitionClient amazonRekognition;
    AWSCredentials awsCredentials;
    CompareFacesResult compareFacesResult;
    Context context;
    byte[] imageTosend;
    public static final String collectionId = "eckankar";
    private Toast toast;
    private ProgressDialog myAuth;
    List < FaceMatch > faceImageMatches;
    private String res = "";

    public Rekognize(Context context, Toast toast, ProgressDialog myAuth) {
        if (context != null) { this.context = context; }
        this.toast = toast;
        this.myAuth = myAuth;
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

        // Search collection for faces similar to the largest face in the image.
        SearchFacesByImageRequest searchFacesByImageRequest = new SearchFacesByImageRequest()
                .withCollectionId(collectionId)
                .withImage(sentImage)
                .withFaceMatchThreshold(80F)
                .withMaxFaces(3);

        SearchFacesByImageResult searchFacesByImageResult =
                amazonRekognition.searchFacesByImage(searchFacesByImageRequest);

        faceImageMatches = searchFacesByImageResult.getFaceMatches();


        //provides a list of all the faces that macth in the pictrue sent to compare
        //List<CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
            if (!faceImageMatches.isEmpty()){
                String usernameRole = "";

                //get the name of the image file and send to the next activity
                usernameRole =  faceImageMatches.get(0).getFace().getExternalImageId();
                res = usernameRole;//.substring(0, usernameRole.indexOf("_"));
            }

        return res;
    }

    @Override
    protected void onPostExecute(String response) {

        if (!res.equals("")){
                Intent intent = new Intent(context, ConfirmationActivity.class);
                intent.putExtra("name", res);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                //end the camera activity
                ((Activity)context).finish();

            //ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
            //ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        }

        else {

            toast = Toast.makeText(context,
                    "You are not yet registered for this program.\\n kindly do so",Toast.LENGTH_LONG);
            myAuth.dismiss();
            toast.show();
        }
    }
}