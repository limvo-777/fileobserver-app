package com.sadikul.fileobserver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.HashSet;

public class SelectActivity extends AppCompatActivity {
    int request_code =1;
    String path="";
    String app="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        Intent sIntent = getIntent();
        if (sIntent.getStringExtra("path")!=""){
            path = sIntent.getStringExtra("path");
        }
        if (sIntent.getStringExtra("app")!=""){
            app = sIntent.getStringExtra("app");
        }

        ImageButton fileMon=(ImageButton) findViewById(R.id.fileMon);
        fileMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( SelectActivity.this,MainActivity.class);
                intent.putExtra("path",path);
                startActivityForResult(intent,request_code);
            }
        });
        ImageButton appMon=(ImageButton) findViewById(R.id.appMon);
        appMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( SelectActivity.this,MainActivity2.class);
                intent.putExtra("app",app);
                startActivityForResult(intent,request_code);
            }
        });
    }
}