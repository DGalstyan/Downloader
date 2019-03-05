package app.com.downloader;

/**
 * Created by Davit Galstyan on 3/5/19.
 */
public class VideoModel {
    private String url;
    private String fileName;
    private int status =Util.STATE_INIT;

    public VideoModel(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
