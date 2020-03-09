package gmedia.net.id.semargres2020merchant.kategoriKupon;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gmedia.net.id.semargres2020merchant.HomeActivity;
import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.URL;

//import SessionManager;

public class SettingKategoriKuponActivity extends AppCompatActivity {

    int start = 0, count = 10;
    private EditText edt_namaKupon, edt_hargaPerKupon;
    private ArrayList<SettingKategoriKuponModel> kuponLama, kuponBaru;
    private RecyclerView rvView;
    private SettingKategoriKuponAdapter adapter;
    private ProgressDialog progressDialog;
//    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_kupon);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar));
        }
//        session = new SessionManager(getApplicationContext());

        rvView = findViewById(R.id.rv_kupon);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SettingKategoriKuponActivity.this);
        rvView.setLayoutManager(layoutManager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        getDataKupon();

//        RelativeLayout back = findViewById(R.id.back);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

//        Button iv_tambah_kupon = findViewById(R.id.iv_tambah_kupon);
//        iv_tambah_kupon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showTambahKupon();
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTambahKupon() {
        final Dialog dialog = new Dialog(SettingKategoriKuponActivity.this);
        dialog.setContentView(R.layout.popup_tambah_kategori_kupon);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        edt_namaKupon = dialog.findViewById(R.id.edt_namaKupon);
        edt_hargaPerKupon = dialog.findViewById(R.id.edt_hargaPerKupon);
        RelativeLayout rvOK = dialog.findViewById(R.id.rv_ok);
        RelativeLayout rvCancel = dialog.findViewById(R.id.rv_cancel);

        rvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edt_namaKupon.getText().toString().equals("")) {
                    edt_namaKupon.setError("Nama kupon harap diisi");
                    edt_namaKupon.requestFocus();
                    return;
                }

                if (edt_hargaPerKupon.getText().toString().equals("")) {
                    edt_hargaPerKupon.setError("Harga per kupon harap diisi");
                    edt_hargaPerKupon.requestFocus();
                    return;
                }

                AlertDialog alertDialog = new AlertDialog.Builder(SettingKategoriKuponActivity.this)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah Anda yakin ingin menyimpan data?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveKupon();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.color_grey_new));

                dialog.dismiss();
            }
        });

        rvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void saveKupon() {
        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("nama", edt_namaKupon.getText());
            jBody.put("nominal", edt_hargaPerKupon.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(this, jBody, "POST", URL.urlStoreKategoriKupon, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    String pesan = object.getJSONObject("metadata").getString("message");
                    String status = object.getJSONObject("metadata").getString("status");
                    if (status.equals("200")) {
                        getDataKupon();
                    } else {
                        Toast.makeText(getApplicationContext(), pesan, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                getDataKupon();
            }

            @Override
            public void onError(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getDataKupon() {
        kuponLama = new ArrayList<>();
        showProgressDialog();
        new ApiVolley(this, new JSONObject(), "POST", URL.urlKategoriKupon, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                kuponBaru = new ArrayList<>();
                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        JSONArray array = object.getJSONObject("response").getJSONArray("kategori");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject isi = array.getJSONObject(i);
                            kuponLama.add(new SettingKategoriKuponModel(
                                    isi.getString("id"),
                                    isi.getString("nama"),
                                    isi.getString("nominal")
                            ));
                        }

                        rvView.setAdapter(null);
                        adapter = new SettingKategoriKuponAdapter(SettingKategoriKuponActivity.this, kuponLama);
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

    @Override
    public void onBackPressed() {
        Intent i = new Intent(SettingKategoriKuponActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(SettingKategoriKuponActivity.this,
                R.style.AppTheme_Custom_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
