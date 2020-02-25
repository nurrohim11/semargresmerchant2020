package gmedia.net.id.semargres2020merchant.akun;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.kategoriKupon.SettingKategoriKuponModel;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.CustomKategoriModel;
import gmedia.net.id.semargres2020merchant.util.GoogleLocationManager;
import gmedia.net.id.semargres2020merchant.util.HideKeyboard;
import gmedia.net.id.semargres2020merchant.util.ScrollableMapView;
import gmedia.net.id.semargres2020merchant.util.URL;
import gmedia.net.id.semargres2020merchant.util.CompressBitmap;

public class TambahAkunActivity extends AppCompatActivity implements OnMapReadyCallback {

    ArrayAdapter<CustomKategoriModel> adapter;
    Boolean visible = true;
    TextView textLatitude, textLongitude;
    Bitmap bitmap;
    private ArrayList<SettingKategoriKuponModel> array_kategori_tambah_akun;
    private Spinner sp_kategori_tambah_akun, sp_tambah_akun;
    private EditText edtNama, edtUsername, edtPassword, edtRePassword, edtAlamat;
    private Button saveAkun;
    private String kategori_tambah_akun = "0";
    private String kategori_akun = "0";
    private ArrayList<CustomKategoriModel> kategori;
    private ImageView showCamera;
    private ProgressBar pbLoading;
    private ProgressDialog progressDialog;

    //lokasi
    private GoogleMap mMap;
    private LatLng lokasi;
    private GoogleLocationManager manager;
    private Marker marker;
    private boolean loading_location = false;
    private ProgressBar pb_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_akun);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar));
        }

        RelativeLayout utama = findViewById(R.id.layout_tambahAkun);
        utama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyboard.hideSoftKeyboard(TambahAkunActivity.this);
            }
        });

//        RelativeLayout back = findViewById(R.id.back);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        sp_kategori_tambah_akun = findViewById(R.id.sp_tipe_kupon);
        sp_tambah_akun = findViewById(R.id.sp_kategori_akun);
        edtNama = findViewById(R.id.edt_namaKategori);
        edtUsername = findViewById(R.id.edt_usernameAkun);
        edtPassword = findViewById(R.id.edt_passwordAkun);
        edtRePassword = findViewById(R.id.edt_repasswordAkun);
        saveAkun = findViewById(R.id.btnSaveAkun);
        pb_map = findViewById(R.id.pb_map);
        edtAlamat = findViewById(R.id.edt_alamat);

        textLatitude = findViewById(R.id.textLatitude);
        textLongitude = findViewById(R.id.textLongitude);

        showCamera = findViewById(R.id.showCameraProfile);

        pbLoading = findViewById(R.id.pb_loading);

        final ImageView btnPass = findViewById(R.id.visiblePassAkun);
        btnPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (visible) {
                    btnPass.setImageDrawable(getResources().getDrawable(R.drawable.visible_red));
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    visible = false;
                } else {
                    btnPass.setImageDrawable(getResources().getDrawable(R.drawable.invisible_red));
                    edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    visible = true;
                }
            }
        });

        final ImageView btnRePass = findViewById(R.id.visibleRePassAkun);
        btnRePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (visible) {
                    btnRePass.setImageDrawable(getResources().getDrawable(R.drawable.visible_red));
                    edtRePassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    visible = false;
                } else {
                    btnRePass.setImageDrawable(getResources().getDrawable(R.drawable.invisible_red));
                    edtRePassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    edtRePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    visible = true;
                }
            }
        });

//        getKategoriKupon();
        getKategori();

        //init lokasi
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if(mapFragment != null){
            mapFragment.getMapAsync(this);
        }

        //Update lokasi
        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMap != null && !loading_location){
                    loading_location = true;
                    pb_map.setVisibility(View.VISIBLE);

                    manager = new GoogleLocationManager(TambahAkunActivity.this, new GoogleLocationManager.LocationUpdateListener() {
                        @Override
                        public void onChange(Location location) {
                            if(marker != null){
                                mMap.clear();
                            }

                            lokasi = new LatLng(location.getLatitude(), location.getLongitude());

                            //Update lokasi outlet & TextView
                            textLatitude.setText(String.valueOf(lokasi.latitude));
                            textLongitude.setText(String.valueOf(lokasi.longitude));

                            //Update marker
                            marker = mMap.addMarker(new MarkerOptions().position(lokasi).title("Lokasi Outlet").draggable(true));
                            marker.setPosition(lokasi);
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 15.0f));
                            manager.stopLocationUpdates();

                            loading_location = false;
                            pb_map.setVisibility(View.INVISIBLE);
                        }
                    });
                    manager.startLocationUpdates();
                }
                else{
                    Toast.makeText(TambahAkunActivity.this, "Map tidak dapat menampilkan lokasi", Toast.LENGTH_SHORT).show();
                }
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

    private void getKategori() {
        new ApiVolley(TambahAkunActivity.this, new JSONObject(), "GET", URL.urlKategori, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                kategori = new ArrayList<>();
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("message");
                    if (status.equals("Success!")) {
                        JSONArray detail = object.getJSONArray("response");
                        for (int i = 0; i < detail.length(); i++) {
                            JSONObject isi = detail.getJSONObject(i);
                            kategori.add(new CustomKategoriModel(
                                    isi.getString("id_k"),
                                    isi.getString("nama")
                            ));
                        }
//                        String isiSpinner = "Restoran";
                        adapter = new ArrayAdapter<>(TambahAkunActivity.this, R.layout.item_simple_item, kategori);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_tambah_akun.setAdapter(adapter);
                        sp_tambah_akun.setSelection(0, true);
//                        kategori.get()
//                        prepareDataProfile();
                        getKategoriKupon();
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
        new ApiVolley(this, new JSONObject(), "POST", URL.urlKategoriKupon, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                array_kategori_tambah_akun = new ArrayList<>();

                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
//                        Toast.makeText(getApplicationContext(),status,Toast.LENGTH_LONG).show();
                        JSONArray array = object.getJSONObject("response").getJSONArray("kategori");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject isi = array.getJSONObject(i);
                            array_kategori_tambah_akun.add(new SettingKategoriKuponModel(
                                    isi.getString("id"),
                                    isi.getString("nama"),
                                    isi.getString("nominal")
                            ));
                        }

//                        showTambahAkun(array_kategori_tambah_akun);

                        ArrayAdapter adapter = new ArrayAdapter(TambahAkunActivity.this, R.layout.layout_simple_list, array_kategori_tambah_akun);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_kategori_tambah_akun.setAdapter(adapter);
                        sp_kategori_tambah_akun.setSelection(0, true);

                        saveAkun.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (edtNama.getText().toString().equals("")) {
                                    edtNama.setError("Nama harap diisi");
                                    edtNama.requestFocus();
                                    return;
                                }

                                if (edtAlamat.getText().toString().equals("")) {
                                    edtAlamat.setError("Alamat harap diisi");
                                    edtAlamat.requestFocus();
                                    return;
                                }

                                if (edtUsername.getText().toString().equals("")) {
                                    edtUsername.setError("Username harap diisi");
                                    edtUsername.requestFocus();
                                    return;
                                }

                                if (edtPassword.getText().toString().equals("")) {
                                    edtPassword.setError("Password harap diisi");
                                    edtPassword.requestFocus();
                                    return;
                                }

                                if (edtRePassword.getText().toString().equals("")) {
                                    edtRePassword.setError("Password ulang harap diisi");
                                    edtRePassword.requestFocus();
                                    return;
                                }

                                if (!edtRePassword.getText().toString().equals(edtPassword.getText().toString())) {
                                    edtRePassword.setError("Password tidak sama, cek kembali");
                                    edtRePassword.requestFocus();
                                    return;
                                }



                                if(lokasi == null){
                                    Toast.makeText(TambahAkunActivity.this, "Lokasi tidak ditemukan", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                kategori_tambah_akun = "0";
                                SettingKategoriKuponModel kategori = (SettingKategoriKuponModel) sp_kategori_tambah_akun.getSelectedItem();
                                kategori_tambah_akun = kategori.getId();

                                kategori_akun = "0";
                                CustomKategoriModel customKategoriModel = (CustomKategoriModel) sp_tambah_akun.getSelectedItem();
                                kategori_akun = customKategoriModel.getId();

                                AlertDialog alertDialog = new AlertDialog.Builder(TambahAkunActivity.this)
                                        .setTitle("Konfirmasi")
                                        .setMessage("Apakah Anda yakin ingin menyimpan data?")
                                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                saveNewAccount();
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

    private void saveNewAccount() {

        showProgressDialog();
        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("id_kategori", String.valueOf(kategori_akun));
            jBody.put("nama_tenant", edtNama.getText());
            jBody.put("username", edtUsername.getText());
            jBody.put("password", edtPassword.getText());
            jBody.put("re_password", edtRePassword.getText());
            jBody.put("id_kategori_kupon", String.valueOf(kategori_tambah_akun));
            jBody.put("latitude", lokasi.latitude);
            jBody.put("longitude", lokasi.longitude);
            jBody.put("alamat", edtAlamat.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        ApiVolley request = new ApiVolley(this, jBody, "POST", URL.urlTambahAkun, "", "", 0, new ApiVolley.VolleyCallback() {

        new ApiVolley(this, jBody, "POST", URL.urlStoreUserAkun, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
//                    Log.d("hasil save", object.toString());
                    String pesan = object.getJSONObject("metadata").getString("message");
                    String status = object.getJSONObject("metadata").getString("status");
                    if (status.equals("200")) {
//                        Toast.makeText(getApplicationContext(), pesan, Toast.LENGTH_LONG).show();

//                        startActivity(new Intent(TambahAkunActivity.this, ListAkunActivity.class));
                        Intent intent=new Intent();
                        setResult(101,intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), pesan, Toast.LENGTH_LONG).show();
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

    private void prepareDataLatLongi() {
        new ApiVolley(TambahAkunActivity.this, new JSONObject(), "GET", URL.urlProfile, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
//                    Log.d("hasil profile merchant", object.toString());
                    final String status = object.getJSONObject("metadata").getString("message");
                    if (status.equals("Success!")) {
                        lokasi = new LatLng(object.getJSONObject("response").getDouble("latitude"),
                                object.getJSONObject("response").getDouble("longitude"));

                        textLatitude.setText(String.valueOf(lokasi.latitude));
                        textLongitude.setText(String.valueOf(lokasi.longitude));

                        marker = mMap.addMarker(new MarkerOptions().position(lokasi).title("Lokasi Customer").draggable(true));
                        marker.setPosition(lokasi);
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 15.0f));

                        pbLoading.setVisibility(View.VISIBLE);
                        Picasso.with(TambahAkunActivity.this).load(object.getJSONObject("response").getString("foto"))
                                .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(showCamera, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        BitmapDrawable drawable = (BitmapDrawable) showCamera.getDrawable();
                                        bitmap = drawable.getBitmap();
                                        bitmap = CompressBitmap.scaleDown(bitmap, 460, true);
//                                        bitmapString = bitmap != null ? EncodeBitmapToString.convert(bitmap) : "";
                                        pbLoading.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {

                                        pbLoading.setVisibility(View.GONE);
                                    }
                                });
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

    //============ LOKASI =============================================================

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("custom_log", "MASUK");
        mMap = googleMap;
        final RelativeLayout layout_map = findViewById(R.id.layoutMap);
        final ScrollableMapView mapView = (ScrollableMapView) getSupportFragmentManager().findFragmentById(R.id.map);
        if(mapView != null){
            mapView.setListener(new ScrollableMapView.OnTouchListener() {
                @Override
                public void onTouch() {
                    layout_map.requestDisallowInterceptTouchEvent(true);
                }
            });
        }

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                lokasi = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);

                textLatitude.setText(String.valueOf(lokasi.latitude));
                textLongitude.setText(String.valueOf(lokasi.longitude));
            }
        });

        /*manager = new GoogleLocationManager(this, new GoogleLocationManager.LocationUpdateListener() {
            @Override
            public void onChange(Location location) {
                if(marker == null){
                    Log.d("custom_log", "latitude  " + location.getLatitude());
                    Log.d("custom_log", "longitude " + location.getLongitude());
                    lokasi = new LatLng(location.getLatitude(), location.getLongitude());

                    marker = mMap.addMarker(new MarkerOptions().position(lokasi).title("Lokasi Customer").draggable(true));
                    marker.setPosition(lokasi);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasi, 15.0f));
                    manager.stopLocationUpdates();
                }
            }
        });
        manager.startLocationUpdates();*/

        prepareDataLatLongi();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == GoogleLocationManager.PERMISSION_LOCATION) {
            if (manager != null) {
                manager.startLocationUpdates();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(TambahAkunActivity.this,
                R.style.AppTheme_Custom_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}

