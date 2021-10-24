package com.sadikul.fileobserver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends Activity {

    int request_code =1;

    public static final List<String> list = new ArrayList<>();
    public static final List<String> listL = new ArrayList<>();
    String path="";



    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Intent sIntent = getIntent();
            if (sIntent.getStringExtra("path")!=""){
                path = sIntent.getStringExtra("path");
            }
            final ListView listview = (ListView)findViewById(R.id.listviewA);
            //리스트뷰의 어댑터 지정
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, listL);
            listview.setAdapter(adapter);
            final EditText edittext=(EditText)findViewById(R.id.editTextPath);

            LocalBroadcastManager.getInstance(this).registerReceiver(
                    mAlertReceiver, new IntentFilter("AlertServiceFilter")
            );
            if (path!="")
            {
                edittext.setText(path);
                //edittext 비활성화
                edittext.setClickable(false);
                edittext.setFocusable(false);
            }
            //START 버튼
            Button button=(Button)findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), FileObserverService.class);
                    path = String.valueOf(edittext.getText());
                    String mpath;
                    if (path!=""){
                        mpath = Environment.getExternalStorageDirectory() + path;
                    }else{
                        mpath = String.valueOf(Environment.getExternalStorageDirectory());
                    }

                    intent.putExtra("path",mpath);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    }else{
                        startService(intent);
                    }
                    //edittext 비활성화
                    edittext.setClickable(false);
                    edittext.setFocusable(false);

                    listview.setAdapter(adapter);
                    listL.add("==== LOG START ====");
                }
            });
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> a_parent, View a_view, int a_position, long a_id) {
                    if (list.size()!=0){
                        HashSet<String> hlist = new HashSet<>(list);
                        listL.addAll(hlist);
                        list.clear();
                    }
                    listview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            });
            //STOP 버튼
            Button button2=(Button)findViewById(R.id.button2);
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edittext.setFocusableInTouchMode (true);
                    edittext.setFocusable(true);
                    Intent intent = new Intent(getApplicationContext(), FileObserverService.class);
                    stopService(intent);
                    HashSet<String> hlist = new HashSet<>(list);
                    listL.addAll(hlist);
                    list.clear();
                    listL.add("==== LOG STOP ====");
                    listview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }
            });

            //CLEAR 버튼
            Button button3=(Button)findViewById(R.id.button3);
            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listL.clear();
                    listL.add("==== LOG CLEAR ====");
                    listview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            });
            //CHOOSE
            Button btn_select=(Button)findViewById(R.id.btn_select);
            btn_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent( MainActivity.this,explorer.class);
                    startActivityForResult(intent,request_code);
                }
            });
            //UPDATE
            Button button4=(Button)findViewById(R.id.button4);
            button4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.size()!=0){
                        HashSet<String> hlist = new HashSet<>(list);
                        listL.addAll(hlist);
                        list.clear();
                    }
                    listview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            });



        }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MainActivity.this, SelectActivity.class); //지금 액티비티에서 다른 액티비티로 이동하는 인텐트 설정
        intent.putExtra("path",path);
        startActivity(intent);
    }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data){
            if (requestCode==request_code)
            {
                if(resultCode==RESULT_OK){

                    EditText edittext=(EditText)findViewById(R.id.editTextPath);
                    String path = data.getStringExtra("path");

                    edittext.setText(path.substring(19));
                }
            }
        }

    private BroadcastReceiver mAlertReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("oblog");

            Log.i("oblog recieve", message);

            list.add(message);
        }
    };


}




