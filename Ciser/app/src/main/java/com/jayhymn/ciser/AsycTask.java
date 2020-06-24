package com.jayhymn.ciser;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

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

public class AsycTask extends AsyncTask<String, Void, String> {
    Context context;
    private URL url;
    private HttpURLConnection http;

    public AsycTask(Context context) {
        if (context != null) {
            this.context = context;
        }
    }

    @Override
    protected String doInBackground(String... args) {
        String strB = "";
        String sb = "";
        String key, value;
        try {
            url = new URL(args[0]);
            key = args[1];
            value = args[2];
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
        return null;
    }

    @Override
    protected void onPostExecute(String msg) {
        if ("Permission Granted".equals(msg)){
            Intent intent = new Intent(context, LogIn.class);
            context.startActivity(intent);
        }
        else if ("device blocked or not registered".equals(msg)){
            //Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, LogIn.class);
            context.startActivity(intent);
        }
        else {
            Toast.makeText(context, "Nothing Here", Toast.LENGTH_LONG).show();
        }
    }

}
