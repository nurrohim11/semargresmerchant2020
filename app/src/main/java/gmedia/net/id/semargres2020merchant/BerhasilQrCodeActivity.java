package gmedia.net.id.semargres2020merchant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.util.CircleTransform;

public class BerhasilQrCodeActivity extends AppCompatActivity {
    TextView nama, telpon, email, jumlah_kupon;
    ImageView gambar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.berhasil_qr_code);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar));
        }

        nama = findViewById(R.id.namaHasilScanBarcode);
        telpon = findViewById(R.id.teleponHasilScanBarcode);
        email = findViewById(R.id.emailHasilScanBarcode);
        jumlah_kupon = findViewById(R.id.jumlahKuponHasilScanBarcode);
        gambar = findViewById(R.id.fotoHasilScanBarcode);

        RelativeLayout back = findViewById(R.id.backBerhasilQrCode);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        RelativeLayout home = findViewById(R.id.btnHomeBerhasilQrCode);
//        RelativeLayout logout = findViewById(R.id.btnLogout);
//        final ImageView gbrHome = findViewById(R.id.gambarHome);
//        final ImageView gbrLogout = findViewById(R.id.gambarLogout);
//        final TextView txtGbrHome = findViewById(R.id.textGambarHome);
//        final TextView txtGbrLogout = findViewById(R.id.textGambarLogout);
//        home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!HomeActivity.isHome) {
//                    Intent i = new Intent(BerhasilQrCodeActivity.this, HomeActivity.class);
//                    startActivity(i);
//                    finish();
//                    overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
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
//                final Dialog dialog = new Dialog(BerhasilQrCodeActivity.this);
//                dialog.setContentView(R.layout.popup_logout);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                RelativeLayout ya = dialog.findViewById(R.id.logoutYa);
//                RelativeLayout tidak = dialog.findViewById(R.id.logoutTidak);
//                ya.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent i = new Intent(BerhasilQrCodeActivity.this, LoginActivity.class);
//                        startActivity(i);
//                        finish();
//                        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
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

        prepareDataHasilScanBarcode();
    }

    private void prepareDataHasilScanBarcode() {
        Bundle save = getIntent().getExtras();
        if (save != null) {
            nama.setText(save.getString("nama", ""));
            email.setText(save.getString("email", ""));
            telpon.setText(save.getString("telpon", ""));
            Picasso.with(BerhasilQrCodeActivity.this).load(save.getString("gambar", ""))
                    .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .transform(new CircleTransform()).into(gambar);
            jumlah_kupon.setText(save.getString("jumlah_kupon", ""));
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(BerhasilQrCodeActivity.this, HomeActivity.class);
        startActivity(i);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
