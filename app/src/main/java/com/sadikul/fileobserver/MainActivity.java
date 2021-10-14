package com.sadikul.fileobserver;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends Activity {
    CheckPackageNameThread checkPackageNameThread;

    public static final List<String> list = new ArrayList<>();
    public static final List<String> listL = new ArrayList<>();
    public static String procName = new String();
    public static final ArrayList<TestFileObserver> sListFileObserver = new ArrayList<TestFileObserver>();


    class TestFileObserver extends FileObserver {
        private String mPath;

        int[] eventValue = new int[] {FileObserver.ACCESS, FileObserver.ALL_EVENTS, FileObserver.ATTRIB, FileObserver.CLOSE_NOWRITE,FileObserver.CLOSE_WRITE, FileObserver.CREATE,
                FileObserver.DELETE, FileObserver.DELETE_SELF,FileObserver.MODIFY,FileObserver.MOVED_FROM,FileObserver.MOVED_TO, FileObserver.MOVE_SELF,FileObserver.OPEN};
        String[] eventName = new String[] {"ACCESS", "ALL_EVENTS", "ATTRIB", "CLOSE_NOWRITE", "CLOSE_WRITE", "CREATE",
                "DELETE", "DELETE_SELF" , "MODIFY" , "MOVED_FROM" ,"MOVED_TO", "MOVE_SELF","OPEN"};


        public TestFileObserver(String path) {
            super(path);
            Log.i("XXpath", path);
            mPath = path;
            sListFileObserver.add(this);
        }


        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onEvent(int event, String path) {
            StringBuilder strEvents = new StringBuilder();
//            strEvents.append("Event : ").append('(').append(event).append(')');
            if (path==null)
            {
                path = mPath.substring(mPath.lastIndexOf("/")+1);
            }
            for (int i = 0; i < eventValue.length; ++i) {
                if ((eventValue[i] & event) == eventValue[i]) {
                    strEvents.append("Event : "+eventName[i]);
                    strEvents.append('\n');
                }
            }
            if (strEvents.toString().length()!=0 && procName!=null){
                if ((event & FileObserver.DELETE_SELF) == FileObserver.DELETE_SELF) {
                    sListFileObserver.remove(this);
                }

                strEvents.append("Name  : ").append(path+"\n").append("Path   : ").append(mPath+'\n');
                serviceInfo2();
                strEvents.append("Proc  : \n").append(procName);

                Log.i("FileObserver", strEvents.toString());
                if (list.isEmpty()) {
                    list.add(strEvents.toString());
                } else {
                    if (list.get(list.size() - 1) != strEvents.toString()) {
                        list.add(strEvents.toString());
                    }
                }
                if (list.size() > 1000) {
                    list.clear();
                }
            }


        }
    }
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);

            checkPackageNameThread = new CheckPackageNameThread();
            checkPackageNameThread.start();

            final ListView listview = (ListView)findViewById(R.id.listviewA);
            //리스트뷰의 어댑터 지정
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, listL);

            final EditText edittext=(EditText)findViewById(R.id.editTextPath);




            //START 버튼
            Button button=(Button)findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(View v) {
                    if(!checkPermission())
                        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

                    //edittext 비활성화
                    edittext.setClickable(false);
                    edittext.setFocusable(false);

                    listview.setAdapter(adapter);
                    listL.add("==== LOG START ====");


                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a_parent, View a_view, int a_position, long a_id) {

                        }
                    });
                    String path = String.valueOf(edittext.getText());
                    String mpath;
                    if (path!=""){
                        mpath = Environment.getExternalStorageDirectory() + path;
                    }else{
                        mpath = String.valueOf(Environment.getExternalStorageDirectory());
                    }
                    File f = new File(mpath);
//                    monitorAllFiles(path);
                    monitorAllFiles(f);
                }
            });

            //STOP 버튼
            Button button2=(Button)findViewById(R.id.button2);
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    edittext.setFocusableInTouchMode (true);
                    edittext.setFocusable(true);
                    for(TestFileObserver fob : sListFileObserver){
                        fob.stopWatching();
                    }
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
            //UPDATE 버튼
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


        private void monitorAllFiles(File root) {
//        private void monitorAllFiles(String path) {
        //하위경로 포함
            try {
                File[] files = root.listFiles();
                if (files.length != 0) {
                    for (File file : files) {
                        TestFileObserver fileObserver = new TestFileObserver(file.getAbsolutePath());
                        fileObserver.startWatching();
                        if (file.isDirectory()) monitorAllFiles(file);
                    }
                }
                throw new Exception();

            }catch(Exception e) {
                e.printStackTrace();
            }

//            해당 디렉터리 내 파일, 디렉터리만
//            String Opath = Environment.getExternalStorageDirectory() + path;
//            TestFileObserver fileObserver = new TestFileObserver(Opath);
//            fileObserver.startWatching();

         }
        public String serviceInfo(){
            StringBuilder strProc = new StringBuilder();
            ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = am.getRunningAppProcesses();
            strProc.append("procsize : "+runningAppProcessInfoList.size()+"\n");
            List<String> processNameList = new ArrayList<String>();
            for (ActivityManager.RunningAppProcessInfo procinfo : runningAppProcessInfoList) {
                processNameList.add(procinfo.processName);
                strProc.append(procinfo.processName+"\n");
            }

            return strProc.toString();
        }

        // 프로세스 정보 가져오기
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void serviceInfo2(){
        if(!checkPermission())
        {startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));}

    }

    private class CheckPackageNameThread extends Thread{

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void run(){
            while(true){
                if(!checkPermission()) continue;
//                System.out.println(getPackageName(getApplicationContext()));
                Log.i("XXapp",getPackageName(getApplicationContext()));
                procName=getPackageName(getApplicationContext());
                try {

                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean checkPermission(){

        boolean granted = false;

        AppOpsManager appOps = (AppOpsManager) getApplicationContext()
                .getSystemService(Context.APP_OPS_SERVICE);

        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getApplicationContext().getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (getApplicationContext().checkCallingOrSelfPermission(
                    android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        }
        else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }

        return granted;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static String getPackageName(@NonNull Context context) {

        // UsageStatsManager 선언
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        long lastRunAppTimeStamp = 0L;
        long previousRunAppTimeStamp=0L;

        // 얼마만큼의 시간동안 수집한 앱의 이름을 가져오는지 정하기 (begin ~ end 까지의 앱 이름을 수집한다)
        final long INTERVAL = 10000;
        final long end = System.currentTimeMillis();
        // 1 minute ago
        final long begin = end - INTERVAL;
        String lastProc ;
        String preProc;

        //
        LongSparseArray packageNameMap = new LongSparseArray<>();

        // 수집한 이벤트들을 담기 위한 UsageEvents
        final UsageEvents usageEvents = usageStatsManager.queryEvents(begin, end);
        // 이벤트가 여러개 있을 경우 (최소 존재는 해야 hasNextEvent가 null이 아니니까)
        while (usageEvents.hasNextEvent()) {

            // 현재 이벤트를 가져오기
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);
            // 현재 이벤트가 포그라운드 상태라면 = 현재 화면에 보이는 앱이라면
            if(isForeGroundEvent(event)) {
                // 해당 앱 이름을 packageNameMap에 넣는다.
                packageNameMap.put(event.getTimeStamp(), event.getPackageName());

                // 가장 최근에 실행 된 이벤트에 대한 타임스탬프를 업데이트 해준다.
                if(event.getTimeStamp() > lastRunAppTimeStamp) {
                    previousRunAppTimeStamp = lastRunAppTimeStamp;
                    lastRunAppTimeStamp = event.getTimeStamp();
                }
            }
        }

        lastProc = packageNameMap.get(lastRunAppTimeStamp, "").toString();
        preProc =packageNameMap.get(previousRunAppTimeStamp, "").toString();
        if (lastProc!=preProc)
            lastProc+="\n"+preProc;

        // 가장 마지막까지 있는 앱의 이름을 리턴해준다.
        return lastProc;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static boolean isForeGroundEvent(UsageEvents.Event event) {

        if(event == null) return false;

        if(BuildConfig.VERSION_CODE >= 29){
//            return event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED;
        }


        return event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND;
    }

}




