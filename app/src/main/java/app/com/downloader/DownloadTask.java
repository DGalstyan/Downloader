package app.com.downloader;

import java.io.File;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Davit Galstyan on 3/5/19.
 */

public class DownloadTask extends AsyncTask<String, Integer, Integer> {


    private boolean messageChanged;
    private boolean downloadFinished;
    private StringBuffer message= new StringBuffer();
    private Listener listener;

    public DownloadTask(Listener listener) {
        this.listener = listener;
    }

    /* This method is invoked after doInBackground() method. */
    @Override
    protected void onPostExecute(Integer downloadStatue) {
        if (downloadStatue == Util.DOWNLOAD_SUCCESS) {
            downloadFinished = true;
            listener.onSuccess();
            setMessage("-------Finished----------");
        } else if (downloadStatue == Util.DOWNLOAD_FAILED) {
            listener.onFailed();
            downloadFinished = true;
            setMessage("-------Download Failed----------");
        }
    }


    /* Invoked when this async task execute.When this method return, onPostExecute() method will be called.*/
    @Override
    protected Integer doInBackground(String... params) {
        setMessage("-------Starting Download----------");
        // Set current thread priority lower than main thread priority, so main thread Pause, Continue and Cancel action will not be blocked.
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY - 2);

        for(VideoModel model: Util.videoModels){
            if(model.getStatus() == Util.STATE_INIT) {
                File downloadLocalFile = Util.createFile(model);
                listener.sendDownloadNotification("File "+model.getFileName()+" is downloading...");
                setMessage("File "+model.getFileName()+" is downloading...");
                model.setStatus(Util.STATE_DOWNLOADED);
                Util.downloadFileFromUrl(model.getUrl(), downloadLocalFile, this);
            }

        }
        return Util.DOWNLOAD_SUCCESS;
    }

    public boolean isDownloadFinished() {
        return downloadFinished;
    }

    public StringBuffer getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message.append(message+"\n");
        messageChanged = true;
    }

    public boolean isMessageChanged() {
        return this.messageChanged;
    }

    public void setMessageChanged(boolean messageChanged) {
        this.messageChanged = messageChanged;
    }
}