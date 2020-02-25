package gmedia.net.id.semargres2020merchant.promo;

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

import gmedia.net.id.semargres2020merchant.HomeActivity;
import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.URL;

public class CreatePromoActivity extends AppCompatActivity {
    int start = 0, count = 10;
    private RecyclerView rvView;
    private CreatePromoAdapter adapter;
    private ArrayList<CreatePromoModel> promoLama, promoBaru;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_promo);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar));
        }

        rvView = findViewById(R.id.rv_create_promo);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CreatePromoActivity.this);
        rvView.setLayoutManager(layoutManager);


//        RelativeLayout back = findViewById(R.id.back);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

//        RelativeLayout home = findViewById(R.id.btnHomeCreatePromo);
//        RelativeLayout logout = findViewById(R.id.btnLogout);
//        final ImageView gbrHome = findViewById(R.id.gambarHome);
//        final ImageView gbrLogout = findViewById(R.id.gambarLogout);
//        final TextView txtGbrHome = findViewById(R.id.textGambarHome);
//        final TextView txtGbrLogout = findViewById(R.id.textGambarLogout);

//        home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                gbrHome.setImageResource(R.drawable.home_grey);
//                txtGbrHome.setTextColor(Color.parseColor("#FF5F5D5D"));
//                gbrLogout.setImageResource(R.drawable.logout_red);
//                txtGbrLogout.setTextColor(Color.parseColor("#e40112"));
//                final Dialog dialog = new Dialog(CreatePromoActivity.this);
//                dialog.setContentView(R.layout.popup_logout);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                RelativeLayout ya = dialog.findViewById(R.id.logoutYa);
//                RelativeLayout tidak = dialog.findViewById(R.id.logoutTidak);
//                ya.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        session.logoutUser();
//                    }
//                });
//                tidak.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        gbrLogout.setImageResource(R.drawable.logout_grey);
//                        txtGbrLogout.setTextColor(Color.parseColor("#FF5F5D5D"));
//                        dialog.dismiss();
//                    }
//                });
//                dialog.setCanceledOnTouchOutside(false);
//                dialog.show();
//            }
//        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        Button tambahPromo = findViewById(R.id.tambahPromo);
        tambahPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CreatePromoActivity.this, SettingPromoActivity.class);
                startActivity(i);
            }
        });
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
        prepareDataViewPromo();
        super.onResume();
    }

    private void prepareDataViewPromo() {
        showProgressDialog();
        new ApiVolley(this, new JSONObject(), "GET", URL.urlViewPromo, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                dismissProgressDialog();
                promoLama = new ArrayList<>();
                promoBaru = new ArrayList<>();
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        JSONArray array = object.getJSONArray("response");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject isi = array.getJSONObject(i);
                            promoLama.add(new CreatePromoModel(
                                    isi.getString("id"),
                                    isi.getString("gambar"),
                                    isi.getString("title"),
                                    isi.getString("status"),
                                    isi.getString("id_k")
                            ));
                        }

                        rvView.setAdapter(null);
                        adapter = new CreatePromoAdapter(CreatePromoActivity.this, promoLama);
                        rvView.setAdapter(adapter);

//                        for (int i = start; i < count; i++) {
//                            if (i < promoLama.size()) {
//                                promoBaru.add(promoLama.get(i));
//                            }
//                        }
//

//
//                        EndlessScroll scrollListener = new EndlessScroll((LinearLayoutManager) layoutManager) {
//                            @Override
//                            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                                start += count;
//                                for (int i = start; i < start + count; i++) {
//                                    if (i < promoLama.size()) promoBaru.add(promoLama.get(i));
//                                }
//                                adapter.addMoreData();
//                            }
//                        };
//                        rvView.addOnScrollListener(scrollListener);

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
//        Intent i = new Intent(CreatePromoActivity.this, HomeActivity.class);
//        startActivity(i);
        finish();
//        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(CreatePromoActivity.this,
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
