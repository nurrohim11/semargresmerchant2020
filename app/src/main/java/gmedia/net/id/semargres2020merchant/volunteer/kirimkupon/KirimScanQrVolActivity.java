package gmedia.net.id.semargres2020merchant.volunteer.kirimkupon;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.semargres2020merchant.BerhasilQrCodeActivity;
import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.kategoriKupon.SettingKategoriKuponModel;
import gmedia.net.id.semargres2020merchant.merchant.KirimScanQrMerActivity;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.RuntimePermissionsActivity;
import gmedia.net.id.semargres2020merchant.util.SessionManager;
import gmedia.net.id.semargres2020merchant.util.TwoItemModel;
import gmedia.net.id.semargres2020merchant.util.URL;
import gmedia.net.id.semargres2020merchant.volunteer.MerchantAdapter;
import gmedia.net.id.semargres2020merchant.volunteer.MerchantModel;
import gmedia.net.id.semargres2020merchant.volunteer.SessionMerchant;

import static gmedia.net.id.semargres2020merchant.util.Utils.isEmailValid;
import static gmedia.net.id.semargres2020merchant.volunteer.SessionMerchant.SP_ID_MERCHANT_EMAIL;
import static gmedia.net.id.semargres2020merchant.volunteer.SessionMerchant.SP_ID_MERCHANT_QR;
import static gmedia.net.id.semargres2020merchant.volunteer.SessionMerchant.SP_NAMA_MERCHANT_EMAIL;
import static gmedia.net.id.semargres2020merchant.volunteer.SessionMerchant.SP_NAMA_MERCHANT_QR;

public class KirimScanQrVolActivity extends RuntimePermissionsActivity implements MerchantAdapter.MerchantAdapterCallback {

    Toolbar toolbar;
    ScrollView svContainer;

    public static Dialog dgMerchant;
    private RecyclerView rvMerchant;
    MerchantAdapter merchantAdapter;
    private List<MerchantModel> merchantModels = new ArrayList<>();
    private List<TwoItemModel> caraBayarList = new ArrayList<>();
    private List<SettingKategoriKuponModel> kategoriKupon = new ArrayList<>();
    private SessionMerchant sessionMerchant;
    private String keyword_merchant="";
    private int start =0, count=20;

    private LinearLayout llSelectMerchant;
    private TextView tvMerchantScan;
    private EditText edtNominal;
    private Button btnKirim;
    private Spinner spCaraBayar, spKategori;

    String id_merchant="0";
    String cara_bayar="", kategori_kupon="";

    SessionManager session;
    private IntentResult resultScanBarcode;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kirim_scan_qr_vol);
        sessionMerchant = new SessionMerchant(this);
        session = new SessionManager(this);
        getCaraBayarData();
        initUi();
    }

    private void initUi(){
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar));
        }

        toolbar = findViewById(R.id.toolbar);
        svContainer = findViewById(R.id.sv_container);
        svContainer.setVerticalScrollBarEnabled(false);
        svContainer.setHorizontalScrollBarEnabled(false);

        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("Kalkulator E-Kupon By Scan QR");
        }

        llSelectMerchant = findViewById(R.id.ll_select_merchant);
        llSelectMerchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionMerchant = new SessionMerchant(getApplicationContext());
                showDialogMerchant();
            }
        });
        tvMerchantScan = findViewById(R.id.tv_merchant);
        tvMerchantScan.setText(sessionMerchant.getSpNamaQr());

        edtNominal = findViewById(R.id.edt_nominal);
        spCaraBayar = (Spinner) findViewById(R.id.sp_cara_bayar);
        spKategori = (Spinner) findViewById(R.id.sp_kategori);
        id_merchant = sessionMerchant.getSpIdQr();
        getKategoriByMerchant(id_merchant);
        btnKirim = findViewById(R.id.btn_send);
        spKategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SettingKategoriKuponModel model = kategoriKupon.get(position);
                kategori_kupon = model.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spCaraBayar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TwoItemModel twoItemModel = caraBayarList.get(position);
                cara_bayar = twoItemModel.getItem1();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sessionMerchant == null && sessionMerchant.getSpIdQr().equals("")){
                    tvMerchantScan.setError("Silahkan pilih merchant dahulu");
                    tvMerchantScan.requestFocus();
                    return;
                }

                if(spKategori ==null && spKategori.getSelectedItem() == null){
                    TextView errorText = (TextView)spKategori.getSelectedView();
                    errorText.setError("Silahkan memilih kategori");
                    errorText.requestFocus();
                    return;
                }

                if (edtNominal.getText().toString().equals("")) {
                    edtNominal.setError("Silahkan mengisi nominal");
                    edtNominal.requestFocus();
                    return;
                }

                AlertDialog alertDialog = new AlertDialog.Builder(KirimScanQrVolActivity.this)
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
        IntentIntegrator integrator = new IntentIntegrator(KirimScanQrVolActivity.this);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        resultScanBarcode = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (resultScanBarcode != null) {

            if (resultScanBarcode.getContents() != null) {
                // TODO to action if after scan barcode
                Toast.makeText(this, resultScanBarcode.getContents(), Toast.LENGTH_SHORT).show();
                prepareDataScanBarcode();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == 601){
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAllMerchant(){
        JSONObject params = new JSONObject();
        try {
            params.put("keyword",keyword_merchant);
            params.put("start",start);
            params.put("count",count);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ApiVolley(KirimScanQrVolActivity.this, params, "POST", URL.urlAllMerchant, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);

                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        JSONArray arr = object.getJSONArray("response");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject isi = arr.getJSONObject(i);
                            merchantModels.add(new MerchantModel(
                                    isi.getString("id"),
                                    isi.getString("nama"),
                                    isi.getString("alamat"),
                                    isi.getString("email"),
                                    isi.getString("deskripsi"),
                                    isi.getString("kategori"),
                                    isi.getString("latitude"),
                                    isi.getString("longitude")
                            ));
                        }
                        merchantAdapter.notifyDataSetChanged();
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

    private void showDialogMerchant(){
        merchantModels.clear();
        keyword_merchant="";
        dgMerchant = new Dialog(KirimScanQrVolActivity.this);
        dgMerchant.setContentView(R.layout.popup_merchant);
        dgMerchant.setCancelable(false);
        RelativeLayout rvCancel = dgMerchant.findViewById(R.id.rv_cancel);
        rvMerchant = dgMerchant.findViewById(R.id.rv_merchant);
        setupListMerchantSms();
        setupListScrollListenerMerchantSms();
        start =0;
        count =20;
        getAllMerchant();

        final EditText edtSearchMerchant = dgMerchant.findViewById(R.id.edt_search_merchant);
        edtSearchMerchant.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(edtSearchMerchant.getWindowToken(), 0);
                    keyword_merchant = edtSearchMerchant.getText().toString().trim();
                    start =0;
                    count =20;
                    merchantModels.clear();
                    getAllMerchant();
                    merchantAdapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            }
        });
        edtSearchMerchant.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(edtSearchMerchant.getText().toString().length() == 0) {
                    keyword_merchant="";
                    start=0;
                    count=20;
                    getAllMerchant();
                }
            }
        });

        rvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dgMerchant.dismiss();
            }
        });
        dgMerchant.show();
    }

    private void setupListMerchantSms() {
        merchantAdapter = new MerchantAdapter(KirimScanQrVolActivity.this, merchantModels,this);
        rvMerchant.setLayoutManager(new LinearLayoutManager(KirimScanQrVolActivity.this));
        rvMerchant.setAdapter(merchantAdapter);
    }

    private void setupListScrollListenerMerchantSms() {
        rvMerchant.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (! recyclerView.canScrollVertically(1)) {
                    start += count;
                    getAllMerchant();
                }
            }
        });
    }

    private void getCaraBayarData() {

        new ApiVolley(KirimScanQrVolActivity.this, new JSONObject(), "GET", URL.urlCaraBayar, "", "", 0, new ApiVolley.VolleyCallback() {
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
                        spCaraBayar.setAdapter(new ArrayAdapter<>(KirimScanQrVolActivity.this, R.layout.layout_simple_list, caraBayarList));
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

    private void getKategoriByMerchant(String id_merchant){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id_merchant",id_merchant);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(KirimScanQrVolActivity.this, jsonObject, "POST", URL.urlKategoriKuponByMerchant, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                kategoriKupon.clear();
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");

                    Log.d(">>>>>", String.valueOf(object));

                    if (status.equals("200")) {
                        JSONArray jArray = object.getJSONObject("response").getJSONArray("kategori");
                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject jo = jArray.getJSONObject(i);
                            kategoriKupon.add(new SettingKategoriKuponModel(jo.getString("id"), jo.getString("nama"),jo.getString("nominal")));
                        }
                        spKategori.setAdapter(new ArrayAdapter<>(KirimScanQrVolActivity.this, R.layout.layout_simple_list, kategoriKupon));
                    }else{
                        spKategori.setAdapter(null);
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

    @Override
    public void onRowMerchantAdapterClicked(String id_m, String nama) {
        spKategori.setSelection(-1);
        id_merchant = id_m;
        getKategoriByMerchant(id_merchant);
        sessionMerchant.saveSPString(SP_ID_MERCHANT_QR, id_merchant);
        sessionMerchant.saveSPString(SP_NAMA_MERCHANT_QR, nama);
        tvMerchantScan.setText(sessionMerchant.getSpNamaQr());
        dgMerchant.dismiss();
    }

    private void prepareDataScanBarcode() {
        showProgressDialog();

        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("qrcode_user", resultScanBarcode.getContents());
            jBody.put("id_merchant",sessionMerchant.getSpIdQr());
            jBody.put("total", edtNominal.getText().toString());
            jBody.put("cara_bayar", cara_bayar);
            jBody.put("id_kategori_kupon", kategori_kupon);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(KirimScanQrVolActivity.this, jBody, "POST", URL.urlKirimKuponViaScanVolunter, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dismissProgressDialog();
                Log.d(">>>>>",result);
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        JSONObject detail = object.getJSONObject("response");

                        Intent i = new Intent(KirimScanQrVolActivity.this, BerhasilQrCodeActivity.class);
                        i.putExtra("type", "volunteer");
                        i.putExtra("nama", detail.getString("nama"));
                        i.putExtra("email", detail.getString("email"));
                        i.putExtra("telpon", detail.getString("no_telp"));
                        i.putExtra("gambar", detail.getString("foto"));
                        i.putExtra("jumlah_kupon", detail.getString("jumlah_kupon"));
                        startActivityForResult(i,601);
//                        finish();
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
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(KirimScanQrVolActivity.this,
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
