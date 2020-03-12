package gmedia.net.id.semargres2020merchant.akun;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gmedia.net.id.semargres2020merchant.HomeActivity;
import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.FourItemModel;
import gmedia.net.id.semargres2020merchant.util.SessionManager;
import gmedia.net.id.semargres2020merchant.util.URL;

public class ListAkunActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;

    SessionManager session;
    int start = 0, count = 10;
    String kalimat = "Daftar tenant yang telah dibuat oleh ";
    private RecyclerView rvView;
    private ListAkunAdapter adapter;
    private ArrayList<FourItemModel> listAkun, akunBaru;
    private TextView namaTenant, kirimSemua;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_akun);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar));
        }

        session = new SessionManager(getApplicationContext());

        rvView = findViewById(R.id.rv_akun);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ListAkunActivity.this);
        rvView.setLayoutManager(layoutManager);

        namaTenant = findViewById(R.id.txt_daftarTenant);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button ivTambahAkun = findViewById(R.id.iv_tambah_akun);

        ivTambahAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ListAkunActivity.this, TambahAkunActivity.class),101);
            }
        });

        kirimSemua = findViewById(R.id.kirimSemuaQr);
        kirimSemua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kirimSemuaQR();
            }
        });
        getDataAkun();
        prepareNamaMerchant();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 101){
            getDataAkun();
            prepareNamaMerchant();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void kirimSemuaQR() {
        new ApiVolley(ListAkunActivity.this, new JSONObject(), "GET", URL.urlKirimSemua, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        Toast.makeText(ListAkunActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(ListAkunActivity.this, message, Toast.LENGTH_LONG).show();
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

    private void getDataAkun() {
        showProgressDialog();
        new ApiVolley(this, new JSONObject(), "POST", URL.urlUserAkun, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dismissProgressDialog();
                listAkun = new ArrayList<>();

                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        JSONArray array = object.getJSONObject("response").getJSONArray("merchant_users");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject isi = array.getJSONObject(i);
                            listAkun.add(new FourItemModel(
                                    isi.getString("id"),
                                    isi.getString("username"),
                                    isi.getString("nama_kupon"),
                                    isi.getString("nominal_kupon")
                            ));
                        }

                        rvView.setAdapter(null);
                        adapter = new ListAkunAdapter(ListAkunActivity.this, listAkun);
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
        finish();
    }

    private void prepareNamaMerchant() {
        new ApiVolley(ListAkunActivity.this, new JSONObject(), "GET", URL.urlProfile, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    if (status.equals("200")) {
                        String kalimatBaru = kalimat + object.getJSONObject("response").getString("nama");
                        namaTenant.setText(kalimatBaru);
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

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(ListAkunActivity.this,
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
