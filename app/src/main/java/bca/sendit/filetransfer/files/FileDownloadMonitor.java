package bca.sendit.filetransfer.files;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class FileDownloadMonitor {
    public static final String DOWNLOAD_SUCCESS = "download_success";
    public static final String PATH = "path";
    private final Context context;

    private final OnFileDownloadListener onFileDownloadListener;
    private final DownloadReceiver downloadReceiver;

    public FileDownloadMonitor(Context context, OnFileDownloadListener onFileDownloadListener) {
        this.context = context;
        this.onFileDownloadListener = onFileDownloadListener;
        downloadReceiver = new DownloadReceiver();
    }

    public void start() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DOWNLOAD_SUCCESS);
        context.registerReceiver(downloadReceiver, intentFilter);
    }

    public static void notifyFileDownloaded(Context context, String path) {
        Intent downloadBroadcast = new Intent(FileDownloadMonitor.DOWNLOAD_SUCCESS);
        downloadBroadcast.putExtra(FileDownloadMonitor.PATH, path);
        context.sendBroadcast(downloadBroadcast);
    }

    public void stop() {
        context.unregisterReceiver(downloadReceiver);
    }

   class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DOWNLOAD_SUCCESS)) {
                String path = intent.getStringExtra(FileDownloadMonitor.PATH);
                onFileDownloadListener.onSuccess(path);
            }
        }
    }
}
