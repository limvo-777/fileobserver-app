package com.sadikul.fileobserver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AppMonitoringService extends Service {
    NotificationCompat.Builder notification2;
    NotificationManager mNotificationManager2;
    PendingIntent pendingIntent2;
    List<String> list = new ArrayList<>();
    String line;
    Process process,process1;
    BufferedReader bufferedReader,bufferedReader1;
    int tmp=1000;

    public AppMonitoringService() {
    }
    @Override
    public void onCreate(){
        super.onCreate();

        // 서비스는 한번 실행되면 계속 실행된 상태로 있는다.
        // 따라서 서비스 특성상 intent를 받아서 처리하기에 적합하지않다.
        // intent에 대한 처리는 onStartCommand()에서 처리해준다.
        Log.d("appservice", "onCreate() called");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d("observice", "onStartCommand() called");
        if (intent == null) {
            return Service.START_STICKY; //서비스가 종료되어도 자동으로 다시 실행시켜줘!
        }

        // PendingIntent를 이용하면 포그라운드 서비스 상태에서 알림을 누르면 앱의 MainActivity를 다시 열게 된다.
        Intent testIntent = new Intent(getApplicationContext(), MainActivity2.class);
        testIntent.putExtra("app","Camera APP Monitoring");
        pendingIntent2
                = PendingIntent.getActivity(this, 0, testIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        // 오래오 윗버젼일 때는 아래와 같이 채널을 만들어 Notification과 연결해야 한다.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel2 = new NotificationChannel("channel2", "play1",
                    NotificationManager.IMPORTANCE_DEFAULT);

            // Notification과 채널 연걸
            mNotificationManager2 = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
            mNotificationManager2.createNotificationChannel(channel2);

            // Notification 세팅
            notification2
                    = new NotificationCompat.Builder(getApplicationContext(), "channel2")
                    .setSmallIcon(R.drawable.aperture_launcher)
                    .setContentTitle("Camera Monitoring Service")
                    .setContentIntent(pendingIntent2)
                    .setContentText("");

            // id 값은 0보다 큰 양수가 들어가야 한다.
            mNotificationManager2.notify(1000, notification2.build());
            // foreground에서 시작
            startForeground(1000, notification2.build());
        }
        alertService th = new alertService();
        th.start();


        return super.onStartCommand(intent, flags, startId);
    }
    public class alertService extends Thread{
        public alertService(){ }
        @Override
        public void run(){
            postToastMessage("run start!");
            int i =0;
            try {
                process1 = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat");
                bufferedReader1 = new BufferedReader(
                        new InputStreamReader(process1.getInputStream()));
                bufferedReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                while (true) {
                    line = bufferedReader.readLine();
                    if(line.contains("Camera2Client")&&line.contains("Opened.")){
                        list.add(line);
                        String alert=line.substring(line.lastIndexOf("com."));
                        alert=alert.substring(0,alert.lastIndexOf("(")-1);
                        String hEvent = "Proc : "+alert;
                        tmp+=1;
                        notification2
                                = new NotificationCompat.Builder(getApplicationContext(), "channel")
                                .setSmallIcon(R.drawable.aperture_launcher)
                                .setContentTitle("Alert : Camera Access Detected!" )
                                .setContentIntent(pendingIntent2)
                                .setContentText(hEvent);
                        mNotificationManager2.notify(tmp, notification2.build());
                        postToastMessage("Camera Access!");
                        i+=1;
                    }
                    if(i>200){
                        break;
                    }
                }
            } catch (IOException e) {
            }
        }
    }
    public void postToastMessage(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}