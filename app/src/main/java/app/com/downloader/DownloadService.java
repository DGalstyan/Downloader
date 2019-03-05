package app.com.downloader;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Davit Galstyan on 3/5/19.
 */
public class DownloadService extends Service {

    private DownloadBinder downloadBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        downloadBinder.getListener().setDownloadService(this);
        return downloadBinder;
    }
}
