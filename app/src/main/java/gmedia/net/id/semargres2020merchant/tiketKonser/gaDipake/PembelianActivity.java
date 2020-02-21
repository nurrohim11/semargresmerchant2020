package gmedia.net.id.semargres2020merchant.tiketKonser.gaDipake;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmedia.apps.Converter;
import com.gmedia.apps.DialogFactory;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.semargres2020merchant.HomeActivity;
import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.tiketKonser.GambarDenahActivity;
import gmedia.net.id.semargres2020merchant.tiketKonser.pembelian.TiketKonserModel;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.URL;

public class PembelianActivity extends AppCompatActivity {

    private List<TiketKonserModel> listJenisTiket = new ArrayList<>();
    private ArrayAdapter<TiketKonserModel> adapter;

    private int selected_jenis = -1;

    private ImageView img_konser;
    private EditText txt_jumlah, txt_email, txt_kodePromo, txt_whatsapp;
    private TextView txt_diskon, txt_total, txt_denah;

    private AppCompatSpinner spn_jenis;

    private Double diskon = 0.0;

    private ProgressDialog progressDialog;

    private String qrCode = "";
    private String denah = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembelian);

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

        spn_jenis = findViewById(R.id.spn_jenis);
        img_konser = findViewById(R.id.img_konser);
        txt_jumlah = findViewById(R.id.txt_jumlah);
        txt_email = findViewById(R.id.txt_emailTiket);
        txt_kodePromo = findViewById(R.id.txt_kodePromo);
        txt_whatsapp = findViewById(R.id.txt_whatsapp);
        txt_diskon = findViewById(R.id.txt_diskonPromo);

        txt_total = findViewById(R.id.txt_total);
        txt_total.setText(Converter.doubleToRupiah(0));

        String editable = getIntent().getStringExtra("editable");
        if(editable.equals("false")){
            getEmail();
            txt_email.setEnabled(false);
        }
        else{
            txt_email.setEnabled(true);
        }

        if(getIntent().getStringExtra("qr_code")!=null){
            qrCode = getIntent().getStringExtra("qr_code");
        }

        getKonser();

//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listJenisTiket);
//        spn_jenis.setAdapter(adapter);

        spn_jenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_jenis = position;
                if(txt_jumlah.getText().toString().equals("")){
                    txt_total.setText(Converter.doubleToRupiah(0));
                }
                else{

//                    txt_total.setText(Converter.doubleToRupiah(
//                            listJenisTiket.get(selected_jenis).getHarga() *
//                                    Integer.parseInt(txt_jumlah.getText().toString())));

                    txt_total.setText(Converter.doubleToRupiah(
                            (listJenisTiket.get(selected_jenis).getHarga() *
                                    Integer.parseInt(txt_jumlah.getText().toString())) -
                                    (listJenisTiket.get(selected_jenis).getHarga() *
                                            Integer.parseInt(txt_jumlah.getText().toString()) *
                                            diskon) ));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.btn_cekKode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekKodePromo();
            }
        });

        findViewById(R.id.btn_beli).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txt_jumlah.getText().toString().equals("") ||
                        Double.parseDouble(txt_jumlah.getText().toString()) <= 0){
                    Toast.makeText(PembelianActivity.this, "Jumlah tiket tidak boleh kosong", Toast.LENGTH_SHORT).show();
                }
                else if(selected_jenis == -1){
                    Toast.makeText(PembelianActivity.this, "Jenis tiket belum dipilih", Toast.LENGTH_SHORT).show();
                }
                else{
                    AlertDialog alertDialog = new AlertDialog.Builder(PembelianActivity.this)
                            .setTitle("Konfirmasi")
                            .setMessage("Apakah Anda yakin ingin melakukan pembelian atas nama email : \n\n"+txt_email.getText().toString()+"?")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if(qrCode.equals("")){
                                        beliTiketEmail();
                                    }
                                    else{
                                        beliTiketScanQR();
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
            }
        });

        txt_jumlah.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(selected_jenis != -1){
                    if(s.toString().equals("")){
                        txt_total.setText(Converter.doubleToRupiah(0));
                    }
                    else{

//                        txt_total.setText(Converter.doubleToRupiah(
//                                listJenisTiket.get(selected_jenis).getHarga() *
//                                        Integer.parseInt(txt_jumlah.getText().toString())));

                        txt_total.setText(Converter.doubleToRupiah(
                                (listJenisTiket.get(selected_jenis).getHarga() *
                                        Integer.parseInt(txt_jumlah.getText().toString())) -
                                        (listJenisTiket.get(selected_jenis).getHarga() *
                                                Integer.parseInt(txt_jumlah.getText().toString()) *
                                                diskon) ));
                    }
                }
            }
        });

        txt_denah = findViewById(R.id.txt_denah);
        txt_denah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PembelianActivity.this, GambarDenahActivity.class);
                i.putExtra("denah", denah);
                startActivity(i);
            }
        });

//        txt_kodePromo.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String diskon = "10%";
//                txt_diskon.setText(diskon);
//            }
//        });

//        findViewById(R.id.img_konser).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showGagal();
//            }
//        });

//        loadJenisTiket();
    }

    private void getEmail(){
        showProgressDialog();
        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("qr_data", qrCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(PembelianActivity.this, jBody, "POST", URL.urlGetEmail, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    Log.d("hasil email", object.toString());
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        txt_email.setText(object.getJSONObject("response").getString("email"));
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
                Toast.makeText(PembelianActivity.this, result, Toast.LENGTH_SHORT).show();
                showGagal();
            }
        });
//        showBerhasil();
    }

    private void getKonser(){
        new ApiVolley(PembelianActivity.this, new JSONObject(), "GET", URL.urlGetKonser, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
//                    Log.d("hasil konser", object.toString());
                    final String status = object.getJSONObject("metadata").getString("status");
                    final String message = object.getJSONObject("metadata").getString("message");
                    if(status.equals("200")){
                        JSONObject konser = object.getJSONObject("response").getJSONObject("konser");
                        Picasso.with(PembelianActivity.this).load(konser.getString("gambar")).into(img_konser);
                        denah = konser.getString("denah");

                        JSONArray jenisTiket = object.getJSONObject("response").getJSONArray("jenis_tiket");
                        for (int i = 0; i < jenisTiket.length(); i++) {
                            JSONObject isi = jenisTiket.getJSONObject(i);
                            listJenisTiket.add(new TiketKonserModel(
                                    isi.getString("id"),
                                    isi.getString("nama"),
                                    Double.parseDouble(isi.getString("harga")),
                                    Boolean.parseBoolean(isi.getString("status_sold_out"))
                            ));
                        }
                        adapter = new ArrayAdapter(PembelianActivity.this, android.R.layout.simple_spinner_dropdown_item, listJenisTiket);
                        spn_jenis.setAdapter(adapter);
                    }
                    else{
                        Toast.makeText(PembelianActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(PembelianActivity.this, result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cekKodePromo(){
        showProgressDialog();
        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("email", txt_email.getText().toString());
            jBody.put("id_jenis_tiket", listJenisTiket.get(selected_jenis).getId());
            jBody.put("jumlah", txt_jumlah.getText().toString());
            jBody.put("kode_promo", txt_kodePromo.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(PembelianActivity.this, jBody, "POST", URL.urlCekPromo, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    Log.d("hasil check", object.toString());
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        String persenDiskon = object.getJSONObject("response").getString("discount_percent");
                        diskon = Double.parseDouble(persenDiskon) / 100;

                        String diskonTampil = persenDiskon + "%";
                        txt_diskon.setText(diskonTampil);

                        if(selected_jenis != -1){
                            if(txt_jumlah.getText().toString().equals("")){
                                txt_total.setText(Converter.doubleToRupiah(0));
                            }
                            else{
                                txt_total.setText(Converter.doubleToRupiah(
                                        (listJenisTiket.get(selected_jenis).getHarga() *
                                                Integer.parseInt(txt_jumlah.getText().toString())) -
                                                (listJenisTiket.get(selected_jenis).getHarga() *
                                                        Integer.parseInt(txt_jumlah.getText().toString()) *
                                                        diskon) ));
                            }
                        }
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
                Toast.makeText(PembelianActivity.this, result, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void beliTiketEmail(){
        showProgressDialog();
        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("email", txt_email.getText().toString());
            jBody.put("id_jenis_tiket", listJenisTiket.get(selected_jenis).getId());
            jBody.put("jumlah", txt_jumlah.getText().toString());
            jBody.put("kode_promo", txt_kodePromo.getText().toString());
            jBody.put("nomor_wa", txt_whatsapp.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(PembelianActivity.this, jBody, "POST", URL.urlTiketEmail, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        showBerhasil(txt_email.getText().toString(), txt_total.getText().toString(), listJenisTiket.get(selected_jenis).getNama(), txt_jumlah.getText().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        showGagal();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                dismissProgressDialog();
                Toast.makeText(PembelianActivity.this, result, Toast.LENGTH_SHORT).show();
                showGagal();
            }
        });
    }

    private void beliTiketScanQR(){
        showProgressDialog();
        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("qr_data", qrCode);
            jBody.put("email", txt_email.getText().toString());
            jBody.put("id_jenis_tiket", listJenisTiket.get(selected_jenis).getId());
            jBody.put("jumlah", txt_jumlah.getText().toString());
            jBody.put("kode_promo", txt_kodePromo.getText().toString());
            jBody.put("nomor_wa", txt_whatsapp.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(PembelianActivity.this, jBody, "POST", URL.urlTiketScanQR, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        showBerhasil(txt_email.getText().toString(), txt_total.getText().toString(), listJenisTiket.get(selected_jenis).getNama(), txt_jumlah.getText().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        showGagal();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                dismissProgressDialog();
                Toast.makeText(PembelianActivity.this, result, Toast.LENGTH_SHORT).show();
                showGagal();
            }
        });
    }

    private void showBerhasil(String email, String total, String jenis, String jumlah){
        final Dialog dialog = DialogFactory.getInstance().createDialog(this, R.layout.popup_tiket_berhasil,
                80, 60);

        TextView txt_email = dialog.findViewById(R.id.txt_email);
        txt_email.setText(email);

        TextView txt_jenis = dialog.findViewById(R.id.txt_jenisTiket);
        txt_jenis.setText(jenis);

        TextView txt_total = dialog.findViewById(R.id.txt_total);
        txt_total.setText(total);

        TextView txt_jumlah = dialog.findViewById(R.id.txt_jumlahTiket);
        txt_jumlah.setText(jumlah);

        Button btn_ok = dialog.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showGagal(){
        final Dialog dialog = DialogFactory.getInstance().createDialog(this, R.layout.popup_tiket_gagal,
                80, 45);
        Button btn_ulang = dialog.findViewById(R.id.btn_ulang);
        btn_ulang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

//    private void loadJenisTiket(){
//        /*AppLoading.getInstance().showLoading(activity);
//        ApiVolleyManager.getInstance().addSecureRequest(activity, Constant.URL_TIKET_KONSER,
//                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(activity),
//                new AppRequestCallback(new AppRequestCallback.RequestListener() {
//                    @Override
//                    public void onEmpty(String message) {
//                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
//                        AppLoading.getInstance().stopLoading();
//                    }
//
//                    @Override
//                    public void onSuccess(String result) {
//                        try{
//                            JSONObject konser = new JSONObject(result).getJSONObject("konser");
//                            ImageLoader.load(activity, konser.getString("gambar"), img_konser);
//
//                            JSONArray jenis_tiket = new JSONObject(result).getJSONArray("jenis_tiket");
//                            for(int i = 0; i < jenis_tiket.length(); i++){
//                                JSONObject tiket = jenis_tiket.getJSONObject(i);
//                                listJenisTiket.add(new TiketKonserModel(tiket.getString("id"),
//                                        tiket.getString("nama"), tiket.getDouble("harga"),
//                                        tiket.getInt("status_sold_out")==1));
//                            }
//
//                            adapter.notifyDataSetChanged();
//                        }
//                        catch (JSONException e){
//                            Log.e(Constant.TAG, e.getMessage());
//                        }
//
//                        AppLoading.getInstance().stopLoading();
//                    }
//
//                    @Override
//                    public void onFail(String message) {
//                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
//                        AppLoading.getInstance().stopLoading();
//                    }
//                }));*/
//
//        trustAllCertificates();
//        ImageLoader.load(PembelianActivity.this,
//                "https://semargres.gmedia.id/assets/uploads/tiket_konser/5bf223e050d59d7ff5b1201bec801be5.jpg",
//                img_konser);
//
//        listJenisTiket.add(new TiketKonserModel("1",
//                "Diamond", 2000000,
//                false));
//        listJenisTiket.add(new TiketKonserModel("2",
//                "Gold", 1500000,
//                false));
//        listJenisTiket.add(new TiketKonserModel("3",
//                "Silver", 1000000,
//                false));
//
//        adapter.notifyDataSetChanged();
//    }

//    private void trustAllCertificates(){
//        try {
//            setDefaultHostnameVerifier(new HostnameVerifier(){
//                public boolean verify(String hostname, SSLSession session) {
//                    return hostname.equalsIgnoreCase("semargres.gmedia.id") ||
//                            hostname.equalsIgnoreCase("api.crashlytics.com") ||
//                            hostname.equalsIgnoreCase("settings.crashlytics.com") ||
//                            hostname.equalsIgnoreCase("clients4.google.com") ||
//                            hostname.equalsIgnoreCase("www.facebook.com") ||
//                            hostname.equalsIgnoreCase("www.instagram.com") ||
//                            hostname.equalsIgnoreCase("lh1.googleusercontent.com") ||
//                            hostname.equalsIgnoreCase("lh2.googleusercontent.com") ||
//                            hostname.equalsIgnoreCase("lh3.googleusercontent.com") ||
//                            hostname.equalsIgnoreCase("lh4.googleusercontent.com") ||
//                            hostname.equalsIgnoreCase("lh5.googleusercontent.com") ||
//                            hostname.equalsIgnoreCase("lh6.googleusercontent.com") ||
//                            hostname.equalsIgnoreCase("lh7.googleusercontent.com") ||
//                            hostname.equalsIgnoreCase("lh8.googleusercontent.com") ||
//                            hostname.equalsIgnoreCase("lh9.googleusercontent.com") ||
//                            hostname.equalsIgnoreCase("graph.facebook.com") ||
//                            hostname.equalsIgnoreCase("googleusercontent.com") ||
//                            hostname.equalsIgnoreCase("scontent.xx.fbcdn.net") ||
//                            hostname.equalsIgnoreCase("lookaside.facebook.com");
//                }});
//            SSLContext context = SSLContext.getInstance("TLS");
//            context.init(null, new X509TrustManager[]{new X509TrustManager(){
//                public void checkClientTrusted(X509Certificate[] chain,
//                                               String authType) throws CertificateException {}
//                public void checkServerTrusted(X509Certificate[] chain,
//                                               String authType) throws CertificateException {}
//                public X509Certificate[] getAcceptedIssuers() {
//                    return new X509Certificate[0];
//                }}}, new SecureRandom());
//            setDefaultSSLSocketFactory(
//                    context.getSocketFactory());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(PembelianActivity.this,
                R.style.AppTheme_Custom_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setMessage("Mohon tunggu sebentar...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PembelianActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
