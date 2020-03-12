package gmedia.net.id.semargres2020merchant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.util.CircleTransform;

public class BerhasilQrCodeActivity extends AppCompatActivity {
    TextView nama, telpon, email, jumlah_kupon, tvUser;
    ImageView gambar;
    Toolbar toolbar;
    Bundle save;

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
        tvUser = findViewById(R.id.tv_user);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() !=  null){
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        save = getIntent().getExtras();

        prepareDataHasilScanBarcode();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            if(save.getString("type").equals("merchant")){
                Intent intent = new Intent();
                setResult(501,intent);
            }else if(save.getString("type").equals("tenant")){
                Intent intent = new Intent();
                setResult(401, intent);
            }else{
                Intent intent = new Intent();
                setResult(601,intent);
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void prepareDataHasilScanBarcode() {
        if (save != null) {
            nama.setText(save.getString("nama", ""));
            email.setText(save.getString("email", ""));
            telpon.setText(save.getString("telpon", ""));
            if(save.getString("gambar","").isEmpty()){
                tvUser.setVisibility(View.VISIBLE);
                if(!save.getString("nama","").isEmpty()){
                    tvUser.setText(save.getString("nama", "").substring(0,1).toUpperCase());
                }else if(!save.getString("email","").isEmpty()){
                    tvUser.setText(save.getString("email", "").substring(0,1).toUpperCase());
                }else if(!save.getString("telpon","").isEmpty()){
                    tvUser.setText(save.getString("telpon", "").substring(0,1).toUpperCase());
                }else{
                    tvUser.setText("E");
                }
            }else{
                tvUser.setVisibility(View.GONE);
            }
            Picasso
                    .with(BerhasilQrCodeActivity.this)
                    .load(save.getString("gambar", "").isEmpty() ? null : save.getString("gambar",""))
//                    .load(String.valueOf(save.getString("gambar","") != null ? save.getString("gambar","") : R.drawable.background_upload_profile))
                    .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .transform(new CircleTransform())
                    .into(gambar);

            jumlah_kupon.setText(save.getString("jumlah_kupon", ""));
        }
    }

    @Override
    public void onBackPressed() {
        if(save.getString("type").equals("merchant")) {
            Intent intent = new Intent();
            setResult(501, intent);
        }else if(save.getString("type").equals("tenant")){
            Intent intent = new Intent();
            setResult(401, intent);
        }else{
            Intent intent = new Intent();
            setResult(601,intent);
        }
        finish();
    }
}
