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
import android.support.v7.widget.Toolbar;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

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

public class EditPromoActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 1;
    EditText title, link, keterangan;
    ImageView gambar, showCamera, openCamera;
    Button simpan;
    Bitmap photo, bitmap;
    String linkID;
    private ProgressDialog progressDialog;
    private RelativeLayout home;
    private RelativeLayout logout;
    private String bitmapString = "";
    private ProgressBar pbLoading;
    private Spinner spKategori;
    private ArrayList<CustomKategoriModel> kategoriList;
    private String selectedKategori = "";
    private ArrayAdapter<CustomKategoriModel> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_promo);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar));
        }

        LinearLayout utama = findViewById(R.id.layoutUtamaEditPromo);
        utama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyboard.hideSoftKeyboard(EditPromoActivity.this);
            }
        });

//        RelativeLayout back = findViewById(R.id.backEditPromo);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        pbLoading = findViewById(R.id.pb_loading);

//        home = (RelativeLayout) findViewById(R.id.btnHomeCreatePromo);
//        logout = (RelativeLayout) findViewById(R.id.btnLogout);
//        final ImageView gbrHome = findViewById(R.id.gambarHome);
//        final ImageView gbrLogout = findViewById(R.id.gambarLogout);
//        final TextView txtGbrHome = findViewById(R.id.textGambarHome);
//        final TextView txtGbrLogout = findViewById(R.id.textGambarLogout);
//
//        home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!HomeActivity.isHome){
//                    Intent intent = new Intent(EditPromoActivity.this,HomeActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        });
//
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                gbrHome.setImageResource(R.drawable.home_grey);
//                txtGbrHome.setTextColor(Color.parseColor("#FF5F5D5D"));
//                gbrLogout.setImageResource(R.drawable.logout_red);
//                txtGbrLogout.setTextColor(Color.parseColor("#e40112"));
//                final Dialog dialog = new Dialog(EditPromoActivity.this);
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

        title = findViewById(R.id.titleEditPromo);
        link = findViewById(R.id.urlEditPromo);
        keterangan = findViewById(R.id.keteranganEditPromo);
//        gambar = findViewById(R.id.showCameraEditPromo);
        showCamera = findViewById(R.id.showCameraEditPromo);
        openCamera = findViewById(R.id.openCameraEditPromo);

        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        spKategori = findViewById(R.id.sp_kategori);

        getDataMerchant();
        //prepareDataEditPromo();

        simpan = findViewById(R.id.btnEditPromo);
        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!bitmapString.equals("")) {

                    if (title.getText().toString().equals("")) {
                        title.setError("Silahkan mengisi judul promo");
                        title.requestFocus();
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

                    AlertDialog alertDialog = new AlertDialog.Builder(EditPromoActivity.this)
                            .setTitle("Konfirmasi")
                            .setMessage("Apakah Anda yakin ingin menyimpan data?")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    prepareEditPromo();
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
                    Toast.makeText(EditPromoActivity.this, "Harap pilih gambar terlebih dahulu", Toast.LENGTH_LONG).show();
                }

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

    private void getDataMerchant() {

        new ApiVolley(EditPromoActivity.this, new JSONObject(), "GET", URL.urlProfile, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                selectedKategori = "";
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
                selectedKategori = "";
                getDataKategori();
            }
        });
    }

    private void getDataKategori() {

        new ApiVolley(EditPromoActivity.this, new JSONObject(), "GET", URL.urlKategori, "", "", 0, new ApiVolley.VolleyCallback() {
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

                        adapter = new ArrayAdapter<>(EditPromoActivity.this, R.layout.item_simple_item_promo, kategoriList);
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

                prepareDataEditPromo();
            }

            @Override
            public void onError(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        });
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
            bitmapString = bitmap != null ? EncodeBitmapToString.convert(bitmap) : "";
        }
    }

    private void prepareEditPromo() {

        simpan.setEnabled(false);
        showProgressDialog();
        Bundle save = getIntent().getExtras();
        if (save != null) {
            linkID = save.getString("id", "");
        }

        String idK = "";
        try {

            CustomKategoriModel item = (CustomKategoriModel) spKategori.getSelectedItem();
            idK = item.getId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("id", linkID);
            jBody.put("title", title.getText().toString());
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

                simpan.setEnabled(true);
                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {

//                        Intent intent = new Intent(EditPromoActivity.this, CreatePromoActivity.class);
//                        startActivity(intent);
                        finish();

//                        onBackPressed();

//                        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Terjadi kesalahan saat menyimpan data, harap ulangi", Toast.LENGTH_LONG).show();
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

    private void prepareDataEditPromo() {

        Bundle save = getIntent().getExtras();
        if (save != null) {
            title.setText(save.getString("title", ""));
            link.setText(save.getString("link", ""));
            keterangan.setText(save.getString("keterangan", ""));

            String idK = save.getString("id_k", "");

            int position = 0;
            boolean isSelected = false;
            for (int i = 0; i < kategoriList.size(); i++) {

                if (kategoriList.get(i).getId().equals(idK)) {
                    position = i;
                    isSelected = true;
                }
            }

            if (isSelected) {

                try {
                    spKategori.setSelection(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            pbLoading.setVisibility(View.VISIBLE);
            Picasso.with(EditPromoActivity.this).load(save.getString("gambar", ""))
                    .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(showCamera, new Callback() {
                        @Override
                        public void onSuccess() {

                            BitmapDrawable drawable = (BitmapDrawable) showCamera.getDrawable();
                            bitmap = drawable.getBitmap();
                            bitmap = scaleDown(bitmap, 512, true);
                            bitmapString = bitmap != null ? EncodeBitmapToString.convert(bitmap) : "";
                            pbLoading.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            pbLoading.setVisibility(View.GONE);
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
//        Intent i = new Intent(EditPromoActivity.this, CreatePromoActivity.class);
//        startActivity(i);
        finish();
//        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(EditPromoActivity.this,
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
