package com.jayhymn.eckankarbackpass.AsyncTasks;

import android.app.Activity;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.FaceRecord;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.IndexFacesRequest;
import com.amazonaws.services.rekognition.model.IndexFacesResult;
import com.amazonaws.services.rekognition.model.S3Object;

import java.io.File;
import java.util.List;

public class Collection extends AsyncTask<String, String, File> {
    private AmazonRekognitionClient amazonRekognition;
    private AWSCredentials awsCredentials;
    String photo, bucketName;
    File imgFilePath;
    Context context;

    public Collection(Context context, AWSCredentials awsCredentials){
        this.context = context;
        this.awsCredentials = awsCredentials;
    }


    @Override
    protected File doInBackground(String... strings) {
        photo = strings[0];
        bucketName = strings[1];

        //get the file path of the image
        imgFilePath = new File(strings[2]);

        // KEY and SECRET are gotten when we create an IAM user above
        amazonRekognition = new AmazonRekognitionClient(awsCredentials);
        amazonRekognition.setRegion(Region.getRegion(Regions.US_EAST_1));

        Image image = new Image()
                .withS3Object(new S3Object()
                        .withBucket(bucketName)
                        .withName(photo));

        IndexFacesRequest indexFacesRequest = new IndexFacesRequest()
                .withImage(image)
                .withCollectionId("eckankar")
                .withExternalImageId(photo)
                .withDetectionAttributes("DEFAULT");

        IndexFacesResult indexFacesResult = amazonRekognition.indexFaces(indexFacesRequest);


        List<FaceRecord> faceRecords = indexFacesResult.getFaceRecords();
        if (!faceRecords.isEmpty()){
            String username = faceRecords.get(0).getFace().getExternalImageId();
            String res = username.substring(0, username.indexOf("_"));

            //show a message to the user that their registration has been successful
            //Toast.makeText(context.getApplicationContext(), "you have been fully registered!", Toast.LENGTH_SHORT).show();
        }

        return imgFilePath;
    }

    @Override
    protected void onPostExecute (File response){
        response.delete();

        //tell media to remove cache file from the phone memory
        MediaScannerConnection.scanFile(context,
                new String[]{response.toString()}, null, null);
    }
}
