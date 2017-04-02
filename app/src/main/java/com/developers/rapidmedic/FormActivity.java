package com.developers.rapidmedic;

import android.app.ProgressDialog;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    String temp;
    DatabaseReference reference;
    private Button sync;
    private TextView tempVal;
    private DatabaseReference mDatabase;
    private TextView heartVal;
    private EditText mobileNumber;
    String mob;
    private Button submit;
    String heartRaterange,mFileName;
    FirebaseDatabase database ;
    DatabaseReference myRef ;
    private MediaRecorder mRecorder;
    private FloatingActionButton fb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        TinyDB tinyDB = new TinyDB(getApplicationContext());
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
        age.setText(Integer.toString(yearThis - yearBorn));
        submit= (Button) findViewById(R.id.submit);
        mobileNumber= (EditText) findViewById(R.id.phone_number);
        fb= (FloatingActionButton) findViewById(R.id.recordfb);
        fb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                    startRecording();
                }else if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    stopRecording();
                }

                return false;
            }
        });
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        reference=database.getReference("sixth-bone-163313");
        sync= (Button) findViewById(R.id.sync);
        tempVal= (TextView) findViewById(R.id.temp_val);
        heartVal= (TextView) findViewById(R.id.heart_val);
        mFileName= Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName+="/recorded_audio.3gp";

        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HeartTempInfo().execute();
                TextView text_sync = (TextView) findViewById(R.id.text_sync);
                text_sync.setText("Values synced.");
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mob=mobileNumber.getText().toString();
                //yearThis = Calendar.getInstance().get(Calendar.YEAR);
               // yearBorn = Integer.parseInt(details.get(2));
                PatientData patientData=new PatientData("Amanjeet",temp,heartRaterange,"21","M","9717003912");
                //PatientData patientData=new PatientData(details.get(0),temp,heartRaterange,String.valueOf(yearThis-yearBorn),details.get(1),mob);
                reference.setValue(patientData);
                Toast.makeText(FormActivity.this, "रिपोर्ट भेज दी गई है", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("FormActivity", "prepare() failed");
        }

        mRecorder.start();
    }



    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        TextView record = (TextView) findViewById(R.id.record);

        record.setText("Message recorded.\nसंदेश रिकॉर्ड किया गया है.");

        InputStream stream;
        Uri downloadUrl;
        // Create a storage reference from our app
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();

        // Create a reference to "mountains.jpg"
        StorageReference mountainsRef = storageRef.child("recorded_audio.3gp");

        try {
            stream = new FileInputStream(mFileName);
            UploadTask uploadTask = mountainsRef.putStream(stream);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Log.d("FormActivity","DONE UPLOAD");
                }
            });
        }
        catch (Exception e){
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        }
    }

    private class HeartTempInfo extends AsyncTask{
        HttpURLConnection httpURLConnection,httpURLConnection1;
        BufferedReader bufferedReader,bufferedReader1;
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
            heartRaterange=heartRange.get(0)+" - "+heartRange.get(1);
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
