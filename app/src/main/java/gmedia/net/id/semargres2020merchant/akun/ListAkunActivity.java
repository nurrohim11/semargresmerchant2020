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

//    private boolean isPassVisible = true, isRepassVisible = true;
//    private EditText edtUsername, edtPassword, edtRePassword;
//    private Spinner sp_kategori_tambah_akun;
//    private ArrayList<SettingKategoriKuponModel> array_kategori_tambah_akun;
//    private String kategori_tambah_akun = "0";

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

//        isPassVisible = true;
//        isRepassVisible = true;

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
//                final Dialog dialog = new Dialog(ListAkunActivity.this);
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

        Button ivTambahAkun = findViewById(R.id.iv_tambah_akun);

        ivTambahAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ListAkunActivity.this, TambahAkunActivity.class),101);
//                getKategoriKupon();
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

//        ApiVolley request = new ApiVolley(this, new JSONObject(), "GET", URL.urlGetAkun, "", "", 0, new ApiVolley.VolleyCallback() {

        showProgressDialog();
        new ApiVolley(this, new JSONObject(), "POST", URL.urlUserAkun, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dismissProgressDialog();
                listAkun = new ArrayList<>();
//                akunBaru = new ArrayList<>();

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

//                        for (int i = start; i < count; i++) {
//                            if (i < listAkun.size()) {
//                                akunBaru.add(listAkun.get(i));
//                            }
//                        }

                        rvView.setAdapter(null);
                        adapter = new ListAkunAdapter(ListAkunActivity.this, listAkun);
                        rvView.setAdapter(adapter);

//                        EndlessScroll scrollListener = new EndlessScroll((LinearLayoutManager) layoutManager) {
//                            @Override
//                            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                                start += count;
//                                for (int i = start; i < start + count; i++) {
//                                    if (i < listAkun.size()) akunBaru.add(listAkun.get(i));
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
//        Intent i = new Intent(ListAkunActivity.this, HomeActivity.class);
//        startActivity(i);
        finish();
//        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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

//    private void getKategoriKupon() {
//        ApiVolley request = new ApiVolley(this, new JSONObject(), "POST", URL.urlKategoriKupon, "", "", 0, new ApiVolley.VolleyCallback() {
//            @Override
//            public void onSuccess(String result) {
//                array_kategori_tambah_akun = new ArrayList<>();
//
//                try {
//                    JSONObject object = new JSONObject(result);
//                    String status = object.getJSONObject("metadata").getString("status");
//                    String message = object.getJSONObject("metadata").getString("message");
//                    if (status.equals("200")) {
////                        Toast.makeText(getApplicationContext(),status,Toast.LENGTH_LONG).show();
//                        JSONArray array = object.getJSONObject("response").getJSONArray("kategori");
//                        for (int i = 0; i < array.length(); i++) {
//                            JSONObject isi = array.getJSONObject(i);
//                            array_kategori_tambah_akun.add(new SettingKategoriKuponModel(
//                                    isi.getString("id"),
//                                    isi.getString("nama"),
//                                    isi.getString("nominal")
//                            ));
//                        }
////                        showTambahAkun(array_kategori_tambah_akun);
//                    } else {
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(String result) {
//                Toast.makeText(getApplicationContext(), "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    private void showTambahAkun(ArrayList<SettingKategoriKuponModel> list){
//
//        final Dialog dialog = new Dialog(ListAkunActivity.this);
//        dialog.setContentView(R.layout.popup_tambah_akun);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        final ImageView ivPass = dialog.findViewById(R.id.iv_password);
////        final ImageView ivRePass = dialog.findViewById(R.id.iv_repassword);
//        edtUsername = (EditText) dialog.findViewById(R.id.edt_username);
//        edtPassword = dialog.findViewById(R.id.edt_password);
////        edtRePassword = dialog.findViewById(R.id.edt_repassword);
//        sp_kategori_tambah_akun = dialog.findViewById(R.id.sp_kategori_tambah_akun);
//        RelativeLayout rvOK = dialog.findViewById(R.id.rv_ok);
//        RelativeLayout rvCancel = dialog.findViewById(R.id.rv_cancel);
//
//        ivPass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isPassVisible) {
//                    ivPass.setImageDrawable(getResources().getDrawable(R.drawable.visible));
//                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
//                    isPassVisible = false;
//                } else {
//                    ivPass.setImageDrawable(getResources().getDrawable(R.drawable.invisible));
//                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//                    isPassVisible = true;
//                }
//            }
//        });
//
////        ivRePass.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                if (isRepassVisible) {
////                    ivRePass.setImageDrawable(getResources().getDrawable(R.drawable.visible));
////                    edtRePassword.setInputType(InputType.TYPE_CLASS_TEXT);
////                    isRepassVisible = false;
////                } else {
////                    ivRePass.setImageDrawable(getResources().getDrawable(R.drawable.invisible));
////                    edtRePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
////                    edtRePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
////                    isRepassVisible= true;
////                }
////            }
////        });
//
//
//        ArrayAdapter adapter = new ArrayAdapter(ListAkunActivity.this, R.layout.layout_simple_list, list);
//        sp_kategori_tambah_akun.setAdapter(adapter);
//        sp_kategori_tambah_akun.setSelection(0, true);
//
//        rvOK.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(edtUsername.getText().toString().equals("")){
//                    edtUsername.setError("Username harap diisi");
//                    edtUsername.requestFocus();
//                    return;
//                }
//
//                if(edtPassword.getText().toString().equals("")){
//                    edtPassword.setError("Password harap diisi");
//                    edtPassword.requestFocus();
//                    return;
//                }
//
////                if(edtRePassword.getText().toString().equals("")){
////                    edtRePassword.setError("Password ulang harap diisi");
////                    edtRePassword.requestFocus();
////                    return;
////                }
////
////                if(!edtRePassword.getText().toString().equals(edtPassword.getText().toString())){
////                    edtRePassword.setError("Password tidak sama, cek kembali");
////                    edtRePassword.requestFocus();
////                    return;
////                }
//
//                kategori_tambah_akun = "0";
//                SettingKategoriKuponModel kategori = (SettingKategoriKuponModel) sp_kategori_tambah_akun.getSelectedItem();
//                kategori_tambah_akun = kategori.getId();
//
////                saveNewAccount();
//                dialog.dismiss();
//            }
//        });
//
//        rvCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.show();
//    }
//
//    private void saveNewAccount() {
//
//        showProgressDialog();
//        final JSONObject jBody = new JSONObject();
//        try {
//            jBody.put("username", edtUsername.getText());
//            jBody.put("password", edtPassword.getText());
//            jBody.put("id_kategori_kupon", String.valueOf(kategori_tambah_akun));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
////        ApiVolley request = new ApiVolley(this, jBody, "POST", URL.urlTambahAkun, "", "", 0, new ApiVolley.VolleyCallback() {
//            ApiVolley request = new ApiVolley(this, jBody, "POST", URL.urlStoreUserAkun, "", "", 0, new ApiVolley.VolleyCallback() {
//            @Override
//            public void onSuccess(String result) {
//
//                dismissProgressDialog();
//                try {
//                    JSONObject object = new JSONObject(result);
//                    Log.d("hasil object", object.toString());
//                    String pesan = object.getJSONObject("metadata").getString("message");
//                    String status = object.getJSONObject("response").getString("status");
//                    if (status.equals("1")) {
//                        Toast.makeText(getApplicationContext(), pesan, Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), pesan, Toast.LENGTH_LONG).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                getDataAkun();
//            }
//
//            @Override
//            public void onError(String result) {
//                Toast.makeText(getApplicationContext(), "Terjadi kesalahan saat memuat data", Toast.LENGTH_LONG).show();
//                dismissProgressDialog();
//            }
//        });
//    }

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
