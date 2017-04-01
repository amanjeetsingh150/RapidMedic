package com.developers.rapidmedic;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by gagan on 1/4/17.
 */

public class UserInfoQrScan extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ArrayList<String> finalStuff;
    private ZXingScannerView mScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView=new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void handleResult(Result result) {
        Log.d("DoctorActivityQrScan",result.getText());
        parseDetails(result);
        mScannerView.resumeCameraPreview(this);
    }

    private void parseDetails(Result result) {
        String res=result.getText();
        try {
            DocumentBuilderFactory doc=DocumentBuilderFactory.newInstance();
            DocumentBuilder db=doc.newDocumentBuilder();
            InputSource is=new InputSource();
            is.setCharacterStream(new StringReader(res));
            Document d = db.parse(is);
            NodeList nodes = d.getElementsByTagName("PrintLetterBarcodeData");
            Element line = (Element) nodes.item(0);
            String uid=line.getAttribute("uid");
            String name=line.getAttribute("name");
            String gender=line.getAttribute("gender");
            String yob=line.getAttribute("yob");
            String house=line.getAttribute("house");
            String loc=line.getAttribute("loc");
            String state=line.getAttribute("state");
            String pc=line.getAttribute("pc");
            SharedPreferences preferences=this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("pincode",pc);
            editor.commit();
            new Submitting().execute(uid,name,gender,yob,house,loc,state,pc);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
    private class Submitting extends AsyncTask<String,Void,String> {
        ProgressDialog progress;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress=new ProgressDialog(UserInfoQrScan.this);
            progress.setMessage("Registring....");
            progress.show();
        }

        @Override
        protected String doInBackground(String... string) {
            String uid=string[0];
            String name=string[1];
            String gender=string[2];
            String yob=string[3];
            String house=string[4];
            String loc=string[5];
            String state=string[6];
            String pc=string[7];
            finalStuff=new ArrayList<>();
            finalStuff.add(uid);
            finalStuff.add(name);
            finalStuff.add(gender);
            finalStuff.add(yob);
            finalStuff.add(house);
            finalStuff.add(loc);
            finalStuff.add(state);
            finalStuff.add(pc);

            TinyDB tinyDB = new TinyDB(getApplicationContext());
            tinyDB.putListString("personalDetail", finalStuff);

            Log.d("DoctorActivityQrScan", "BACKGROUND CHALA");
//            JSONObject jsonObject=new JSONObject();
            String response="1";
            /*try {
                jsonObject.put("uid",uid);
                jsonObject.put("name",name);
                jsonObject.put("gender",gender);
                jsonObject.put("yob",yob);
                jsonObject.put("house",house);
                jsonObject.put("loc",loc);
                jsonObject.put("state",state);
                jsonObject.put("pc",pc);
                *//*URL url = new URL("https://6824f751.ngrok.io/save-a-user"); //Enter URL here
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
                httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
                httpURLConnection.connect();
                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes(jsonObject.toString());
                wr.flush();
                wr.close();
                InputStream inputStream=httpURLConnection.getInputStream();
                if(inputStream==null){
                    Log.e("Manual","input stream is null");
                }
                StringBuffer buffer=new StringBuffer();
                String line="";
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
                while((line=bufferedReader.readLine())!=null){
                    buffer.append(line+"\n");
                }
                response=buffer.toString();
                Log.d("SubmitActivity",response);*//*
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            return response;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            Log.d("TAG","sca"+res);
            String r=res.trim();
            if(r.equals("1")){
                Toast.makeText(UserInfoQrScan.this,"User Successfully Registered",Toast.LENGTH_SHORT).show();
                progress.cancel();
                /*SharedPreferences preferences=DoctorActivityQrScan.this.getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putInt("reg",1);
                editor.commit();*/
                Intent intent=new Intent(UserInfoQrScan.this, FormActivity.class);
                startActivity(intent);
            }
        }
    }
}
