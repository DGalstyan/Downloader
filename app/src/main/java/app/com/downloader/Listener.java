package app.com.downloader;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;


/**
 * Created by Davit Galstyan on 3/5/19.
 */
public class Listener {

    private DownloadService downloadService = null;

    public void setDownloadService(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    public DownloadService getDownloadService() {
        return downloadService;
    }

    public void onSuccess() {
        downloadService.stopForeground(true);
        sendDownloadNotification("Download success.");
    }

    public void onFailed() {
        downloadService.stopForeground(true);
        sendDownloadNotification("Download failed.");
    }


    public void createNotification(String title) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = getNewDownloadNotification(title);
            getDownloadService().startForeground(2, notification);
        }else{
            // Create and start foreground service with notification.
            Notification notification = getOldDownloadNotification(title);
            getDownloadService().startForeground(1, notification);
        }
    }

    public void sendDownloadNotification(String title) {
        NotificationManager notificationManager = (NotificationManager)downloadService.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = getNewDownloadNotification(title);
            notificationManager.notify(2, notification);
            return;
        }
        Notification notification = getOldDownloadNotification(title);
        notificationManager.notify(1, notification);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification getNewDownloadNotification(String title) {
        String NOTIFICATION_CHANNEL_ID = "app.com.downloader";
        String channelName = "My Download Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) downloadService.getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getDownloadService().getBaseContext(), NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        return notification;
    }


    private Notification getOldDownloadNotification(String title) {
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(downloadService, 0, intent, 0);
        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(downloadService);
        notifyBuilder.setSmallIcon(android.R.mipmap.sym_def_app_icon);

        Bitmap bitmap = BitmapFactory.decodeResource(downloadService.getResources(), android.R.drawable.stat_sys_download);
        notifyBuilder.setLargeIcon(bitmap);

        notifyBuilder.setContentIntent(pendingIntent);
        notifyBuilder.setContentTitle(title);
        notifyBuilder.setFullScreenIntent(pendingIntent, true);

        Notification notification = notifyBuilder.build();

        return notification;
    }
}