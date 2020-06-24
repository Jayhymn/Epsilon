package com.jayhymn.eckankarbackpass.MainActivities;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.jayhymn.eckankarbackpass.AsyncTasks.Collection;
import com.jayhymn.eckankarbackpass.AsyncTasks.Submit;
import com.jayhymn.eckankarbackpass.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Schedule_Activity extends AppCompatActivity{
    ImageView imgView;
    RadioGroup radioGroup;
    RadioButton visitor, staff;
    Button button;
    TextInputEditText name;
    Spinner spinner = null;
    private EditText /*date,*/ host, role = null;
    String mHost, mRole, mSpinner = "";
    private LinearLayout parentView;
    private LinearLayout dynamic;
    private long timestamp;
    String imgExtId;
    int id = 190;
    CheckBox checkBox;
    final static String bucketName = "3controx-tunji";
    //final Calendar myCalendar = Calendar.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Bundle bundle = getIntent().getExtras();
        //get the file path
        final Uri uri = Uri.parse(bundle.getString("filepath"));

        final File imageFile = new File(String.valueOf(uri));
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(),bmOptions);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final byte[] byteArray = stream.toByteArray();

        name = findViewById(R.id.name);

        imgView = findViewById(R.id.imgView);
        imgView.setImageBitmap(bitmap);//display the image

        radioGroup = findViewById(R.id.radioGrp);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int choice = radioGroup.getCheckedRadioButtonId();
                RadioButton button = (RadioButton) findViewById(choice);
                String selection = button.getText().toString();

                renderSpinner(selection);
            }
        });

        visitor = findViewById(R.id.visitor);
        staff = findViewById(R.id.backstageStf);
        button = findViewById(R.id.register);

        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
            @Override
            public void onClick(View view) {
                //if (date == null){ mDate = "";} else { mDate = date.getText().toString();}
                if (host == null){ mHost =""; } else { mHost = host.getText().toString(); }
                if (role == null){ mRole = ""; } else { mRole= role.getText().toString().trim(); }
                if (spinner == null){ mSpinner =""; }
                else { mSpinner = spinner.getSelectedItem().toString(); }

                upload(imageFile);

                String theName = enCOdeInput("name", name.getText().toString());

                String theRole = enCOdeInput("role", mRole);
                String theSpinner = enCOdeInput("spinner", mSpinner);
                String theHost = enCOdeInput("host", mHost);

                String WEB_ADDRESS = "http://hr.watershedcorporation.com/php/eckSignUp.php?";
                String THE_WEB_ADDRESS = WEB_ADDRESS + theName + "&" + theName + "&"
                        + theRole + "&" + theSpinner + "&" + theHost;


                //store the from information in a database
                Submit submit = new Submit(getApplicationContext());

                String key = "P";
                String value = "XemPl1fy";

                submit.execute(THE_WEB_ADDRESS, key, value);
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void renderSpinner(String action) {
        //get the first linearlayout below id 'allbtns' and add the spinner to it
        parentView = (LinearLayout) findViewById(R.id.spinnerLayout);
        spinner = new Spinner(this);
        final String[] myArraySpinner = {"Invite", "Schedule"};

        ArrayAdapter spinnerAdapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, myArraySpinner);
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view

        LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        spinner.setLayoutParams(params);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String spinnerText = spinner.getSelectedItem().toString();
                if (spinnerText.equals("Schedule")){
                    host.setText("");
                    host.setFocusable(false);
                }
                else if (spinnerText.equals("Invite")){
                    host.setFocusable(true);
                    host.setFocusableInTouchMode(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //get the LinearLayout
        dynamic = findViewById(R.id.dynamic);

        host = new EditText(this);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        host.setLayoutParams(params2);
        host.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        host.setHint("Host");

    if (action.equals("Visitor")){
        dynamic.removeAllViews();

        parentView.addView(spinner);
        dynamic.addView(host);
        }
        else {

        role = new EditText(this);
        RelativeLayout.LayoutParams params3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params3.addRule(RelativeLayout.BELOW, R.id.allbtns);
        params3.setMargins(50, 10, 50, 10);

        role.setLayoutParams(params3);
        role.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        role.setHint("role");


        parentView.removeAllViews();
        dynamic.removeAllViews();

        //add textview for hostname input
        dynamic.addView(role);
        }
    }
    public void upload (final File uri) {
        // KEY and SECRET are gotten when we create an IAM user above
        final AWSCredentials awsCredentials = new AWSCredentials() {
            @Override
            public String getAWSAccessKeyId() {
                return "AKIAIBTPI2U5LZD4HNVQ";
            }

            @Override
            public String getAWSSecretKey() {
                return "+P8SPAHaptP9bsxxEtDp9ER6GNiZTtMubLv2Mb3z";
            }
        };
        AmazonS3Client s3Client = new AmazonS3Client(awsCredentials);

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .build();

            timestamp = System.currentTimeMillis();
            imgExtId = name.getText().toString().trim() + "0" + mRole + "_"+ String.valueOf(timestamp)+".jpg";
        // image is being uploaded to s3 bucket
        TransferObserver myUpload =
                transferUtility.upload(bucketName , imgExtId, uri);
        myUpload.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                String response;
                if (TransferState.COMPLETED == state) {

                    new Collection(getApplicationContext(),awsCredentials)
                            .execute(imgExtId, bucketName, uri.toString());
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;
            }

            @Override
            public void onError(int id, Exception ex) {

            }
        });
    }

    private String enCOdeInput(String title, String value){
        String enCodedString ="";
        try {
            enCodedString = URLEncoder.encode(title, "UTF-8") + "="
                    + URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return enCodedString;
    }
}
