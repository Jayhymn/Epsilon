package com.jayhymn.eckankarbackpass.AsyncTasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.TextView;
import android.widget.Toast;

import com.jayhymn.eckankarbackpass.MainActivities.ConfirmationActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class Submit extends AsyncTask<String, Void, String> {
    String verified;
    Context context;
    private URL url;
    private HttpURLConnection http;
    private TextView message;
    String name, role = "";
    String strB = " ";
    String key, value, sb = "";

    public Submit(Context context,TextView message) {
        if (context != null) {
            this.context = context;
            this.message = message;
        }
    }
    public Submit(Context context) {
        if (context != null) {
            this.context = context;
        }
    }
    @Override
    protected String doInBackground(String... args) {
        try {
            url = new URL(args[0]);
            key = args[1];
            value = args[2];
            if (args.length > 3){
                name = args[3];
                role = args[4];
            }


            //the parent class passes the arguments through execute
            //set up an http connection with the arguments passed
            http = (HttpURLConnection) url.openConnection();
            //http.setChunkedStreamingMode(0);

            //enable reading and writing to server
            http.setDoOutput(true);
            http.setDoInput(true);

            http.setRequestMethod("POST");

            OutputStream output = http.getOutputStream();
            //create a writer object to post the outputstream data
            BufferedWriter os = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
            String data = URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
            os.write(data);
            os.flush();
            os.close();
            output.close();

            //after posting, get the server response body
            InputStream input = http.getInputStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(input, "iso-8859-1"));

            while ((sb = r.readLine()) != null) {
                strB += sb;
            }
            r.close();
            input.close();
            http.disconnect();
            //read the response and return as a string
            return strB;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strB;
    }

    @Override
    protected void onPostExecute(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

        if (msg.equals(" successful")) {
            if (!role.equals("")) {
                message.setText("Successfully verified " + name + " " + "with role as " + role);
            }
        }
           // Intent intent = new Intent(context, MainActivity.class);
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //context.startActivity(intent);
        else if(msg.equals("try again")){
            Toast.makeText(context, "try again", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(context, "Please. Restart the App", Toast.LENGTH_LONG).show();
    }

}
