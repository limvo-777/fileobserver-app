package com.sadikul.fileobserver;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.FileObserver;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.LongSparseArray;

import junit.framework.Test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FileObserverService extends Service {
    public static String gpath = new String();
    SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
    CheckPackageNameThread checkPackageNameThread;
    public static List<String> list = new ArrayList<>();
    public static final List<String> listL = new ArrayList<>();
    public static String procName = new String();
    public static final ArrayList<TestFileObserver> sListFileObserver = new ArrayList<TestFileObserver>();
    NotificationCompat.Builder notification;
    NotificationManager mNotificationManager;
    PendingIntent pendingIntent;
    int tmp=1;
    String hTime ="default";
    String hEvent="";




    @Override
    public void onCreate(){
        super.onCreate();

        // 서비스는 한번 실행되면 계속 실행된 상태로 있는다.
        // 따라서 서비스 특성상 intent를 받아서 처리하기에 적합하지않다.
        // intent에 대한 처리는 onStartCommand()에서 처리해준다.
        Log.d("observice", "onCreate() called");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d("observice", "onStartCommand() called");
        if (intent == null) {
            return Service.START_STICKY; //서비스가 종료되어도 자동으로 다시 실행시켜줘!
        } else {
            // intent가 null이 아니다.
            // 액티비티에서 intent를 통해 전달한 내용을 뽑아낸다.(if exists)
            gpath = intent.getStringExtra("path");

            Log.d("Recieve path data", gpath);
        }
        // PendingIntent를 이용하면 포그라운드 서비스 상태에서 알림을 누르면 앱의 MainActivity를 다시 열게 된다.
        Intent testIntent = new Intent(getApplicationContext(), MainActivity.class);
        testIntent.putExtra("path",gpath.substring(gpath.lastIndexOf("/")));
        pendingIntent
                = PendingIntent.getActivity(this, 0, testIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        // 오래오 윗버젼일 때는 아래와 같이 채널을 만들어 Notification과 연결해야 한다.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("channel", "play1",
                    NotificationManager.IMPORTANCE_DEFAULT);

            // Notification과 채널 연걸
            mNotificationManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
            mNotificationManager.createNotificationChannel(channel);

            // Notification 세팅
            notification
                    = new NotificationCompat.Builder(getApplicationContext(), "channel")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("File Observing Service")
                    .setContentText("Observing Path :"+gpath)
                    .setContentIntent(pendingIntent)
                    .setContentText("");

            // id 값은 0보다 큰 양수가 들어가야 한다.
            mNotificationManager.notify(1, notification.build());
            // foreground에서 시작
            startForeground(1, notification.build());
        }

        checkPackageNameThread = new CheckPackageNameThread();
        checkPackageNameThread.start();
        if(!checkPermission())
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        File f = new File(gpath);
        monitorAllFiles(f);
        return super.onStartCommand(intent, flags, startId);
    }
    private void sendMessage(String message) {
        Intent intent = new Intent("AlertServiceFilter");
        intent.putExtra("oblog", message);
        Log.i("oblog send",message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


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
            String mEvent="";

            if (path==null)
            {
                path = mPath.substring(mPath.lastIndexOf("/")+1);
            }
            for (int i = 0; i < eventValue.length; ++i) {
                if ((eventValue[i] & event) == eventValue[i]) {
                    mEvent = eventName[i];
                    strEvents.append("Event : "+eventName[i]);
                    strEvents.append('\n');
                }
            }
            if (strEvents.toString().length()!=0 && procName.length()!=0&&!procName.contains("com.sadikul.fileobserver")){
                if ((event & FileObserver.DELETE_SELF) == FileObserver.DELETE_SELF) {
                    sListFileObserver.remove(this);
                }
                Date time = new Date();
                strEvents.append("Name  : ").append(path+"\n");
                strEvents.append("Path  : ").append(mPath+'\n');
                strEvents.append("Time  : "+format.format(time)+"\n");
                serviceInfo2();
                strEvents.append("Proc  : \n").append(procName);
                sendMessage(strEvents.toString());

                Log.i("FileObserver", strEvents.toString());


                if (list.isEmpty()) {
                    list.add(strEvents.toString());
                    hEvent+= path+" : "+mEvent+ " : Detected!\n";
                    hTime=format.format(time);
                    alertService th = new alertService();
                    th.start();
                } else {
                    if (!list.contains(strEvents.toString())) {
                        list.add(strEvents.toString());
                        hEvent+= path+" : "+mEvent+ " : Detected!\n";
                    }
                }
            }
        }
    }
    public class alertService extends Thread{
        public alertService(){ }
        @Override
        public void run(){
            try {
                Log.i("XXthread","thread start");
                TimeUnit.MILLISECONDS.sleep(500);
                work();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        public void work(){
            tmp+=1;
            notification
                    = new NotificationCompat.Builder(getApplicationContext(), "channel")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle("Alert : File Access Detected!" )
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(hEvent));
            mNotificationManager.notify(tmp, notification.build());
            hEvent="";
            list.clear();
        }
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
                    Log.i("XXmonitor",file.getAbsolutePath().toString());
                    if (file.isDirectory()) monitorAllFiles(file);
                }
            }
            throw new Exception();

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}