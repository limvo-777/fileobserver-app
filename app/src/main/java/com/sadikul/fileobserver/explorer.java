package com.sadikul.fileobserver;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;


public class explorer extends AppCompatActivity {
    String mCurrent;
    String mRoot;
    TextView mCurrentTxt;
    ListView mFileList;
    ArrayAdapter<String> mAdapter;
    ArrayList<String> arFiles;
    ArrayList<String> arTemp;

    private View decorView; // ==== Full Screen
    private int uiOption; // ==== Full Screen


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 액션바(타이틀바) 감추기
        setContentView(R.layout.activity_explorer);

        decorView = getWindow().getDecorView(); // ==== Full Screen 시작
        uiOption = getWindow().getDecorView().getSystemUiVisibility();

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH )
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY; // ==== Full Screen 끝


        mCurrentTxt = (TextView)findViewById(R.id.current);
        mFileList = (ListView)findViewById(R.id.filelist);



        arFiles = new ArrayList<String>();
        arTemp = new ArrayList<String>();
        mRoot = Environment.getExternalStorageDirectory().getAbsolutePath(); // SD카드 루트
        mCurrent = mRoot;



        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arFiles); // 어댑터 생성
        mFileList.setAdapter(mAdapter); // 리스트뷰에 어댑터 연결
        mFileList.setOnItemClickListener(mItemClickListener); // 리스트뷰에 리스너 연결
        refreshFiles();
        Button button=(Button)findViewById(R.id.current);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
//                mCurrentTxt=(TextView)findViewById(R.id.current);

                data.putExtra("path",mCurrent);
                Log.i("XXpath",mCurrent);
                setResult(RESULT_OK,data);
                finish();
            }
        });
    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) { // ==== Full Screen
        if( hasFocus ) {
            decorView.setSystemUiVisibility( uiOption );
        }
    }

    AdapterView.OnItemClickListener mItemClickListener =
            new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String Name = arFiles.get(position); // 클릭된 위치의 값(경로)
                    if (Name.startsWith("[") && Name.endsWith("]")) {
                        Name = Name.substring(1, Name.length()-1); // 폴더이면 [] 제거
                    }

                    String Path = mCurrent + "/" + Name; // 경로+파일명 가공
                    File f = new File(Path); // File 클래스 생성

                    if (f.isDirectory()) {
                        mCurrent = Path; // 폴더이면 mCurrent 갱신
                        refreshFiles();

                    } else {
                        mCurrent = Path;
                        refreshFiles();
//                        Toast.makeText(explorer.this, arFiles.get(position), Toast.LENGTH_SHORT).show(); // 파일이면 ... 토스트 대신 필요한 작업을 지정하면 된다.
                    }
                }
            };



    public void mOnClick(View v) {

        switch (v.getId()) {

            case R.id.btnroot:
                if (mCurrent.compareTo(mRoot) != 0) { // 루트가 아니면 루트로 가기
                    mCurrent = mRoot;
                    refreshFiles();
                }

                break;

            case R.id.btnup:
                if (mCurrent.compareTo(mRoot) != 0) { // 루트가 아닐 때만
                    int end = mCurrent.lastIndexOf("/"); // 마지막 폴더 구분자 위치
                    String uppath = mCurrent.substring(0, end); // 구분자 앞까지만 살림(부모 폴더)
                    mCurrent = uppath;
                    refreshFiles();
                }
                break;
            case R.id.current:

        }

    }



    void refreshFiles() {

        mCurrentTxt.setText(mCurrent); // 현재 path
        arFiles.clear(); // 폴더리스트 배열 삭제
        arTemp.clear(); // 파일리스트 배열 삭제
        File current = new File(mCurrent); // 현재 path의 File 클래스
        String[] files = current.list(); // 현재 path의 파일리스트(문자열) 배열

        if (files != null) { // 파일이 있으면
            String tmp = "";
            for (int i = 0; i < files.length;i++) { // 배열 전체를 출력
                tmp = files[i];
                if (
                        tmp.startsWith(".") ||
                        tmp.startsWith("LG") ||
                        tmp.endsWith(".db")
                    // || (tmp.indexof(".db.") != -1)
                ) continue; // 필터

                String Path = mCurrent + "/" + files[i];
                String Name = "";

                File f = new File(Path);

                if (f.isDirectory()) {
                    Name = "[" + files[i] + "]"; // 폴더이면 [] 덧붙임
                    arFiles.add(Name); // 폴더이면 arFiles 배열에 추가

                } else {
                    Name = files[i];
                    arTemp.add(Name); // 파일이면 arTemp 배열에 추가
                }

                // arFiles.add(Name); // 파일리스트 배열에 추가

            }

            Collections.sort(arFiles); // 폴더리스트 정렬
            Collections.sort(arTemp); // 파일리스트 정렬

            Toast.makeText(this, "폴더:"+arFiles.size()+"/파일:"+arTemp.size(), Toast.LENGTH_SHORT).show();
            arFiles.addAll(arTemp);

            // Collections.sort(arFiles); // 파일리스트 정렬
        }

        mAdapter.notifyDataSetChanged(); // 리스트뷰 갱신
    }

}