package gmedia.net.id.semargres2020merchant.historyPenjualan;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import gmedia.net.id.semargres2020merchant.HomeActivity;
import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.FormatRupiah;
import gmedia.net.id.semargres2020merchant.util.URL;

/**
 * Created by Bayu on 22/03/2018.
 */

public class HistoryPenjualanActivity extends AppCompatActivity {
//    private final String tanggal[] =
//            {
//                    "Jum'at, 23-06-2017",
//                    "Rabu, 20-06-2017",
//                    "Senin, 27-06-2017",
//                    "Jum'at, 23-06-2017",
//                    "Rabu, 20-06-2017",
//                    "Senin, 27-06-2017"
//            };
//    private final String email[] =
//            {
//                    "hsx@gmail.com",
//                    "hahaha@gmail.com",
//                    "hihhi@gmail.com",
//                    "hsx@gmail.com",
//                    "hahaha@gmail.com",
//                    "hihhi@gmail.com"
//            };
//    private final String jumlah_kupon[] =
//            {
//                    "450",
//                    "250",
//                    "500",
//                    "450",
//                    "250",
//                    "500"
//            };
//    private final String total_belanja[] =
//            {
//                    "Rp 250.000",
//                    "Rp 150.000",
//                    "Rp 550.000",
//                    "Rp 250.000",
//                    "Rp 150.000",
//                    "Rp 550.000"
//            };
//    int total;

    Integer start = 0, count = 10;
    private RecyclerView rvView;
    private HistoryPenjualanAdapter adapter;
    private ArrayList<HistoryPenjualanModel> historyPenjualan;
    private ArrayList<HistoryPenjualanModel> historyBaru;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_penjualan);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

//        RelativeLayout back = findViewById(R.id.backHistoryPenjualan);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        rvView = findViewById(R.id.rv_history_penjualan);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HistoryPenjualanActivity.this);
        rvView.setLayoutManager(layoutManager);

        prepareDataHistoryPenjualan();
//        HistoryPenjualanModel history = historyPenjualan.get(2);
//        ArrayList<HistoryPenjualanModel> historyPenjualan = prepareDataHistoryPenjualan();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareDataHistoryPenjualan() {
//        final String string = "";
//
//        final JSONObject jBody = new JSONObject();
//
//        if(string == "awal"){
//            try {
//                jBody.put("start", "0");
//                jBody.put("limit", "10");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        else if (string == "load"){
//            try {
//                jBody.put("start", start);
//                jBody.put("limit", count);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }

        showProgressDialog();

        new ApiVolley(this, new JSONObject(), "POST", URL.urlHistoryPenjualan, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                historyPenjualan = new ArrayList<>();
//                historyBaru = new ArrayList<>();
                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    Log.d("hasil object", object.toString());
                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        JSONArray array = object.getJSONArray("response");
                        FormatRupiah request = new FormatRupiah();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject isi = array.getJSONObject(i);
                            historyPenjualan.add(new HistoryPenjualanModel(
                                    isi.getString("email"),

                                    isi.getString("tanggal"),

                                    isi.getString("kupon"),

                                    request.ChangeToRupiahFormat(isi.getString("total")),

                                    ChangeFormatDateString(isi.getString("waktu"), "yyyy-MM-dd HH:mm:ss", "HH:mm:ss"),

                                    isi.getString("profile_name"),

                                    isi.getString("user_insert")
                            ));
                        }

                        rvView.setAdapter(null);
                        adapter = new HistoryPenjualanAdapter(HistoryPenjualanActivity.this, historyPenjualan);
                        rvView.setAdapter(adapter);

                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                dismissProgressDialog();
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        });
    }

    private String ChangeFormatDateString(String date, String formatDateFrom, String formatDateTo) {

        if (date != null && !date.equals("") && !date.equals(null)) {

//            String result = date;
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(formatDateFrom);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfCustom = new SimpleDateFormat(formatDateTo);

            Date date1 = null;
            try {
                date1 = sdf.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return (sdfCustom.format(date1) == null) ? "" : sdfCustom.format(date1);
        } else {
            return "";
        }

    }

//    /*private ArrayList<HistoryPenjualanModel> prepareDataHistoryPenjualan() {
//            ArrayList<HistoryPenjualanModel> rvKuis = new ArrayList<>();
//            for (int i = 0; i < tanggal.length; i++) {
//                HistoryPenjualanModel custom = new HistoryPenjualanModel();
//                custom.setTanggal(tanggal[i]);
//                custom.setEmail(email[i]);
//                custom.setJumlah_kupon(jumlah_kupon[i]);
//                custom.setTotal_belanja(total_belanja[i]);
//                rvKuis.add(custom);
//            }
//            return rvKuis;
//    }*/

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(HistoryPenjualanActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(HistoryPenjualanActivity.this,
                R.style.AppTheme_Custom_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

}
