package gmedia.net.id.semargres2020merchant;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.Proses;
import gmedia.net.id.semargres2020merchant.util.SessionManager;

//import java.net.URL;
//import URL;

public class MyQRCode extends AppCompatActivity {
    SessionManager session;
    DownloadManager downloadManager;
    String urlQrCode;
    Long download_id;
    private RelativeLayout back;
    private Proses proses;
    private Context context;
    private ImageView imgBarcode;
    private TextView namaTenant, namaMerchant;
    private Button btnQrCode, btnKirim;
//	private BroadcastReceiver receiverDownloadComplete, receiverNotificationClicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qrcode);

        context = this;
        proses = new Proses(context);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar));
        }

        session = new SessionManager(getApplicationContext());

        initUI();
        initAction();

    }

    private void initUI() {
//        back = (RelativeLayout) findViewById(R.id.backMyQRCode);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        imgBarcode = (ImageView) findViewById(R.id.imgMyQRCode);
        namaTenant = findViewById(R.id.namaTenant);
        namaMerchant = findViewById(R.id.namaMerchant);
        btnQrCode = findViewById(R.id.btn_qrCode);
        btnKirim = findViewById(R.id.btn_kirimQr);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initAction() {
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

//		namaTenant.setText(session.getUsername());

        if (getIntent().getStringExtra("qr_code") == null) { //dari akun tenant
            prepareDataBarcode();
            prepareNamaMerchant();
            prepareNamaTenant();
            btnKirim.setVisibility(View.GONE);
        } else { //dari akun merchant
            Picasso.with(context).load(getIntent().getStringExtra("qr_code")).into(imgBarcode);
            urlQrCode = getIntent().getStringExtra("qr_code");
            prepareNamaMerchant();
            namaTenant.setText(getIntent().getStringExtra("nama"));
        }

        btnQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MyQRCode.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(MyQRCode.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MyQRCode.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    new DownloadFileFromURL().execute(urlQrCode);

//					File folderFoto = new File(Environment.getExternalStorageDirectory() + "/Downloads");
//					File file = new File(folderFoto.getAbsolutePath(), "temp"+ ".png");
//
//					if(!file.exists()){
//					    file.mkdir();
//					    Log.d("DownloadManager", "Dir baru dibuat");
//                    }
//
//					Uri uri = Uri.parse(urlQrCode);
//
//					DownloadManager.Request request = new DownloadManager.Request(uri);
//                    request.setTitle("Download QR Code");
//                    request.setDescription("Download QR Code Merchant/Tenant");
//					request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
//					request.setDestinationUri(Uri.fromFile(file));
////                    request.setDestinationInExternalPublicDir(Environment.DIRE, uri.getLastPathSegment());
////                    request.setMimeType("image/png");
//					request.setAllowedOverMetered(true);
//					request.setAllowedOverRoaming(true);
//					request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
//					request.allowScanningByMediaScanner();
//					request.setVisibleInDownloadsUi(true);
//
//                    downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
//                    downloadManager.enqueue(request);

                }
            }
        });

        final String id = getIntent().getStringExtra("id");
        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ApiVolley(MyQRCode.this, new JSONObject(), "GET", gmedia.net.id.semargres2020merchant.util.URL.urlKirim + "/" + id, "", "", 0, new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            final String status = object.getJSONObject("metadata").getString("status");
                            String message = object.getJSONObject("metadata").getString("message");
                            if (status.equals("200")) {
                                Toast.makeText(MyQRCode.this, message, Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(MyQRCode.this, message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void prepareDataBarcode() {
        proses.ShowDialog();
        new ApiVolley(context, new JSONObject(), "GET", gmedia.net.id.semargres2020merchant.util.URL.urlQrCodeTenant, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(">>>>>",result);
                proses.DismissDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        Picasso.with(context).load(object.getJSONObject("response").getString("qr_code")).into(imgBarcode);
                        urlQrCode = object.getJSONObject("response").getString("qr_code");
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                proses.DismissDialog();
                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void prepareNamaMerchant() {
        new ApiVolley(MyQRCode.this, new JSONObject(), "GET", gmedia.net.id.semargres2020merchant.util.URL.urlProfile, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("message");
                    if (status.equals("Success!")) {
                        namaMerchant.setText(object.getJSONObject("response").getString("nama"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void prepareNamaTenant() {
        new ApiVolley(MyQRCode.this, new JSONObject(), "GET", gmedia.net.id.semargres2020merchant.util.URL.urlProfileTenant, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    if (status.equals("200")) {
                        JSONObject jsonObject = object.getJSONObject("response").getJSONObject("profil");
                        namaTenant.setText(jsonObject.getString("nama_tenant"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        });
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        private File f;
        private ProgressDialog progressDialog;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
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
                f = new File(Environment.getExternalStorageDirectory() + File.separator + "downloadedfile.png");
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream("/sdcard/downloadedfile.png");
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
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog();

            // Displaying downloaded image into image view
            // Reading image path from sdcard
            if (f != null) {
                String imagePath = String.valueOf(FileProvider.getUriForFile(context, context.getPackageName() + ".provider", f));
                // setting downloaded into image view
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(imagePath), "image/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);
            }
        }

        private void showDialog() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Downloading file. Please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        private void dismissDialog() {
            progressDialog.dismiss();
        }
    }
}
