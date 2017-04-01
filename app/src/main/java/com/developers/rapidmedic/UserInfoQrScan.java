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
    private String doctorName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doctorName = getIntent().getExtras().getString("Doctor");
        Log.d("UserInfoQrScan",doctorName);
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
            /*SharedPreferences preferences=this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences.edit();
            editor.putString("pincode",pc);
            editor.commit();*/
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
            //finalStuff.add(uid);
            finalStuff.add(name);
            finalStuff.add(gender);
            finalStuff.add(yob);
            //finalStuff.add(house);
            //finalStuff.add(loc);
            //finalStuff.add(state);
            //finalStuff.add(pc);
            finalStuff.add(doctorName);

            TinyDB tinyDB = new TinyDB(getApplicationContext());
            tinyDB.putListString("personalDetail", finalStuff);

            Log.d("DoctorActivityQrScan", "BACKGROUND CHALA");
            String response="1";
            return response;
        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            Log.d("TAG","sca"+res);
            String r=res.trim();
            if(r.equals("1")){
                Toast.makeText(UserInfoQrScan.this,"Details Successfully Registered",Toast.LENGTH_SHORT).show();
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
