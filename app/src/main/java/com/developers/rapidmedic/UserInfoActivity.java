package com.developers.rapidmedic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserInfoActivity extends AppCompatActivity {

    private Button qr;
    private String doctorName;
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        doctorName = getIntent().getExtras().getString("Doctor");

        qr = (Button) findViewById(R.id.qr);
        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(UserInfoActivity.this, UserInfoQrScan.class);
                intent.putExtra("Doctor",doctorName);
                startActivity(intent);
            }
        });
    }
}
