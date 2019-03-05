package app.com.downloader;

import android.os.Binder;
import android.util.Log;

/**
 * Created by Davit Galstyan on 3/5/19.
 */
public class DownloadBinder extends Binder {
    private DownloadTask downloadTask;

    private Listener listener;

    public DownloadTask getDownloadTask() {
        return downloadTask;
    }

    public DownloadBinder() {
        if (listener == null)
            listener = new Listener();
    }

    public void startDownload() {
        listener.createNotification("Downloading...");
        downloadTask = new DownloadTask(listener);
        downloadTask.execute();
    }

    public void checkFiles(){
        Thread thread = new Thread() {
            public void run() {
                while (true) {
                    for(VideoModel model: Util.videoModels){
                        if(model.getStatus() == Util.STATE_DOWNLOADED && !Util.isFileExists(model.getFileName())){
                            model.setStatus(Util.STATE_INIT);
                            if(downloadTask!= null){
                                downloadTask.setMessage("File "+model.getFileName()+" is not found ...");
                            }
                        }

                        if(model.getStatus() == Util.STATE_INIT && Util.isFileExists(model.getFileName())){
                            model.setStatus(Util.STATE_DOWNLOADED);
                            if(downloadTask!= null) {
                                downloadTask.setMessage("File " + model.getFileName() + " already downloaded ...");
                            }
                        }
                    }
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    public Listener getListener() {
        return listener;
    }
}
