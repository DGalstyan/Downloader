package app.com.downloader;

/**
 * Created by Davit Galstyan on 3/5/19.
 */

import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class Util {

    public static ArrayList <VideoModel> videoModels = new ArrayList<>();


    public static String filePath = Environment.getExternalStorageDirectory() +
            File.separator + "FileDownloaderTest";

    public static final int STATE_INIT = 0;
    public static final int STATE_DOWNLOADED = 1;


    public static final int DOWNLOAD_SUCCESS = 1;
    public static final int DOWNLOAD_FAILED = 2;


    private static OkHttpClient okHttpClient = new OkHttpClient();

    public static long getDownloadUrlFileSize(String downloadUrl)
    {
        long ret = 0;

        try {
            if (downloadUrl != null && !TextUtils.isEmpty(downloadUrl)) {
                Request.Builder builder = new Request.Builder();
                builder = builder.url(downloadUrl);
                Request request = builder.build();

                Call call = okHttpClient.newCall(request);
                Response response = call.execute();

                if(response != null) {
                    if(response.isSuccessful())
                    {
                        String contentLength = response.header("Content-Length");
                        ret = Long.parseLong(contentLength);
                    }
                }
            }
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }finally {
            return ret;
        }
    }

    public static void downloadFileFromUrl(String downloadFileUrl, File existLocalFile, DownloadTask downloadTask) {
        try {
            long downloadFileLength = getDownloadUrlFileSize(downloadFileUrl);
            long existLocalFileLength = existLocalFile.length();
            Request.Builder builder = new Request.Builder();
            builder = builder.url(downloadFileUrl);
            builder = builder.addHeader("RANGE", "bytes=" + existLocalFileLength);
            Request request = builder.build();

            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            if(downloadFileLength == 0)
            {
                downloadTask.setMessage("Feild to download file " + existLocalFile.getName());
                downloadTask.setMessage("---------------------------------------------------");
            }else if(downloadFileLength == existLocalFileLength)
            {
                downloadTask.setMessage("File " + existLocalFile.getName() + " is downloaded");
                downloadTask.setMessage("---------------------------------------------------");
            }else {

                if (response != null && response.isSuccessful()) {
                    RandomAccessFile downloadFile = new RandomAccessFile(existLocalFile, "rw");
                    downloadFile.seek(existLocalFileLength);

                    ResponseBody responseBody = response.body();
                    InputStream inputStream = responseBody.byteStream();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    byte data[] = new byte[102400];

                    int readLength = bufferedInputStream.read(data);
                    long totalReadLength = 0;
                    int progress = 0;
                    while (readLength != -1) {
                        downloadFile.write(data, 0, readLength);
                        totalReadLength = totalReadLength + readLength;
                        int downloadProgress = (int) ((totalReadLength + existLocalFileLength) * 100 / downloadFileLength);
                        if(progress + 10 < downloadProgress){
                            downloadTask.setMessage("File " + existLocalFile.getName() + " is download " + downloadProgress + " %");
                            progress = downloadProgress;
                        }
                        readLength = bufferedInputStream.read(data);
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static  File createFile(VideoModel model) {
        File file = null;
        try {
            file = new File(filePath + File.separator + model.getFileName());
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            return file;
        }
    }

    public static boolean isFileExists(String fileName){
        File file = new File(filePath + File.separator + fileName);
        return !file.isDirectory() && file.exists() && file.length() > 0;
    }

    public static void createVideoFolder(){
        File folder = new File(filePath);
        if (!folder.exists())
            folder.mkdirs();
    }

    public static void loadData(){
        videoModels.add(new VideoModel("https://sample-videos.com/video123/mp4/240/big_buck_bunny_240p_30mb.mp4", "big_buck_bunny1.mp4"));
        videoModels.add(new VideoModel("https://sample-videos.com/video123/mp4/240/big_buck_bunny_240p_30mb.mp4", "big_buck_bunny2.mp4"));
        videoModels.add(new VideoModel("https://sample-videos.com/video123/mp4/240/big_buck_bunny_240p_30mb.mp4", "big_buck_bunny3.mp4"));
        videoModels.add(new VideoModel("https://sample-videos.com/video123/mp4/240/big_buck_bunny_240p_30mb.mp4", "big_buck_bunny4.mp4"));
        videoModels.add(new VideoModel("https://sample-videos.com/video123/mp4/240/big_buck_bunny_240p_30mb.mp4", "big_buck_bunny5.mp4"));
        videoModels.add(new VideoModel("https://sample-videos.com/video123/mp4/240/big_buck_bunny_240p_30mb.mp4", "big_buck_bunny6.mp4"));
        videoModels.add(new VideoModel("https://sample-videos.com/video123/mp4/240/big_buck_bunny_240p_30mb.mp4", "big_buck_bunny7.mp4"));
        videoModels.add(new VideoModel("https://sample-videos.com/video123/mp4/240/big_buck_bunny_240p_30mb.mp4", "big_buck_bunny8.mp4"));
        videoModels.add(new VideoModel("https://sample-videos.com/video123/mp4/240/big_buck_bunny_240p_30mb.mp4", "big_buck_bunny9.mp4"));
        videoModels.add(new VideoModel("https://sample-videos.com/video123/mp4/240/big_buck_bunny_240p_30mb.mp4", "big_buck_bunny10.mp4"));
        videoModels.add(new VideoModel("https://sample-videos.com/video123/mp4/240/big_buck_bunny_240p_30mb.mp4", "big_buck_bunny11.mp4"));
        Collections.synchronizedList(videoModels);
    }
}