package com.developers.rapidmedic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> doctorNames;
    private DoctorNameAdapter doctorNameAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        doctorNames = new ArrayList<>();
        doctorNames.add("Dr. Shastri");
        doctorNames.add("Dr. Reema");
        doctorNames.add("Dr. Pooja");
        doctorNames.add("Dr. Venkat");
        doctorNames.add("Dr. Reddy");
        doctorNames.add("Dr. Singh");
        doctorNames.add("Dr. Jain");
        doctorNameAdapter = new DoctorNameAdapter(getApplicationContext(), doctorNames);
        listView.setAdapter(doctorNameAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, doctorNames.get(position) + "Welcome !", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
