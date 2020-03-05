package gmedia.net.id.semargres2020merchant.merchant;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.semargres2020merchant.HomeActivity;
import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.kategoriKupon.SettingKategoriKuponModel;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.SessionManager;
import gmedia.net.id.semargres2020merchant.util.TwoItemModel;
import gmedia.net.id.semargres2020merchant.util.URL;

import static gmedia.net.id.semargres2020merchant.util.Utils.isEmailValid;

public class KirimEmailMerActivity extends AppCompatActivity {

    Toolbar toolbar;
    ScrollView svContainer;

    private List<TwoItemModel> caraBayarList = new ArrayList<>();
    private List<SettingKategoriKuponModel> kategoriKupon = new ArrayList<>();

    private EditText edtEmail, edtNominal;
    private Button btnKirim;
    private Spinner spCaraBayar, spKategori;

    String cara_bayar="", kategori_kupon="";
    private ArrayList<SettingKategoriKuponModel> dataSet;
    SessionManager session;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kirim_email_mer);
        getCaraBayarData();
        session = new SessionManager(this);
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
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("Kalkulator E-Kupon By Email");
        }

        svContainer = findViewById(R.id.sv_container);
        svContainer.setVerticalScrollBarEnabled(false);
        svContainer.setHorizontalScrollBarEnabled(false);

        edtEmail = findViewById(R.id.edt_email);
        edtNominal = findViewById(R.id.edt_nominal);
        spCaraBayar = (Spinner) findViewById(R.id.sp_cara_bayar);
        spKategori = (Spinner) findViewById(R.id.sp_kategori);
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
                sendKupon();
//                TwoItemModel item = (TwoItemModel) spCaraBayar.getSelectedItem();
//                cara_bayar = item.getItem1();
//                Toast.makeText(KirimEmailMerActivity.this, cara_bayar, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCaraBayarData() {

        new ApiVolley(KirimEmailMerActivity.this, new JSONObject(), "GET", URL.urlCaraBayar, "", "", 0, new ApiVolley.VolleyCallback() {
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
                        spCaraBayar.setAdapter(new ArrayAdapter<>(KirimEmailMerActivity.this, R.layout.layout_simple_list, caraBayarList));
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
            new ApiVolley(KirimEmailMerActivity.this, new JSONObject(), "POST", URL.urlKategoriKupon, "", "", 0, new ApiVolley.VolleyCallback() {
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
                            spKategori.setAdapter(new ArrayAdapter<>(KirimEmailMerActivity.this, R.layout.layout_simple_list, dataSet));
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

    private void sendKupon(){

        if (edtEmail.getText().toString().equals("")) {
            edtEmail.setError("Silahkan mengisi email");
            edtEmail.requestFocus();
            return;
        }

        if(!isEmailValid(edtEmail.getText().toString())){
            edtEmail.setError("Email belum sesuai format");
            edtEmail.requestFocus();
            return;
        }

        if (edtNominal.getText().toString().equals("")) {
            edtNominal.setError("Silahkan mengisi nominal belanja");
            edtNominal.requestFocus();
            return;
        }

        TwoItemModel item = (TwoItemModel) spCaraBayar.getSelectedItem();
        cara_bayar = item.getItem1();

        SettingKategoriKuponModel kategori = (SettingKategoriKuponModel) spKategori.getSelectedItem();
        kategori_kupon = kategori.getId();

        AlertDialog alertDialog = new AlertDialog.Builder(KirimEmailMerActivity.this)
                .setTitle("Konfirmasi")
                .setMessage("Apakah Anda yakin ingin menyimpan data?")
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (session.getFlag().equals("2")){
                            prepareDataSendEmailTenant();
                        }
                        else{
                            prepareDataSendEmail();
                        }
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

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(KirimEmailMerActivity.this,
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

    private void prepareDataSendEmail() {
        showProgressDialog();

        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("kepada", edtEmail.getText());
            jBody.put("total", edtNominal.getText());
            jBody.put("cara_bayar", String.valueOf(cara_bayar));
            jBody.put("id_kategori_kupon", String.valueOf(kategori_kupon));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(KirimEmailMerActivity.this, jBody, "POST", URL.urlSendEmail, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    Log.d("hasil object email", object.toString());
                    final String status = object.getJSONObject("response").getString("status");
                    String message = object.getJSONObject("response").getString("message");
                    if (status.equals("200")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                if (session.getFlag().equals("2")){
//                    prepareDataDashboardTenant();
//                }
//                else{
//                    prepareDataDashboard();
//                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                dismissProgressDialog();
            }
        });
    }

    private void prepareDataSendEmailTenant() {
        showProgressDialog();

        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("kepada", edtEmail.getText());
            jBody.put("total", edtNominal.getText());
            jBody.put("cara_bayar", String.valueOf(cara_bayar));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(KirimEmailMerActivity.this, jBody, "POST", URL.urlSendEmailTenant, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    Log.d("hasil object email", object.toString());
                    final String status = object.getJSONObject("response").getString("status");
                    String message = object.getJSONObject("response").getString("message");
                    if (status.equals("200")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                if (session.getFlag().equals("2")){
//                    prepareDataDashboardTenant();
//                }
//                else{
//                    prepareDataDashboard();
//                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                dismissProgressDialog();
//                if (session.getFlag().equals("2")){
//                    prepareDataDashboardTenant();
//                }
//                else{
//                    prepareDataDashboard();
//                }
            }
        });
    }


}
