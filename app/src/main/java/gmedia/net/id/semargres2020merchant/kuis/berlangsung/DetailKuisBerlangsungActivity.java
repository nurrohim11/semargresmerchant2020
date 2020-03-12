package gmedia.net.id.semargres2020merchant.kuis.berlangsung;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.kuis.JawabanKuisModel;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.URL;

public class DetailKuisBerlangsungActivity extends AppCompatActivity {

    String id, startSomething, end, soal;
    TextView soalBerlangsung, startNow, endNow;
    ArrayList<JawabanKuisModel> jawabanLama, jawabanBaru;
    RecyclerView rvView;
    DetailKuisBerlangsungAdapter adapter;
    ProgressDialog progressDialog;
    int start = 0, count = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kuis);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar));
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        id = getIntent().getStringExtra("id");
        startSomething = getIntent().getStringExtra("startSomething");
        end = getIntent().getStringExtra("end");
        soal = getIntent().getStringExtra("soal");

        soalBerlangsung = findViewById(R.id.txt_actSoalBerlangsung);
        startNow = findViewById(R.id.txt_actStartBerlangsung);
        endNow = findViewById(R.id.txt_actEndBerlangsung);

        rvView = findViewById(R.id.rv_jawaban);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DetailKuisBerlangsungActivity.this);
        rvView.setLayoutManager(layoutManager);

        soalBerlangsung.setText(soal);
        startNow.setText(startSomething);
        endNow.setText(end);

        getJawaban();
    }

    private void getJawaban() {
        jawabanLama = new ArrayList<>();
        jawabanBaru = new ArrayList<>();
        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("id_quiz", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog();
        new ApiVolley(DetailKuisBerlangsungActivity.this, jBody, "POST", URL.urlKuisJawaban,
                "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    dismissProgressDialog();
                    JSONObject object = new JSONObject(result);
//                    Log.d("hasil jawaban", object.toString());
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        JSONArray arrayAnswer = object.getJSONObject("response").getJSONArray("answers");
                        for (int i = 0; i < arrayAnswer.length(); i++) {
                            JSONObject isi = arrayAnswer.getJSONObject(i);
                            jawabanLama.add(new JawabanKuisModel(
                                    isi.getString("id"),
                                    isi.getString("email_user"),
                                    isi.getString("nama_user"),
                                    isi.getString("jawaban"),
                                    isi.getString("answered_at"),
                                    isi.getString("telp_user")
                            ));
                        }

                        rvView.setAdapter(null);
                        adapter = new DetailKuisBerlangsungAdapter(DetailKuisBerlangsungActivity.this, jawabanLama);
                        rvView.setAdapter(adapter);

                    } else {
                        Toast.makeText(DetailKuisBerlangsungActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                dismissProgressDialog();
                Toast.makeText(DetailKuisBerlangsungActivity.this, result, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(DetailKuisBerlangsungActivity.this,
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
