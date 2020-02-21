package gmedia.net.id.semargres2020merchant.tiketKonser;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import gmedia.net.id.semargres2020merchant.HomeActivity;
import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.tiketKonser.history.HistoryTiketFragment;
import gmedia.net.id.semargres2020merchant.tiketKonser.pembelian.PembelianFragment;
import gmedia.net.id.semargres2020merchant.tiketKonser.rekap.RekapFragment;

public class KonserActivity extends AppCompatActivity {

    public String editable = "";
    public String qrCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konser);

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

        ViewPager viewPager = findViewById(R.id.container);
        TabLayout tabLayout = findViewById(R.id.tabs);

        viewPager.setAdapter(new KonserPagerAdapter(getSupportFragmentManager()));

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        if(getIntent().getStringExtra("editable")!=null){
            editable = getIntent().getStringExtra("editable");

//            if(editable.equals("false")){
//                getEmail();
//                txt_email.setEnabled(false);
//            }
//            else{
//                txt_email.setEnabled(true);
//            }
        }

        if(getIntent().getStringExtra("qr_code")!=null){
            qrCode = getIntent().getStringExtra("qr_code");
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(KonserActivity.this, HomeActivity.class);
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

    class KonserPagerAdapter extends FragmentPagerAdapter{
        KonserPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if(i == 0){
                return new PembelianFragment();
            }
            else if(i == 1){
                return new HistoryTiketFragment();
            }
            else if(i == 2){
                return new RekapFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
