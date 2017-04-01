package com.developers.rapidmedic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class FormActivity extends AppCompatActivity {

    private ArrayList<String> details;
    private TextView age, gender, name, doctor;
    private int yearThis, yearBorn;
    String docName;

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

    }
}
