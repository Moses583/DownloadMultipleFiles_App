package com.example.downloadmultiplefilesapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DownLoadTask extends AsyncTask<ArrayList<String>, Integer, Void> {

    private Context context;
    private ProgressDialog progressDialog;
    private NotificationManagerCompat managerCompat;
    private NotificationCompat.Builder builder;

    public DownLoadTask(Context context) {
        this.context = context;
        managerCompat = NotificationManagerCompat.from(context);
        builder = new NotificationCompat.Builder(context, "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Downloading app")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        builder.setProgress(100, 0, true)
                .setContentText("Starting download...");
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        managerCompat.notify(101, builder.build());
    }

    @Override
    protected Void doInBackground(ArrayList<String>... arrayLists) {

        ArrayList<String> links = arrayLists[0];

        for (String link :
                links) {
            try {
                URL url = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                int fileLength = connection.getContentLength();
                boolean isIndeterminate = fileLength == -1;

                InputStream inputStream = connection.getInputStream();

                File directory = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "MyAppDirectory");

                // Create the directory if it does not exist
                if (!directory.exists()) {
                    if (directory.mkdirs()) {
                        Log.d("MAMA", "Directory created successfully");
                    } else {
                        Log.d("MAMA", "Failed to create directory");
                    }
                } else {
                    Log.d("MAMA", "Directory already exists");
                }

                File file = new File(directory, "image_" + System.currentTimeMillis() + ".jpg");
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int bytesRead;
                long totalBytesRead = 0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    totalBytesRead += bytesRead;
                    fileOutputStream.write(buffer, 0, bytesRead);

                    // Update progress if file length is known
                    if (!isIndeterminate) {
                        int progress = (int) ((totalBytesRead * 100) / fileLength);
                        Log.d("MAMA", "Progress: " + progress + "%");
                        publishProgress(progress);
                    }
                }

                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();

                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        builder.setProgress(100, values[0], false)
                .setContentText("Download progress: " + values[0] + "%");
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        managerCompat.notify(101, builder.build());
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        builder.setContentText("Download completed")
                .setProgress(0, 0, false);
        managerCompat.notify(101, builder.build());

        Toast.makeText(context, "Audio files saved successfully", Toast.LENGTH_LONG).show();

    }
}
