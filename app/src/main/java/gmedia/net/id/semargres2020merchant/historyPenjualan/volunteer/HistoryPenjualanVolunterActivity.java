package gmedia.net.id.semargres2020merchant.historyPenjualan.volunteer;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.historyPenjualan.HistoryPenjualanAdapter;
import gmedia.net.id.semargres2020merchant.historyPenjualan.HistoryPenjualanModel;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.FormatRupiah;
import gmedia.net.id.semargres2020merchant.util.URL;
import gmedia.net.id.semargres2020merchant.util.Utils;
import gmedia.net.id.semargres2020merchant.volunteer.MerchantAdapter;
import gmedia.net.id.semargres2020merchant.volunteer.MerchantModel;

public class HistoryPenjualanVolunterActivity extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout llSelectMerchant;

    public static Dialog dgMerchant;
    private RecyclerView rvMerchant;
    MerchantAdapter merchantAdapter;

    int start_merchant = 0, count_merchant = 20, start = 0, count = 10;
    String id_merchant="";

    TextView tvStartDate, tvEndDate;
    Button btnFilter;

    String start_date ="",end_date="";
    private Calendar calendar;
    private int mYear, mMonth, mDay;

    private RecyclerView rvView;
    private HistoryPenjualanVolunteerAdapter adapter;
    private ArrayList<HistoryPenjualanModel> historyPenjualan = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_penjualan_volunter);
        id_merchant ="";
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
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(calendar.getTime());
        start_date= date;
        end_date= date;
        tvStartDate = findViewById(R.id.tv_startdate);
        tvStartDate.setText(start_date);
        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryPenjualanVolunterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                int m = monthOfYear+1;
                                String bulan = String.valueOf(m);
                                if(String.valueOf(monthOfYear).length() == 1){
                                    bulan = "0"+m;
                                }

                                int d = dayOfMonth;
                                String tanggal = String.valueOf(d);
                                if(tanggal.length() == 1){
                                    tanggal = "0"+d;
                                }
                                start_date = year+"-"+bulan+"-"+tanggal;
                                tvStartDate.setText(start_date);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        tvEndDate = findViewById(R.id.tv_enddate);
        tvEndDate.setText(end_date);
        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryPenjualanVolunterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                int m = monthOfYear+1;
                                String bulan = String.valueOf(m);
                                if(String.valueOf(monthOfYear).length() == 1){
                                    bulan = "0"+m;
                                }

                                int d = dayOfMonth;
                                String tanggal = String.valueOf(d);
                                if(tanggal.length() == 1){
                                    tanggal = "0"+d;
                                }
                                end_date = year+"-"+bulan+"-"+tanggal;
                                tvEndDate.setText(end_date);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        btnFilter = findViewById(R.id.btn_filter);

        rvView = findViewById(R.id.rv_history_penjualan);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(HistoryPenjualanVolunterActivity.this);
        rvView.setLayoutManager(layoutManager);
        setupListHistoryPenjualan();
        setupListScrollListenerHistoryPenjualan();
        prepareDataHistoryPenjualan("");

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
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start =0;
                count =10;
//                historyPenjualan.clear();
                adapter.notifyDataSetChanged();
                prepareDataHistoryPenjualan("search");
            }
        });
    }

    private void prepareDataHistoryPenjualan(final String type) {
        showProgressDialog();
        if(start == 0){
            historyPenjualan.clear();
            dismissProgressDialog();
        }
        JSONObject params = new JSONObject();
        try {
            params.put("start_date",start_date);
            params.put("end_date",end_date);
            params.put("start",start);
            params.put("limit",count);
        }catch (Exception e){
            e.printStackTrace();
        }

        new ApiVolley(this, params, "POST", URL.getUrlHistoryPenjualanVolunter, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                dismissProgressDialog();
                Log.d(">>>>>", result);
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        JSONArray array = object.getJSONArray("response");
                        FormatRupiah request = new FormatRupiah();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject isi = array.getJSONObject(i);
                            historyPenjualan.add(new HistoryPenjualanModel(
                                    isi.getString("email_user"),

                                    isi.getString("waktu"),

                                    isi.getString("jumlah_kupon"),

                                    request.ChangeToRupiahFormat(isi.getString("total_nominal")),

                                    ChangeFormatDateString(isi.getString("waktu"), "yyyy-MM-dd HH:mm:ss", "HH:mm:ss"),

                                    isi.getString("nama_user"),

                                    isi.getString("nama_volunteer"),

                                    isi.getString("nama_merchant")
                            ));
                        }

                        adapter.notifyDataSetChanged();
                    }else{
                        if(type.equals("search")){
                            historyPenjualan.clear();
                            adapter.notifyDataSetChanged();
                            dismissProgressDialog();
                        }
                        dismissProgressDialog();
                    }
//                    else {
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                    }
                } catch (JSONException e) {
                    dismissProgressDialog();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                Log.d(">>>>>", result);
                dismissProgressDialog();
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        });
    }

    private String ChangeFormatDateString(String date, String formatDateFrom, String formatDateTo) {

        if (date != null && !date.equals("") && !date.equals(null)) {

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

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(HistoryPenjualanVolunterActivity.this,
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

    private void setupListHistoryPenjualan() {
        adapter = new HistoryPenjualanVolunteerAdapter(HistoryPenjualanVolunterActivity.this, historyPenjualan);
        rvView.setLayoutManager(new LinearLayoutManager(HistoryPenjualanVolunterActivity.this));
        rvView.setAdapter(adapter);
    }

    private void setupListScrollListenerHistoryPenjualan() {
        rvView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (! recyclerView.canScrollVertically(1)) {
                    start += count;
                    prepareDataHistoryPenjualan("");
                }
            }
        });
    }


//    private void showDialogMerchant(){
//        merchantModels.clear();
//        keyword_merchant="";
//        dgMerchant = new Dialog(HistoryPenjualanVolunterActivity.this);
//        dgMerchant.setContentView(R.layout.popup_merchant);
//        dgMerchant.setCancelable(false);
//        RelativeLayout rvCancel = dgMerchant.findViewById(R.id.rv_cancel);
//        rvMerchant = dgMerchant.findViewById(R.id.rv_merchant);
//        setupListMerchant();
//        setupListScrollListenerMerchant();
//        start_merchant =0;
//        count_merchant =20;
//        getAllMerchant();
//
//        final EditText edtSearchMerchant = dgMerchant.findViewById(R.id.edt_search_merchant);
//        edtSearchMerchant.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    in.hideSoftInputFromWindow(edtSearchMerchant.getWindowToken(), 0);
//                    keyword_merchant = edtSearchMerchant.getText().toString().trim();
//                    start_merchant =0;
//                    count_merchant =20;
//                    merchantModels.clear();
//                    getAllMerchant();
//                    merchantAdapter.notifyDataSetChanged();
//                    return true;
//                }
//                return false;
//            }
//        });
//        edtSearchMerchant.addTextChangedListener(new TextWatcher() {
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
//                if(edtSearchMerchant.getText().toString().length() == 0) {
//                    keyword_merchant="";
//                    start_merchant=0;
//                    count_merchant=20;
//                    getAllMerchant();
//                }
//            }
//        });
//
//        rvCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dgMerchant.dismiss();
//            }
//        });
//        dgMerchant.show();
//    }

//    private void setupListMerchant() {
//        merchantAdapter = new MerchantAdapter(HistoryPenjualanVolunterActivity.this, merchantModels,this);
//        rvMerchant.setLayoutManager(new LinearLayoutManager(HistoryPenjualanVolunterActivity.this));
//        rvMerchant.setAdapter(merchantAdapter);
//    }
//
//    private void setupListScrollListenerMerchant() {
//        rvMerchant.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//
//                if (! recyclerView.canScrollVertically(1)) {
//                    start_merchant += count_merchant;
//                    getAllMerchant();
//                }
//            }
//        });
//    }

//    private void getAllMerchant(){
//        JSONObject params = new JSONObject();
//        try {
////            params.put("keyword",keyword_merchant);
//            params.put("start",start_merchant);
//            params.put("count",count_merchant);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        new ApiVolley(HistoryPenjualanVolunterActivity.this, params, "POST", URL.urlAllMerchant, "", "", 0, new ApiVolley.VolleyCallback() {
//            @Override
//            public void onSuccess(String result) {
//                try {
//                    JSONObject object = new JSONObject(result);
//
//                    String status = object.getJSONObject("metadata").getString("status");
//                    if (status.equals("200")) {
//                        JSONArray arr = object.getJSONArray("response");
//                        for (int i = 0; i < arr.length(); i++) {
//                            JSONObject isi = arr.getJSONObject(i);
//                            merchantModels.add(new MerchantModel(
//                                    isi.getString("id"),
//                                    isi.getString("nama"),
//                                    isi.getString("alamat"),
//                                    isi.getString("email"),
//                                    isi.getString("deskripsi"),
//                                    isi.getString("kategori"),
//                                    isi.getString("latitude"),
//                                    isi.getString("longitude")
//                            ));
//                        }
//                        merchantAdapter.notifyDataSetChanged();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onError(String result) {
//                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
//            }
//        });
//    }

//    @Override
//    public void onRowMerchantAdapterClicked(String id_m, String nama) {
//        id_merchant = id_m;
//        tvMerchant.setText(nama);
//        dgMerchant.dismiss();
//    }


}
