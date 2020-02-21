package gmedia.net.id.semargres2020merchant.promo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.CustomKategoriModel;
import gmedia.net.id.semargres2020merchant.util.EncodeBitmapToString;
import gmedia.net.id.semargres2020merchant.util.HideKeyboard;
import gmedia.net.id.semargres2020merchant.util.URL;

public class SettingPromoActivity extends AppCompatActivity {
//    int bitWidth,bitHeight;
//    private float curScale = 1F;
//    private float curRotate = 0F;
//    private RelativeLayout home;
//    private RelativeLayout logout;
//    private ImageView gbrHome;
//    private ImageView gbrLogout;
//    private TextView txtGbrHome;
//    private TextView txtGbrLogout;

    private static final int PICK_IMAGE = 1;
    public static ImageView openCamera;
    EditText judul, link, keterangan;
    Bitmap bitmap;
    Bitmap photo;
    private ImageView showCamera;
    private ProgressDialog progressDialog;
    private Button proses;
    private String bitmapString = "";
    private Spinner spKategori;
    private ArrayList<CustomKategoriModel> kategoriList;
    private String selectedKategori = "";
    private ArrayAdapter<CustomKategoriModel> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_promo);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar));
        }

        judul = findViewById(R.id.titleSettingPromo);
        link = findViewById(R.id.urlSettingPromo);
        keterangan = findViewById(R.id.keteranganSettingPromo);

        RelativeLayout utama = findViewById(R.id.layoutUtamaSettingPromo);
        utama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyboard.hideSoftKeyboard(SettingPromoActivity.this);
            }
        });

        RelativeLayout back = findViewById(R.id.backSettingPromo);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        spKategori = findViewById(R.id.sp_kategori);

        showCamera = findViewById(R.id.showCameraSettingPromo);

        openCamera = findViewById(R.id.openCameraSettingPromo);
        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        proses = findViewById(R.id.btnSettingPromo);
        proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!judul.getText().toString().equals("") && !keterangan.getText().toString().equals("") && photo != null) {

                    if (!bitmapString.equals("")) {

                        if (judul.getText().toString().equals("")) {
                            judul.setError("Silahkan mengisi judul promo");
                            judul.requestFocus();
                            return;
                        }

                        if (link.getText().toString().equals("")) {
                            link.setError("Silahkan mengisi URL promo");
                            link.requestFocus();
                            return;
                        }

                        if (keterangan.getText().toString().equals("")) {
                            keterangan.setError("Silahkan mengisi keterangan promo");
                            keterangan.requestFocus();
                            return;
                        }

                        AlertDialog alertDialog = new AlertDialog.Builder(SettingPromoActivity.this)
                                .setTitle("Konfirmasi")
                                .setMessage("Apakah Anda yakin ingin menyimpan data?")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        prepareCreateDataSettingPromo();
                                    }
                                })
                                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.color_grey_new));

                    } else {
                        Toast.makeText(SettingPromoActivity.this, "Harap pilih gambar terlebih dahulu", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Silahkan Lengkapi Data Anda", Toast.LENGTH_LONG).show();
                }
            }
        });

        getDataMerchant();


//        home = (RelativeLayout) findViewById(R.id.btnHomeCreatePromo);
//        logout = (RelativeLayout) findViewById(R.id.btnLogout);
//
//        gbrHome = (ImageView) findViewById(R.id.gambarHome);
//        gbrLogout = (ImageView) findViewById(R.id.gambarLogout);
//        txtGbrHome = (TextView) findViewById(R.id.textGambarHome);
//        txtGbrLogout = (TextView) findViewById(R.id.textGambarLogout);

//        home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!HomeActivity.isHome){
//                    Intent i = new Intent(SettingPromoActivity.this,HomeActivity.class);
//                    startActivity(i);
//                    finish();
//                }
//            }
//        });

//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                gbrHome.setImageResource(R.drawable.home_grey);
//                txtGbrHome.setTextColor(Color.parseColor("#FF5F5D5D"));
//                gbrLogout.setImageResource(R.drawable.logout_red);
//                txtGbrLogout.setTextColor(Color.parseColor("#e40112"));
//                final Dialog dialog = new Dialog(SettingPromoActivity.this);
//                dialog.setContentView(R.layout.popup_logout);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                RelativeLayout ya = dialog.findViewById(R.id.logoutYa);
//                RelativeLayout tidak = dialog.findViewById(R.id.logoutTidak);
//                ya.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent i = new Intent(SettingPromoActivity.this, LoginActivity.class);
//                        startActivity(i);
//                        finish();
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

    }

    private void getDataMerchant() {

        new ApiVolley(SettingPromoActivity.this, new JSONObject(), "GET", URL.urlProfile, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                selectedKategori = "0";
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    if (status.equals("200")) {
                        selectedKategori = object.getJSONObject("response").getString("id_k");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                getDataKategori();
            }

            @Override
            public void onError(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                selectedKategori = "0";
                getDataKategori();
            }
        });
    }

    private void getDataKategori() {

        new ApiVolley(SettingPromoActivity.this, new JSONObject(), "GET", URL.urlKategori, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                kategoriList = new ArrayList<>();
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    if (status.equals("200")) {

                        JSONArray detail = object.getJSONArray("response");
                        int position = 0;
                        for (int i = 0; i < detail.length(); i++) {
                            JSONObject isi = detail.getJSONObject(i);
                            kategoriList.add(new CustomKategoriModel(
                                    isi.getString("id_k"),
                                    isi.getString("nama")
                            ));

                            if (isi.getString("id_k").equals(selectedKategori)) {
                                position = i;
                            }
                        }

                        adapter = new ArrayAdapter<>(SettingPromoActivity.this, R.layout.item_simple_item_promo, kategoriList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spKategori.setAdapter(adapter);

                        try {
                            spKategori.setSelection(position);
                        } catch (Exception e) {
                            e.printStackTrace();
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

    private void prepareCreateDataSettingPromo() {

        proses.setEnabled(false);
        showProgressDialog();

        String idK = "";
        try {
            CustomKategoriModel item = (CustomKategoriModel) spKategori.getSelectedItem();
            idK = item.getId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("title", judul.getText().toString());
            jBody.put("link", link.getText().toString());
            jBody.put("gambar", bitmapString);
            jBody.put("keterangan", keterangan.getText().toString());
            jBody.put("id_k", idK);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ApiVolley(this, jBody, "POST", URL.urlSettingPromo, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                proses.setEnabled(true);
                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
//                        Intent intent = new Intent(SettingPromoActivity.this, CreatePromoActivity.class);
//                        startActivity(intent);
//                        finish();
                        onBackPressed();

//                        Toast.makeText(getApplicationContext(), message,Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                dismissProgressDialog();
                proses.setEnabled(true);
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

        return Bitmap.createScaledBitmap(realImage, width, height, filter);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {
                photo = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                showCamera.setImageBitmap(Bitmap.createScaledBitmap(photo, 640, 640, true));
            } catch (IOException e) {
                e.printStackTrace();
            }

//            /*bitWidth = photo.getWidth();
//            bitHeight = photo.getHeight();
//            Matrix matrix = new Matrix();
//            matrix.postScale(curScale, curScale);
//            matrix.postRotate(curRotate);
//            Bitmap reziseBitmap = Bitmap.createBitmap(photo,0,0,bitWidth,bitHeight,matrix,true);*/

            BitmapDrawable drawable = (BitmapDrawable) showCamera.getDrawable();
            bitmap = drawable.getBitmap();

            bitmap = scaleDown(bitmap, 512, true);
            bitmapString = EncodeBitmapToString.convert(bitmap);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(SettingPromoActivity.this, CreatePromoActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(SettingPromoActivity.this,
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

}
