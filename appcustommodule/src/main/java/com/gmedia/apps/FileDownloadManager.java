package com.gmedia.apps;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class FileDownloadManager {

    private int FILE_TYPE;
    private Activity activity;
    private boolean cancelable = false;
    private final String TAG = "download_log";

    private final static int DOWNLOAD_REQUEST = 999;

    public static final int TYPE_PDF = 90;
    public static final int TYPE_PNG = 91;

    private ProgressDialog progressDialog;

    public FileDownloadManager(Activity activity, int FILE_TYPE){
        this.activity = activity;
        this.FILE_TYPE = FILE_TYPE;
    }

    public FileDownloadManager(Activity activity, int FILE_TYPE, boolean cancelable){
        this.activity = activity;
        this.FILE_TYPE = FILE_TYPE;
        this.cancelable = cancelable;
    }

    public void download(String url, String filename){
        if(writeStorageCheckPermission()){
            DownloadFileFromURL downloader = new DownloadFileFromURL();
            downloader.setListener(new DownloadFileFromURL.DownloadListener() {
                @Override
                public void onStart() {
                    showDialog();
                }

                @Override
                public void onProgress(int progress) {
                    showProgress(progress);
                }

                @Override
                public void onFinished(File f) {
                    finishDialog(f);
                }

                @Override
                public void onError(String message) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(activity, "Gagal mengunduh", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.e(TAG, message);
                }
            });
            downloader.execute(url, getFileName(filename));
        }
    }

    //METHOD PERMISSION HANDLING ===================================================================
    private boolean writeStorageCheckPermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            else{
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, DOWNLOAD_REQUEST);
                return false;
            }
        }
        else{
            return true;
        }
    }

    public static boolean ifFileDownloadManagerPermission(int requestCode, int result){
        if(requestCode == DOWNLOAD_REQUEST){
            return result == PackageManager.PERMISSION_GRANTED;
        }
        else{
            return false;
        }
    }

    //ASYNC DOWNLOAD CLASS =========================================================================
    static class DownloadFileFromURL extends AsyncTask<String, String, String> {

        private File f;
        private DownloadListener listener;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(listener != null){
                listener.onStart();
            }
        }

        private void setListener(DownloadListener listener){
            this.listener = listener;
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                f = new File(Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOWNLOADS) + File.separator + f_url[1]);
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream(Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOWNLOADS) + File.separator + f_url[1]);
//                OutputStream output = new FileOutputStream(Environment.getExternalStorageDirectory().getPath());

                byte[] data = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                if(listener != null){
                    listener.onError(e.getMessage());
                }
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            if(listener != null){
                listener.onProgress(Integer.parseInt(progress[0]));
            }
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            if(listener != null){
                listener.onFinished(f);
            }
        }

        public interface DownloadListener{
            void onStart();
            void onProgress(int progress);
            void onFinished(File f);
            void onError(String message);
        }
    }

    //FILE TYPE HANDLING METHOD ====================================================================
    private String getFileName(String name){
        switch (FILE_TYPE){
            case TYPE_PDF : return name + ".pdf";
            case TYPE_PNG : return name + ".png";
        }

        return "";
    }

    private String getFileMimeType(){
        switch (FILE_TYPE){
            case TYPE_PDF : return "application/pdf";
            case TYPE_PNG : return "image/png";
        }

        return "";
    }

    //PROGRESS LISTENER METHOD =====================================================================
    private void showDialog() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("Mengunduh file. Tunggu sebentar...");
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(cancelable);
        progressDialog.show();
       /* progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });*/
    }

    private void showProgress(int progress){
        progressDialog.setProgress(progress);
    }

    private void finishDialog(File f){
        // dismiss the dialog after the file was downloaded
        progressDialog.dismiss();

        // Displaying downloaded image into image view
        // Reading image path from sdcard
        if (f != null) {
            String imagePath = String.valueOf(FileProvider.getUriForFile(activity,
                    activity.getPackageName() + ".provider", f));
            // setting downloaded into image view
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(imagePath), getFileMimeType());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            activity.startActivity(intent);
        }
    }
}
