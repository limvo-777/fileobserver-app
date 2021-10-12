//package com.sadikul.fileobserver;
//
//import android.content.Intent;
//import android.os.Environment;
//import android.os.FileObserver;
//import android.support.annotation.Nullable;
//import android.support.v4.os.EnvironmentCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//
//public class MainActivity extends AppCompatActivity {
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        startService(new Intent(this,FileObserverService.class));
//    }
//
//
//}

package com.sadikul.fileobserver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.FileObserver;
import android.support.annotation.Nullable;
import android.support.constraint.solver.widgets.Helper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends Activity {

    public static final List<String> list = new ArrayList<>();
    public static final List<String> listL = new ArrayList<>();
    public static final ArrayList<TestFileObserver> sListFileObserver = new ArrayList<TestFileObserver>();


    static class TestFileObserver extends FileObserver {
        private String mPath;

        int[] eventValue = new int[] {FileObserver.ACCESS, FileObserver.ALL_EVENTS, FileObserver.ATTRIB, FileObserver.CLOSE_NOWRITE,FileObserver.CLOSE_WRITE, FileObserver.CREATE,
                FileObserver.DELETE, FileObserver.DELETE_SELF,FileObserver.MODIFY,FileObserver.MOVED_FROM,FileObserver.MOVED_TO, FileObserver.MOVE_SELF,FileObserver.OPEN};
        String[] eventName = new String[] {"ACCESS", "ALL_EVENTS", "ATTRIB", "CLOSE_NOWRITE", "CLOSE_WRITE", "CREATE",
                "DELETE", "DELETE_SELF" , "MODIFY" , "MOVED_FROM" ,"MOVED_TO", "MOVE_SELF","OPEN"};


        public TestFileObserver(String path) {
            super(path);
            mPath = path;
            sListFileObserver.add(this);
        }

        @Override
        public void onEvent(int event, String path) {
            StringBuilder strEvents = new StringBuilder();
            strEvents.append("Event : ").append('(').append(event).append(')');
            for (int i = 0; i < eventValue.length; ++i) {
                if ((eventValue[i] & event) == eventValue[i]) {
                    strEvents.append(eventName[i]);
                    strEvents.append(',');
                }
            }
            if ((event & FileObserver.DELETE_SELF) == FileObserver.DELETE_SELF) {
                sListFileObserver.remove(this);
            }
            strEvents.append("\tPath : ").append(path).append('(').append(mPath).append(')');
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
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);


            final ListView listview = (ListView)findViewById(R.id.listviewA);
            //리스트뷰의 어댑터를 지정해준다.
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, listL);
            listview.setAdapter(adapter);
            listL.add("==== LOG START ====");
            //String path = Environment.getExternalStorageDirectory() + "/Test";
            //File f = new File(path);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> a_parent, View a_view, int a_position, long a_id) {
                    HashSet<String> hlist = new HashSet<>(list);
                    listL.addAll(hlist);
                    list.clear();
                    listview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            });

            monitorAllFiles();

        }



    private void monitorAllFiles() {

   //     File[] files = root.listFiles();
   //     for(File file : files) {
   //         TestFileObserver fileObserver = new TestFileObserver(file.getAbsolutePath());
   //         fileObserver.startWatching();
                 TestFileObserver fileObserver = new TestFileObserver("/storage/emulated/0/Test");
                 fileObserver.startWatching();

   //         if(file.isDirectory()) monitorAllFiles(file);
   //     }
    }
}




