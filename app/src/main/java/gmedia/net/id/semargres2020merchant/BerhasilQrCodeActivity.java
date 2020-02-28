package gmedia.net.id.semargres2020merchant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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
    Toolbar toolbar;

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

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() !=  null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        prepareDataHasilScanBarcode();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent();
            setResult(501,intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
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
        Intent intent = new Intent();
        setResult(501,intent);
        finish();
    }
}
