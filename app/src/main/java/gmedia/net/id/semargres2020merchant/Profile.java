package gmedia.net.id.semargres2020merchant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.CompressBitmap;
import gmedia.net.id.semargres2020merchant.util.CustomKategoriModel;
import gmedia.net.id.semargres2020merchant.util.CustomMapView;
import gmedia.net.id.semargres2020merchant.util.EncodeBitmapToString;
import gmedia.net.id.semargres2020merchant.util.HideKeyboard;
import gmedia.net.id.semargres2020merchant.util.SessionManager;
import gmedia.net.id.semargres2020merchant.util.URL;

public class Profile extends AppCompatActivity implements LocationListener {
    private static final int PICK_IMAGE = 2;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    public boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;
    ImageView btnResetPosition;
    TextView edtLatitude, edtLongitude;
    SessionManager session;
    Spinner dropdown;
    EditText nama, alamat, telpon, edtHandphone, email, facebook, instagram, diskonDiberikan, diskonPengguna;
    TextView textLatitude, textLongitude;
    TextView isianJamBuka, isianJamTutup;
    String isianlatitude, isianlongtitude;
    ImageView foto,openCamera;
    Bitmap bitmap, photo;
    //    Boolean posisi = true;
    ArrayAdapter<CustomKategoriModel> adapter;
    float maxImageSize = 512;
    LinearLayout layout1, layout2, layout3, layout4, layout5, layout6, layout7, layout8;
    TextView textView1, textView2, textView3, textView4;
    private ImageView showCamera;
    private double latitude, longitude;
    private LocationManager locationManager;
    private Location location;
    //    private TextView tvTitle;
    private String TAG = "DetailCustomer";
    private String address0 = "";
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private SettingsClient mSettingsClient;
    private Boolean mRequestingLocationUpdates;
    private Location mCurrentLocation;
    private boolean refreshMode = false;
    private CustomMapView mvMap;
    private GoogleMap googleMap;
    private ArrayList<CustomKategoriModel> kategori;
    private ProgressDialog progressDialog;
    private Button simpan;
    private String bitmapString = "";
    private ProgressBar pbLoading;
    ScrollView svContainer;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        nama = findViewById(R.id.isianNama);
        alamat = findViewById(R.id.isianAlamat);
        isianJamBuka = findViewById(R.id.isianJamBuka);
        isianJamTutup = findViewById(R.id.isianJamTutup);
        telpon = findViewById(R.id.isianTelpon);
        edtHandphone = findViewById(R.id.edt_handphone);
        email = findViewById(R.id.isianEmail);
        facebook = findViewById(R.id.isianFacebook);
        instagram = findViewById(R.id.isianInstagram);
        textLatitude = findViewById(R.id.textLatitude);
        textLongitude = findViewById(R.id.textLongitude);
        foto = findViewById(R.id.showCameraProfile);
        showCamera = findViewById(R.id.showCameraProfile);
        openCamera = findViewById(R.id.openCameraProfile);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        layout1 = findViewById(R.id.layoutJamBuka);
        layout2 = findViewById(R.id.layoutJamTutup);
        layout3 = findViewById(R.id.layoutTelpon);
        layout4 = findViewById(R.id.layoutHandphone);
        layout5 = findViewById(R.id.layoutFacebook);
        layout6 = findViewById(R.id.layoutInstagram);
        layout7 = findViewById(R.id.layoutDiskonDiberikan);
        layout8 = findViewById(R.id.layoutDiskonPengguna);

        textView1 = findViewById(R.id.txt_kategori);
        textView2 = findViewById(R.id.txt_nama);
        textView3 = findViewById(R.id.txt_alamat);
        textView4 = findViewById(R.id.txt_email);

        diskonDiberikan = findViewById(R.id.isianDiskonDiberikan);
        diskonPengguna = findViewById(R.id.isianDiskonPengguna);

        svContainer = findViewById(R.id.sv_container);
        svContainer.setVerticalScrollBarEnabled(false);
        svContainer.setHorizontalScrollBarEnabled(false);

        session = new SessionManager(getApplicationContext());

        if (session.getFlag().equals("2")) {
            layout1.setVisibility(View.GONE);
            layout2.setVisibility(View.GONE);
            layout3.setVisibility(View.GONE);
            layout4.setVisibility(View.GONE);
            layout5.setVisibility(View.GONE);
            layout6.setVisibility(View.GONE);
            layout7.setVisibility(View.GONE);
            layout8.setVisibility(View.GONE);

            email.setEnabled(false);

            textView1.setText("Kategori Tenant");
            textView2.setText("Nama Tenant");
            textView3.setText("Alamat Tenant");
            textView4.setText("Username Tenant");
        }

        mvMap = findViewById(R.id.map);
        mvMap.onCreate(null);
        mvMap.onResume();
        try {
            MapsInitializer.initialize(Profile.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar));
        }

        LinearLayout utama = findViewById(R.id.layoutUtamaProfile);
        utama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyboard.hideSoftKeyboard(Profile.this);
            }
        });

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
//                final Dialog dialog = new Dialog(Profile.this);
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

        dropdown = findViewById(R.id.dropdownMenu);
        loadKategori();

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        showCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        ImageView btnJamBuka = findViewById(R.id.btnJamBuka);
        btnJamBuka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Profile.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String jam = String.valueOf(selectedHour);
                        String menit = String.valueOf(selectedMinute);
                        jam = jam.length() < 2 ? "0" + jam : jam;
                        menit = menit.length() < 2 ? "0" + menit : menit;
                        String jamBuka = jam + ":" + menit;
                        isianJamBuka.setText(jamBuka);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        ImageView btnJamTutup = findViewById(R.id.btnJamTutup);
        btnJamTutup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Profile.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String jam = String.valueOf(selectedHour);
                        String menit = String.valueOf(selectedMinute);
                        jam = jam.length() < 2 ? "0" + jam : jam;
                        menit = menit.length() < 2 ? "0" + menit : menit;
                        String jamTutup = jam + ":" + menit;
                        isianJamTutup.setText(jamTutup);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        mRequestingLocationUpdates = false;

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

        btnResetPosition = findViewById(R.id.refresh);
        edtLatitude = findViewById(R.id.textLatitude);
        edtLongitude = findViewById(R.id.textLongitude);
        initLocation();
        simpan = findViewById(R.id.btnProfile);

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (session.getFlag().equals("2")) {
//                    Toast.makeText(Profile.this, "Hanya pemilik yang dapat merubah profile merchant", Toast.LENGTH_LONG).show();
//                } else {
                if(dropdown.getSelectedItem() == null){
                    Toast.makeText(Profile.this,
                            "Kategori merchant tidak termuat", Toast.LENGTH_SHORT).show();
                    dropdown.requestFocus();

                    loadKategori();
                    return;
                }

                if (nama.getText().toString().equals("")) {
                    nama.setError("Silahkan mengisi nama");
                    nama.requestFocus();
                    return;
                }

                if (email.getText().toString().equals("")) {
                    email.setError("Silahkan mengisi email");
                    email.requestFocus();
                    return;
                }

                if (alamat.getText().toString().equals("")) {
                    alamat.setError("Silahkan mengisi alamat");
                    alamat.requestFocus();
                    return;
                }


                if (textLatitude.getText().toString().length() == 0) {
                    refreshMode = true;
                    updateAllLocation();
                    Toast.makeText(Profile.this, "Pastikan Lokasi diketahui", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!bitmapString.equals("")) {

                    if (session.getFlag().equals("2")) {
                        AlertDialog alertDialog = new AlertDialog.Builder(Profile.this)
                                .setTitle("Konfirmasi")
                                .setMessage("Apakah Anda yakin ingin menyimpan data?")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        updateTenant();
                                    }
                                })
                                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                        alertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.color_grey_new));
                    } else {

                        if (telpon.getText().toString().equals("")) {
                            telpon.setError("Silahkan mengisi no telepon");
                            telpon.requestFocus();
                            return;
                        }

                        if (edtHandphone.getText().toString().equals("")) {
                            edtHandphone.setError("Silahkan mengisi no handphone");
                            edtHandphone.requestFocus();
                            return;
                        }

                        if (diskonDiberikan.getText().toString().equals("")) {
                            diskonDiberikan.setError("Silahkan mengisi diskon yang diberikan");
                            diskonDiberikan.requestFocus();
                            return;
                        }

                        AlertDialog alertDialog = new AlertDialog.Builder(Profile.this)
                                .setTitle("Konfirmasi")
                                .setMessage("Apakah Anda yakin ingin menyimpan data?")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        updateMerchant();
                                    }
                                })
                                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                        alertDialog.getButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.color_grey_new));
                    }

                } else {
                    Toast.makeText(Profile.this, "Harap pilih gambar terlebih dahulu", Toast.LENGTH_LONG).show();
                }

//                }

                /*Intent i = new Intent(Profile.this,Profile.class);
                startActivity(i);
                finish();*/
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

    private void loadKategori(){
        new ApiVolley(Profile.this, new JSONObject(), "GET",
                URL.urlKategori, "", "", 0, new ApiVolley.VolleyCallback() {
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
                        adapter = new ArrayAdapter<>(Profile.this, R.layout.item_simple_item, kategori);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        dropdown.setAdapter(adapter);
//                        kategori.get()
                        if (session.getFlag().equals("2")) {
                            prepareDataProfileTenant();
                        } else {
                            prepareDataProfileMerchant();
                        }

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

    private void updateMerchant() {

        simpan.setEnabled(false);
        showProgressDialog();
        CustomKategoriModel customKategoriModel = (CustomKategoriModel) dropdown.getSelectedItem();
        String kategoriID = customKategoriModel.getId();
        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("kategori", kategoriID);
            jBody.put("foto", bitmapString);
            jBody.put("nama", nama.getText().toString());
            jBody.put("alamat", alamat.getText().toString());
            jBody.put("jam_buka", isianJamBuka.getText());
            jBody.put("jam_tutup", isianJamTutup.getText());
            jBody.put("notelp", telpon.getText().toString());
            jBody.put("handphone", edtHandphone.getText().toString());
            jBody.put("link_fb", facebook.getText().toString());
            jBody.put("link_ig", instagram.getText().toString());
            jBody.put("latitude", textLatitude.getText().toString());
            jBody.put("longitude", textLongitude.getText().toString());
            jBody.put("diskon_default", diskonDiberikan.getText().toString());
            jBody.put("diskon_user_app", diskonPengguna.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(this, jBody, "POST", URL.urlEditProfile, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                simpan.setEnabled(true);
                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
//                    Log.d("hasil update merchant", object.toString());
                    final String status = object.getJSONObject("response").getString("status");
                    String message = object.getJSONObject("response").getString("message");
                    if (status.equals("1")) {

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

//                        if (session.getFlag().equals("2")) {
//                            prepareDataProfileTenant();
//                        } else {

                        prepareDataProfileMerchant();

//                        }

                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onError(String result) {
                simpan.setEnabled(true);
                dismissProgressDialog();
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateTenant() {

        simpan.setEnabled(false);
        showProgressDialog();
        CustomKategoriModel customKategoriModel = (CustomKategoriModel) dropdown.getSelectedItem();
        String kategoriID = customKategoriModel.getId();
        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("id_kategori", kategoriID);
//            jBody.put("foto", bitmapString);
            jBody.put("gambar", bitmapString);
            jBody.put("nama_tenant", nama.getText().toString());
            jBody.put("alamat", alamat.getText().toString());
//            jBody.put("jam_buka", isianJamBuka.getText());
//            jBody.put("jam_tutup", isianJamTutup.getText());
//            jBody.put("notelp", telpon.getText().toString());
//            jBody.put("handphone", edtHandphone.getText().toString());
//            jBody.put("link_fb", facebook.getText().toString());
//            jBody.put("link_ig", instagram.getText().toString());
            jBody.put("latitude", textLatitude.getText().toString());
            jBody.put("longitude", textLongitude.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(this, jBody, "POST", URL.urlUpdateProfileTenant, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                simpan.setEnabled(true);
                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
//                    Log.d("hasil update tenant", object.toString());
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                        if(session.getFlag().equals("2")){
                        prepareDataProfileTenant();
//                        }
//                        else{
//                            prepareDataProfileMerchant();
//                        }
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onError(String result) {
                simpan.setEnabled(true);
                dismissProgressDialog();
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        });
    }

    private Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                             boolean filter) {
        float ratio = Math.min(
                maxImageSize / realImage.getWidth(),
                maxImageSize / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        return Bitmap.createScaledBitmap(realImage, width,
                height, filter);
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(Profile.this,
                R.style.AppTheme_Custom_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void prepareDataProfileMerchant() {
        new ApiVolley(Profile.this, new JSONObject(), "GET", URL.urlProfile, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    Log.d("hasil profile merchant", object.toString());
                    final String status = object.getJSONObject("metadata").getString("status");
                    if (status.equals("200")) {
                        String kategoriName = object.getJSONObject("response").getString("kategori");
                        isianlatitude = object.getJSONObject("response").getString("latitude");
                        isianlongtitude = object.getJSONObject("response").getString("longitude");
                        int position = 0;
                        int x = 0;
                        for (CustomKategoriModel item : kategori) {
                            if (item.getNama().equals(kategoriName)) {
                                position = x;
                                break;
                            }
                            x++;
                        }
                        dropdown.setSelection(position, true);
                        nama.setText(object.getJSONObject("response").getString("nama"));
                        alamat.setText(object.getJSONObject("response").getString("alamat"));
                        isianJamBuka.setText(object.getJSONObject("response").getString("jam_buka"));
                        isianJamTutup.setText(object.getJSONObject("response").getString("jam_tutup"));
                        telpon.setText(object.getJSONObject("response").getString("notelp"));
                        edtHandphone.setText(object.getJSONObject("response").getString("hp"));
                        email.setText(object.getJSONObject("response").getString("email"));
                        facebook.setText(object.getJSONObject("response").getString("link_fb"));
                        instagram.setText(object.getJSONObject("response").getString("link_ig"));
                        diskonDiberikan.setText(object.getJSONObject("response").getString("diskon_default"));
                        diskonPengguna.setText(object.getJSONObject("response").getString("diskon_user_app"));

                        pbLoading.setVisibility(View.VISIBLE);
                        Picasso.with(Profile.this).load(object.getJSONObject("response").getString("foto"))
                                .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(showCamera, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        BitmapDrawable drawable = (BitmapDrawable) showCamera.getDrawable();
                                        bitmap = drawable.getBitmap();
                                        openCamera.setVisibility(View.GONE);
                                        bitmap = scaleDown(bitmap, 460, true);
                                        bitmapString = bitmap != null ? EncodeBitmapToString.convert(bitmap) : "";
                                        pbLoading.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {

                                        pbLoading.setVisibility(View.GONE);
                                    }
                                });

                        textLatitude.setText(isianlatitude);
                        textLongitude.setText(isianlongtitude);
                        if (isianlongtitude.length() > 0) {
                            try {
                                latitude = Double.parseDouble(isianlatitude);
                                longitude = Double.parseDouble(isianlongtitude);
                                location = new Location("set");
                                location.setLatitude(latitude);
                                location.setLongitude(longitude);
                                refreshMode = true;
                                onLocationChanged(location);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
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

    private void prepareDataProfileTenant() {
        new ApiVolley(Profile.this, new JSONObject(), "GET", URL.urlProfileTenant, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    Log.d("hasil profile tenant", object.toString());
                    final String status = object.getJSONObject("metadata").getString("status");
                    if (status.equals("200")) {

                        JSONObject jsonObject = object.getJSONObject("response").getJSONObject("profil");

                        String kategoriName = jsonObject.getString("nama_kategori");
                        isianlatitude = jsonObject.getString("latitude");
                        isianlongtitude = jsonObject.getString("longitude");
                        int position = 0;
                        int x = 0;
                        for (CustomKategoriModel item : kategori) {
                            if (item.getNama().equals(kategoriName)) {
                                position = x;
                                break;
                            }
                            x++;
                        }
                        dropdown.setSelection(position, true);
                        nama.setText(jsonObject.getString("nama_tenant"));
                        alamat.setText(jsonObject.getString("alamat"));
//                        isianJamBuka.setText(object.getJSONObject("response").getString("jam_buka"));
//                        isianJamTutup.setText(object.getJSONObject("response").getString("jam_tutup"));
//                        telpon.setText(object.getJSONObject("response").getString("notelp"));
//                        edtHandphone.setText(object.getJSONObject("response").getString("hp"));
                        email.setText(jsonObject.getString("username"));
//                        facebook.setText(object.getJSONObject("response").getString("link_fb"));
//                        instagram.setText(object.getJSONObject("response").getString("link_ig"));

                        pbLoading.setVisibility(View.VISIBLE);
                        Picasso.with(Profile.this).load(jsonObject.getString("gambar"))
                                .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(showCamera, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        BitmapDrawable drawable = (BitmapDrawable) showCamera.getDrawable();
                                        bitmap = drawable.getBitmap();
                                        openCamera.setVisibility(View.GONE);
                                        bitmap = scaleDown(bitmap, 460, true);
                                        bitmapString = bitmap != null ? EncodeBitmapToString.convert(bitmap) : "";
                                        pbLoading.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {

                                        pbLoading.setVisibility(View.GONE);
                                    }
                                });

                        textLatitude.setText(isianlatitude);
                        textLongitude.setText(isianlongtitude);
                        if (isianlongtitude.length() > 0) {
                            try {
                                latitude = Double.parseDouble(isianlatitude);
                                longitude = Double.parseDouble(isianlongtitude);
                                location = new Location("set");
                                location.setLatitude(latitude);
                                location.setLongitude(longitude);
                                refreshMode = true;
                                onLocationChanged(location);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
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

    private void initLocation() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setCriteria();
        latitude = 0;
        longitude = 0;
        location = new Location("set");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        refreshMode = true;
        Bundle bundle = getIntent().getExtras();
        updateAllLocation();

        btnResetPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                refreshMode = true;
                //location = getLocation();
                updateAllLocation();
            }
        });
    }

    private void updateAllLocation() {
        mRequestingLocationUpdates = true;
        startLocationUpdates();
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                mCurrentLocation = locationResult.getLastLocation();
                //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                onLocationChanged(mCurrentLocation);
            }
        };
    }

    private void createLocationRequest() {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d(TAG, "stopLocationUpdates: updates never requested, no-op.");
            return;
        }

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //noinspection MissingPermission
                        if (ActivityCompat.checkSelfPermission(Profile.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Profile.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        mFusedLocationClient.getLastLocation()
                                .addOnSuccessListener(Profile.this, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location clocation) {

                                        if (clocation != null) {

                                            location = clocation;
                                            refreshMode = true;
                                            onLocationChanged(location);
                                        } else {
                                            location = getLocation();
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(Profile.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(Profile.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                                //refreshMode = false;
                        }

                        //get Location
                        location = getLocation();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {

            if (resultCode == Activity.RESULT_CANCELED) {

                mRequestingLocationUpdates = false;
            } else if (resultCode == Activity.RESULT_OK) {

                startLocationUpdates();
            }

        } else if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {
                openCamera.setVisibility(View.GONE);
                photo = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                showCamera.setImageBitmap(Bitmap.createScaledBitmap(photo, 720, 490, true));
//                showCamera.setImageBitmap(CompressBitmap.scaleDown(photo, maxImageSize, true));
            } catch (IOException e) {
                e.printStackTrace();
            }

            BitmapDrawable drawable = (BitmapDrawable) showCamera.getDrawable();
            bitmap = drawable.getBitmap();
            bitmap = scaleDown(bitmap, 640, true);
            bitmapString = bitmap != null ? EncodeBitmapToString.convert(bitmap) : "";
        }
    }

    public Location getLocation() {

        try {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            Log.v("isGPSEnabled", "=" + isGPSEnabled);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Toast.makeText(Profile.this, "Cannot identify the location.\nPlease turn on GPS or turn on your data.",
                        Toast.LENGTH_LONG).show();

            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    location = null;

                    // Granted the permission first
                    if (ActivityCompat.checkSelfPermission(Profile.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Profile.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        int REQUEST_PERMISSION_COARSE_LOCATION = 2;
                        if (ActivityCompat.shouldShowRequestPermissionRationale(Profile.this,
                                Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_COARSE_LOCATION);
                        }

                        int REQUEST_PERMISSION_FINE_LOCATION = 3;
                        if (ActivityCompat.shouldShowRequestPermissionRationale(Profile.this,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showExplanation("Permission Needed", "Rationale", Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_FINE_LOCATION);
                        } else {
                            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_PERMISSION_FINE_LOCATION);
                        }
                        return null;
                    }

                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");

                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            //onLocationChanged(location);
                        }
                    }
                }

                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {

                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("GPS Enabled", "GPS Enabled");

                    if (locationManager != null) {
                        Location bufferLocation = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (bufferLocation != null) {

                            location = bufferLocation;
                        }
                    }
                }  //Toast.makeText(context, "Turn on your GPS for better accuracy", Toast.LENGTH_SHORT).show();


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (location != null && location.getLongitude() != 0 && location.getLatitude() != 0) {
            onLocationChanged(location);
        }

        return location;
    }

    public void setCriteria() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        String provider = locationManager.getBestProvider(criteria, true);
    }

    private void showExplanation(String title,
                                 String message,
                                 final String permission,
                                 final int permissionRequestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        requestPermission(permission, permissionRequestCode);
                    }
                });
        builder.create().show();
    }

    private void requestPermission(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(Profile.this,
                new String[]{permissionName}, permissionRequestCode);
    }


    private void callGoogleMap(String latitude, String longitude) {

        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(Profile.this, "Cannot find google map, Please install latest google map.",
                    Toast.LENGTH_LONG).show();

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps"));
            startActivity(browserIntent);
        }
    }

    private void setPointMap() {

        mvMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {

                googleMap = mMap;
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions()
                        .anchor(0.0f, 1.0f)
                        .draggable(true)
                        .position(new LatLng(latitude, longitude)));

                if (ActivityCompat.checkSelfPermission(Profile.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Profile.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(Profile.this, "Please allow location access from y   our app permission", Toast.LENGTH_SHORT).show();
                    return;
                }

                //googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                MapsInitializer.initialize(Profile.this);
                LatLng position = new LatLng(latitude, longitude);
                // For zooming automatically to the location of the marker
                CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(15).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                updateKeterangan(position);

                googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {

                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {

                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {

                        LatLng position = marker.getPosition();
                        updateKeterangan(position);
                        Log.d(TAG, "onMarkerDragEnd: " + position.latitude + " " + position.longitude);
                    }
                });

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions()
                                .anchor(0.0f, 1.0f)
                                .draggable(true)
                                .position(latLng));
                        updateKeterangan(latLng);
                        Log.d(TAG, "onMarkerDragEnd: " + latLng.latitude + " " + latLng.longitude);
                    }
                });
            }
        });
    }

    private String doubleToStringFull(Double number) {
        return String.format("%s", number).replace(",", ".");
    }

    private void updateKeterangan(LatLng position) {

        latitude = position.latitude;
        longitude = position.longitude;

        //get address
        new Thread(new Runnable() {
            public void run() {
                address0 = getAddress(location);
            }
        }).start();
        edtLatitude.setText(doubleToStringFull(latitude));
        edtLongitude.setText(doubleToStringFull(longitude));


//        edtState.setText(address0);
    }

    private String getAddress(Location location) {
        List<Address> addresses;
        try {
            addresses = new Geocoder(this, Locale.getDefault()).getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            return findAddress(addresses);
        } catch (Exception e) {

            return "";

        }
    }

    private String findAddress(List<Address> addresses) {
        String address = "";
        if (addresses != null) {
            for (int i = 0; i < addresses.size(); i++) {

                Address addre = addresses.get(i);
                String street = addre.getAddressLine(0);
                if (null == street)
                    street = "";

                String city = addre.getLocality();
                if (city == null) city = "";

                String state = addre.getAdminArea();
                if (state == null) state = "";

                String country = addre.getCountryName();
                if (country == null) country = "";

                address = street + ", " + city + ", " + state + ", " + country;
            }
            return address;
        }
        return address;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (refreshMode) {
            refreshMode = false;
            this.location = location;
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
            setPointMap();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
