package gmedia.net.id.semargres2020merchant.voucher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.URL;

public class VoucherActivity extends AppCompatActivity {

    int start = 0, count = 10;
    private RecyclerView rvView;
    private VoucherAdapter adapter;
    private ArrayList<VoucherModel> voucherLama, voucherBaru;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar));
        }

//        RelativeLayout back = findViewById(R.id.back);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        rvView = findViewById(R.id.rv_voucher);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(VoucherActivity.this);
        rvView.setLayoutManager(layoutManager);

        voucherLama = new ArrayList<>();
//        voucherBaru = new ArrayList<>();
        //getVoucher();

        Button tambahVoucher = findViewById(R.id.tambahVoucher);
        tambahVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VoucherActivity.this, SettingVoucherActivity.class);
                startActivity(i);
            }
        });
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getVoucher() {
        voucherLama = new ArrayList<>();
        showProgressDialog();
        new ApiVolley(VoucherActivity.this, new JSONObject(), "POST", URL.urlVoucher, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
//                    Log.d("hasil get voucher", object.toString());
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        voucherLama.clear();
                        JSONArray array = object.getJSONObject("response").getJSONArray("vouchers");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject isi = array.getJSONObject(i);
                            voucherLama.add(new VoucherModel(
                                    isi.getString("uid"),
                                    isi.getString("nama_voucher"),
                                    isi.getString("valid_start"),
                                    isi.getString("valid_end"),
                                    isi.getString("tipe"),
                                    isi.getString("nominal"),
                                    isi.getString("jumlah")
                            ));
                        }

                        rvView.setAdapter(null);
                        adapter = new VoucherAdapter(VoucherActivity.this, voucherLama);
                        rvView.setAdapter(adapter);

//                        rvView.setAdapter(null);
//                        adapter = new VoucherAdapter(VoucherActivity.this, voucherLama);
//                        rvView.setAdapter(adapter);

//                        for (int i = start; i<count; i++){
//                            if (i<voucherLama.size()) {
//                                voucherBaru.add(voucherLama.get(i));
//                            }
//                        }



//                        EndlessScroll scrollListener = new EndlessScroll((LinearLayoutManager) layoutManager) {
//                            @Override
//                            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                                start += count;
//                                for (int i = start; i<start+count; i++){
//                                    if (i<voucherLama.size()) voucherBaru.add(voucherLama.get(i));
//                                }
//                                adapter.addMoreData();
//                            }
//                        };
//                        rvView.addOnScrollListener(scrollListener);

                    } else {
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
    protected void onResume() {
        super.onResume();
        getVoucher();
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(VoucherActivity.this,
                R.style.AppTheme_Custom_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
