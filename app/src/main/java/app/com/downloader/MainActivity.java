package app.com.downloader;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DownloadBinder downloadBinder;

    private int REQUEST_WRITE_PERMISSION_CODE = 1;

    private Button startDownloadButton;
    private TextView logTextView;
    private Handler incomingHandler;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // This is called when the connection with the service has
            // been established, giving us the service object we can use
            // to interact with the service.

            downloadBinder = (DownloadBinder) service;
            startDownload();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // This is called when the connection with the service has
            // been unexpectedly disconnected
            downloadBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Downloader");
        Util.createVideoFolder();
        Util.loadData();

        startDownloadService();
        logTextView = findViewById(R.id.txtLog);
        startDownloadButton = findViewById(R.id.download);

        startDownloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload();
            }
        });

        if(incomingHandler == null){
            incomingHandler = new IncomingHandler();
        }


    }

    private void startDownload(){
        if(downloadBinder == null)
            return;

        Thread messagesThread = new Thread()
        {
            @Override
            public void run() {
                while (true) {
                    try {
                        if(downloadBinder != null && downloadBinder.getDownloadTask() != null) {
                            if (downloadBinder.getDownloadTask().isMessageChanged()) {
                                Message msg = new Message();
                                msg.obj =  downloadBinder.getDownloadTask().getMessage().toString();
                                incomingHandler.sendMessage(msg);
                                downloadBinder.getDownloadTask().setMessageChanged(false);
                            }
                            if (downloadBinder.getDownloadTask().isDownloadFinished()) {
                                Message msg = new Message();
                                msg.what = 1;
                                incomingHandler.sendMessage(msg);
                            }
                        }
                        sleep(3000);
                    }catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        };
        messagesThread.start();

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this, "The Storage permission is required", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION_CODE);
        } else {
            downloadBinder.checkFiles();
            downloadBinder.startDownload();
            startDownloadButton.setEnabled(false);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_WRITE_PERMISSION_CODE)
        {
            int grantResult = grantResults[0];
            if(grantResult != PackageManager.PERMISSION_GRANTED)
            {
                finish();
            }
        }
    }

    private void startDownloadService()
    {
        Intent downloadIntent = new Intent(this, DownloadService.class);
        startService(downloadIntent);
        bindService(downloadIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == 1){
               startDownloadButton.setEnabled(true);
               return;
            }

            String message = (String) msg.obj;
            logTextView.setText(message);
        }
    }
}