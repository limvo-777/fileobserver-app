package com.sadikul.fileobserver;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FileObserverService extends Service
{


    FileObserver mFileObserver = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("obService","started");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();


        Log.d("obService","onCreate");
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d("obService yhs1",path);



        mFileObserver = new FileObserver(path) {
            @Override
            public void onEvent(int event, @Nullable String path) {
                ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = am.getRunningAppProcesses();
                List<String> processNameList = new ArrayList<String>();

                for (ActivityManager.RunningAppProcessInfo rapi : runningAppProcessInfoList) {
                    processNameList.add(rapi.processName);}
                String service1 = TextUtils.join(", ", processNameList);
                String service = "";
                Log.d("obService","yhs2:" + path + ":" + event);

                int evt = event & FileObserver.ALL_EVENTS;

                if(evt == FileObserver.ACCESS) {
                    Log.d("obService","yhs3: ACCESS");
                    if(path != null) {
                        sendMessage("ACCESS:" + path + service);
                    }
                }

                if (evt == FileObserver.OPEN) {
                    Log.d("obService","yhs4: OPEN");
                    Log.d("serviceinfo","SERVICE : "+service1);
                    if(path != null) {
                        sendMessage("OPEN:" + path + service);
                    }
                }

                if (evt == FileObserver.CREATE) {
                    Log.d("obService","yhs3: CREATE");

                    if(path != null) {
                        sendMessage("CREATE:" + path + service);
                    }

                }

                if (evt == FileObserver.ATTRIB) {
                    Log.d("obService","yhs3: ATTRIB");
                    if(path != null) {
                        sendMessage("ATTRIB:" + path + service);
                    }

                }

                if (evt == FileObserver.CREATE) {
                    Log.d("obService","yhs3: CREATE");
                    if(path != null) {
                        sendMessage("CREATE:" + path + service);
                    }

                }

                if (evt == FileObserver.CLOSE_NOWRITE) {
                    Log.d("obService","yhs3: CLOSE_NOWRITE");
                    if(path != null) {
                        sendMessage("CLOSE_NOWRITE:" + path + service);
                    }
                }

                if (evt == FileObserver.CLOSE_WRITE) {
                    Log.d("obService","yhs3: CLOSE_WRITE");
                    if(path != null) {
                        sendMessage("CLOSE_WRITE:" + path + service);
                    }

                }
                if (evt == FileObserver.DELETE) {
                    Log.d("obService","yhs3: DELETE");
                    if(path != null) {
                        sendMessage("DELETE:" + path + service);
                    }
                }
                if (evt == FileObserver.DELETE_SELF) {
                    Log.d("obService","yhs3: DELETE_SELF");
                    if(path != null) {
                        sendMessage("DELETE_SELF:" + path + service);
                    }
                }
                if (evt == FileObserver.MODIFY) {
                    Log.d("obService","yhs3: MODIFY");
                    if(path != null) {
                        sendMessage("MODIFY:" + path + service);
                    }
                }
                if (evt == FileObserver.MOVE_SELF) {
                    Log.d("obService","yhs3: MOVE_SELF");
                    if(path != null) {
                        sendMessage("MOVE_SELF:" + path + service);
                    }
                }
                if (evt == FileObserver.MOVED_FROM) {
                    Log.d("obService","yhs3: MOVED_FROM");
                    if(path != null) {
                        sendMessage("MOVED_FROM:" + path + service);
                    }

                }
                if (evt == FileObserver.MOVED_TO) {
                    Log.d("obService","yhs3: MOVED_TO");
                    if(path != null) {
                        sendMessage("MOVED_TO:" + path + service);
                    }
                }


                // 서비스에서 엑티비티로 데이터 전달 https://onepinetwopine.tistory.com/283
                //final TextView text = (TextView) getView().findViewById(R.id.textView1);
                //text.setText(event);
            }
        };

        mFileObserver.startWatching();
    }

    private void sendMessage(String msg) {
        Intent intent = new Intent("yhsIntent");
        intent.putExtra("alertStr",msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
