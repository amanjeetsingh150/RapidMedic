package com.developers.rapidmedic;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class FormActivity extends AppCompatActivity {

    private ArrayList<String> details;
    private TextView age, gender, name, doctor;
    private int yearThis, yearBorn;
    String docName;

    private Button sync;
    private TextView tempVal;
    private TextView heartVal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        /*TinyDB tinyDB = new TinyDB(getApplicationContext());
        details = tinyDB.getListString("personalDetail");

        doctor = (TextView) findViewById(R.id.doc_head);
        docName = details.get(3);
        doctor.setText(docName);
        Log.d("FormActivity", docName);

        name = (TextView) findViewById(R.id.name);
        name.setText(details.get(0));

        gender = (TextView) findViewById(R.id.gender);
        if (details.get(1).equals("M"))
            gender.setText("Male");
        else
            gender.setText("Female");

        age = (TextView) findViewById(R.id.age);
        yearThis = Calendar.getInstance().get(Calendar.YEAR);
        yearBorn = Integer.parseInt(details.get(2));
        age.setText(Integer.toString(yearThis - yearBorn));*/

        sync= (Button) findViewById(R.id.sync);
        tempVal= (TextView) findViewById(R.id.temp_val);
        heartVal= (TextView) findViewById(R.id.heart_val);
        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HeartTempInfo().execute();
            }
        });
    }
    private class HeartTempInfo extends AsyncTask{
        HttpURLConnection httpURLConnection,httpURLConnection1;
        BufferedReader bufferedReader,bufferedReader1;
        String temp;
        ArrayList<String> heartRange=new ArrayList<>();
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog=new ProgressDialog(FormActivity.this);
            progressDialog.setMessage("Updating...");
            progressDialog.show();
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                //body Temp
                String response="";
                URL url=new URL("https://api.thingspeak.com/channels/251819/fields/1.json?results=2");
                httpURLConnection= (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                InputStream inputStream=httpURLConnection.getInputStream();
                bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer=new StringBuffer();
                String line;
                while((line=bufferedReader.readLine())!=null){
                    buffer.append(line);
                }
                response=buffer.toString();
                if(response.length()>0){
                    parseInfo(response);
                }
                String heartResponse;
                URL url1=new URL("https://api.thingspeak.com/channels/251819/fields/2.json?results=2");
                httpURLConnection1= (HttpURLConnection) url1.openConnection();
                httpURLConnection1.setRequestMethod("GET");
                httpURLConnection1.connect();
                InputStream inputStream1=httpURLConnection1.getInputStream();
                bufferedReader1=new BufferedReader(new InputStreamReader(inputStream1));
                StringBuffer buffer1=new StringBuffer();
                String line1;
                while((line1=bufferedReader1.readLine())!=null){
                    buffer1.append(line1);
                }
                heartResponse=buffer1.toString();
                Log.d("FormActivity","heartRate "+heartResponse);
                if(heartResponse.length()>0){
                    parseHeartDetails(heartResponse);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            tempVal.setText(""+temp);
            heartVal.setText(""+heartRange.get(0)+" - "+heartRange.get(1));
            progressDialog.cancel();
        }

        private void parseInfo(String response){
            try {
                JSONObject res=new JSONObject(response);
                JSONArray arr=res.getJSONArray("feeds");
                for(int i=0;i<arr.length();i++){
                    JSONObject obj=arr.getJSONObject(i);
                    temp=obj.getString("field1");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private void parseHeartDetails(String heartResponse){
            try{
                JSONObject results=new JSONObject(heartResponse);
                JSONArray array=results.getJSONArray("feeds");
                for(int j=0;j<array.length();j++){
                    JSONObject obj=array.getJSONObject(j);
                    Log.d("FormActivity",""+obj.getString("field2"));
                    heartRange.add(obj.getString("field2"));
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
