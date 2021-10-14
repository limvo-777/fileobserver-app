//package com.sadikul.fileobserver;
//
//import android.app.ActivityManager;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Environment;
//import android.os.FileObserver;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//import android.support.v4.content.LocalBroadcastManager;
//import android.text.TextUtils;
//import android.util.Log;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class FileObserverService extends Service
//{
//
//
//    FileObserver mFileObserver = null;
//
//    private void sendMessage(String msg) {
//        Intent intent = new Intent("yhsIntent");
//        intent.putExtra("alertStr",msg);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//    }
//
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//    }
//}
