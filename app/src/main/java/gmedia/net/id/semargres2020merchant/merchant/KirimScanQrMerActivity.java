package gmedia.net.id.semargres2020merchant.merchant;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.semargres2020merchant.BerhasilQrCodeActivity;
import gmedia.net.id.semargres2020merchant.HomeActivity;
import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.kategoriKupon.SettingKategoriKuponModel;
import gmedia.net.id.semargres2020merchant.tiketKonser.KonserActivity;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.RuntimePermissionsActivity;
import gmedia.net.id.semargres2020merchant.util.SessionManager;
import gmedia.net.id.semargres2020merchant.util.TwoItemModel;
import gmedia.net.id.semargres2020merchant.util.URL;
import gmedia.net.id.semargres2020merchant.volunteer.MerchantModel;

public class KirimScanQrMerActivity extends RuntimePermissionsActivity {

    Toolbar toolbar;
    ScrollView svContainer;
    Spinner spKategori, spCaraBayar;
    List<TwoItemModel> caraBayarList = new ArrayList<>();
    List<SettingKategoriKuponModel> dataSet = new ArrayList<>();
    EditText edtNominal;
    Button btnKirim;
    private IntentResult resultScanBarcode;

    String cara_bayar="",kategori_kupon="";
    SessionManager session;
    private ProgressDialog progressDialog;
    LinearLayout llKategori;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kirim_scan_qr_mer);
        session = new SessionManager(this);
        getCaraBayarData();
        getKategoriKupon();
        initUi();
    }


    private void initUi(){
        toolbar = findViewById(R.id.toolbar);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar));
        }
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!= null){
            getSupportActionBar().setTitle("Kalkulator E-Kupon By Scan QR");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        svContainer = findViewById(R.id.sv_container);
        svContainer.setVerticalScrollBarEnabled(false);
        svContainer.setHorizontalScrollBarEnabled(false);

        spKategori = findViewById(R.id.sp_kategori);
        spCaraBayar = findViewById(R.id.sp_cara_bayar);
        edtNominal = findViewById(R.id.edt_nominal);

        llKategori = findViewById(R.id.ll_kategori);
        if (session.getFlag().equals("2")) {
            llKategori.setVisibility(View.GONE);
        }else{
            llKategori.setVisibility(View.VISIBLE);
        }

        btnKirim = findViewById(R.id.btn_send);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TwoItemModel item = (TwoItemModel) spCaraBayar.getSelectedItem();
                cara_bayar = item.getItem1();

                if (session.getFlag().equals("1")) {
                    SettingKategoriKuponModel kategori = (SettingKategoriKuponModel) spKategori.getSelectedItem();
                    kategori_kupon = kategori.getId();
                }

                if(edtNominal.getText().toString().equals("")){
                    edtNominal.setError("Silahkan mengisi nominal belanja");
                    edtNominal.requestFocus();
                    return;
                }

                AlertDialog alertDialog = new AlertDialog.Builder(KirimScanQrMerActivity.this)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah Anda yakin ingin menyimpan data?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openScanBarcode();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.color_grey_new));

            }
        });
    }

    private void openScanBarcode(){
        IntentIntegrator integrator = new IntentIntegrator(KirimScanQrMerActivity.this);
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        resultScanBarcode = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (resultScanBarcode != null) {

            if (resultScanBarcode.getContents() != null) {
                if (session.getFlag().equals("2")){
                    prepareDataScanBarcodeTenant();
                }
                else if(session.getFlag().equals("1")){
                    prepareDataScanBarcode();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == 501){
                finish();
            }else if(requestCode == 401){
                finish();
            }
        }
    }


    private void prepareDataScanBarcode() {
        showProgressDialog();

        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("user_id", resultScanBarcode.getContents());
            jBody.put("total", edtNominal.getText().toString());
            jBody.put("cara_bayar", cara_bayar);
            jBody.put("id_kategori_kupon", kategori_kupon);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(KirimScanQrMerActivity.this, jBody, "POST", URL.urlScanBarcodeMerchant, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(">>>>>",result);
                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        JSONObject detail = object.getJSONObject("response");

                        Intent i = new Intent(KirimScanQrMerActivity.this, BerhasilQrCodeActivity.class);
                        i.putExtra("type", "merchant");
                        i.putExtra("nama", detail.getString("nama"));
                        i.putExtra("email", detail.getString("email"));
                        i.putExtra("telpon", detail.getString("no_telp"));
                        i.putExtra("gambar", detail.getString("foto"));
                        i.putExtra("jumlah_kupon", detail.getString("jumlah_kupon"));
                        startActivityForResult(i,501);
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                dismissProgressDialog();
            }
        });
    }

    private void prepareDataScanBarcodeTenant() {
        showProgressDialog();

        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("user_id", resultScanBarcode.getContents());
            jBody.put("total_bayar", edtNominal.getText().toString());
            jBody.put("cara_bayar", cara_bayar);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(KirimScanQrMerActivity.this, jBody, "POST", URL.urlScanBarcodeTenant, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("response").getString("status");
                    String message = object.getJSONObject("response").getString("message");
                    if (status.equals("1")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        JSONObject detail = object.getJSONObject("response");

                        Intent i = new Intent(KirimScanQrMerActivity.this, BerhasilQrCodeActivity.class);
                        i.putExtra("type", "tenant");
                        i.putExtra("nama", detail.getString("nama"));
                        i.putExtra("email", detail.getString("email"));
                        i.putExtra("telpon", detail.getString("no_telp"));
                        i.putExtra("gambar", detail.getString("foto"));
                        i.putExtra("jumlah_kupon", detail.getString("jumlah_kupon"));
                        startActivityForResult(i,401);
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                dismissProgressDialog();
            }
        });
    }

    private void getCaraBayarData() {

        new ApiVolley(KirimScanQrMerActivity.this, new JSONObject(), "GET", URL.urlCaraBayar, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");

                    if (status.equals("200")) {
                        JSONArray jArray = object.getJSONArray("response");
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jo = jArray.getJSONObject(i);
                            caraBayarList.add(new TwoItemModel(jo.getString("value"), jo.getString("text")));
                        }
                        spCaraBayar.setAdapter(new ArrayAdapter<>(KirimScanQrMerActivity.this, R.layout.layout_simple_list, caraBayarList));
                    } else {
                        Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
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

    private void getKategoriKupon() {
        dataSet = new ArrayList<>();

        if (session.getFlag().equals("2")){
            dataSet.add(new SettingKategoriKuponModel(
                    "id",
                    "nama",
                    "nominal"
            ));
        }
        else{
            new ApiVolley(KirimScanQrMerActivity.this, new JSONObject(), "POST", URL.urlKategoriKupon, "", "", 0, new ApiVolley.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject object = new JSONObject(result);
                        String status = object.getJSONObject("metadata").getString("status");
                        String message = object.getJSONObject("metadata").getString("message");
                        if (status.equals("200")) {
                            JSONArray array = object.getJSONObject("response").getJSONArray("kategori");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject isi = array.getJSONObject(i);
                                dataSet.add(new SettingKategoriKuponModel(
                                        isi.getString("id"),
                                        isi.getString("nama"),
                                        isi.getString("nominal")
                                ));
                            }
                            spKategori.setAdapter(new ArrayAdapter<>(KirimScanQrMerActivity.this, R.layout.layout_simple_list, dataSet));
                        } else {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(KirimScanQrMerActivity.this,
                R.style.AppTheme_Custom_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setMessage("Memproses...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

}
