package com.sadikul.fileobserver;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    public static final List<String> list = new ArrayList<>(Arrays.asList("Camera APP Monitoring"));
    String app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent sIntent = getIntent();
        if (sIntent.getStringExtra("app")!=""){
            app = sIntent.getStringExtra("app");
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        final ListView listview = (ListView)findViewById(R.id.listviewApp);
        //리스트뷰의 어댑터 지정
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);
        final EditText titletext=(EditText)findViewById(R.id.appMonitoring);

        if (app!="")
        {
            titletext.setText(app);
            //edittext 비활성화
            titletext.setClickable(false);
            titletext.setFocusable(false);
        }


        //START 버튼
        Button appStart=(Button)findViewById(R.id.appStart);
        appStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(titletext.getText().toString().contains("Camera")){
                    Intent intent = new Intent(getApplicationContext(), AppMonitoringService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    }else{
                        startService(intent);
                    }
                }

            }
        });
        //STOP 버튼
        Button appStop=(Button)findViewById(R.id.appStop);
        appStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app="";
                titletext.setText(app);
                titletext.setClickable(true);
                titletext.setFocusable(true);

                Intent intent = new Intent(getApplicationContext(), AppMonitoringService.class);
                stopService(intent);

            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Object vo = (Object)adapterView.getAdapter().getItem(i);  //리스트뷰의 포지션 내용을 가져옴.

                app=vo.toString();
                titletext.setText(app);
                titletext.setClickable(false);
                titletext.setFocusable(false);

            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MainActivity2.this, SelectActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.putExtra("app",app);
        startActivity(intent);
    }
}