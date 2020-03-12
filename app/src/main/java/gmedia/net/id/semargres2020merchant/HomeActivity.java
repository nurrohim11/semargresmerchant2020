package gmedia.net.id.semargres2020merchant;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.semargres2020merchant.akun.ListAkunActivity;
import gmedia.net.id.semargres2020merchant.historyPenjualan.volunteer.HistoryPenjualanVolunterActivity;
import gmedia.net.id.semargres2020merchant.merchant.KirimEmailMerActivity;
import gmedia.net.id.semargres2020merchant.merchant.KirimScanQrMerActivity;
import gmedia.net.id.semargres2020merchant.merchant.KirimSmsMerActivity;
import gmedia.net.id.semargres2020merchant.util.CircleTransform;
import gmedia.net.id.semargres2020merchant.util.Utils;
import gmedia.net.id.semargres2020merchant.volunteer.kirimkupon.KirimEmailVolActivity;
import gmedia.net.id.semargres2020merchant.volunteer.kirimkupon.KirimScanQrVolActivity;
import gmedia.net.id.semargres2020merchant.volunteer.kirimkupon.KirimSmsVolActivity;
import gmedia.net.id.semargres2020merchant.volunteer.MerchantAdapter;
import gmedia.net.id.semargres2020merchant.volunteer.MerchantModel;
import gmedia.net.id.semargres2020merchant.historyPenjualan.HistoryPenjualanActivity;
import gmedia.net.id.semargres2020merchant.kategoriKupon.SettingKategoriKuponActivity;
import gmedia.net.id.semargres2020merchant.kategoriKupon.SettingKategoriKuponModel;
import gmedia.net.id.semargres2020merchant.kuis.KuisActivity;
import gmedia.net.id.semargres2020merchant.volunteer.SessionMerchant;
import gmedia.net.id.semargres2020merchant.promo.CreatePromoActivity;
import gmedia.net.id.semargres2020merchant.tiketKonser.KonserActivity;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.FormatRupiah;
import gmedia.net.id.semargres2020merchant.util.RuntimePermissionsActivity;
import gmedia.net.id.semargres2020merchant.util.SessionManager;
import gmedia.net.id.semargres2020merchant.util.TwoItemModel;
import gmedia.net.id.semargres2020merchant.util.URL;
import gmedia.net.id.semargres2020merchant.voucher.VoucherActivity;

public class HomeActivity extends RuntimePermissionsActivity {

    private static final int REQUEST_PERMISSIONS = 20;
    public static boolean isHome = true;
    public static IntentResult resultScanBarcode;
    public static EditText isiSettingTotalBelanja;
    SessionManager session;
    EditText isiSettingKupon, isiEmailSendEmail, isiNominalSendEMail, isiWhatsapp, isiNominalSendWhatsapp;
    String version = "", latestVersion = "", link = "";
    String settingKupon = "";
    TextView totalPenjualan, sisaKupon, settinganKupon, textHome, namaMerchant, textSisaKupon;
    LinearLayout menuSettingKupon;
    LinearLayout llTambahAkun;
    CardView layoutSettingKupon, layoutTambahAkun, layoutCreatePromo, layoutKuis;
    private boolean doubleBackToExitPressedOnce = false;
    private String TAG = "home";
    private EditText passLama, passBaru, rePassBaru;
    private Boolean klikToVisiblePassLama = true;
    private Boolean klikToVisiblePassBaru = true;
    private Boolean klikToVisibleRePassBaru = true;
    private String IDscanBarcode = "0", IDemail = "0", IDwhatsapp = "0";
    private String kategoriEmail = "0", kategoriWhatsapp = "0", kategoriScan = "0";
    private ProgressDialog progressDialog;
    private List<TwoItemModel> caraBayarList;
    private ArrayList<SettingKategoriKuponModel> dataSet;

    private String kondisi = "";
    private String kostum = "Scan berkostum";
    private String tanpaKostum = "Scan tanpa kostum";
    private String tiketEmail = "Tiket dengan Email";
    private String tiketScan = "Tiket dengan Scan QR Code";

    public static Dialog dgMerchant;
    private RecyclerView rvMerchant;
    MerchantAdapter merchantAdapter;
    private List<MerchantModel> merchantModels = new ArrayList<>();
    private SessionMerchant sessionMerchant;
    public static TextView tvMerchant;
    private String keyword_merchant="";
    private int start =0, count=20;
    ImageView imageHome;

    public static TextView tvMerchantEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar));
        }

        session = new SessionManager(getApplicationContext());

        permissionCheck();

        visibilityCheck();

//		totalPenjualan = findViewById(R.id.totalPenjualan);

        sisaKupon = findViewById(R.id.sisaKupon);
        settinganKupon = findViewById(R.id.settinganKupon);
        textHome = findViewById(R.id.textHome);
        namaMerchant = findViewById(R.id.namaMerchantHome);
        textSisaKupon = findViewById(R.id.textSisaKupon);
        imageHome = findViewById(R.id.imageHome);

//        Setting pojok kanan atas
        final ImageView menuSetting = findViewById(R.id.logo_setting);
        menuSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuSettings();
            }
        });

//        Edit profile
        Button menuProfile = findViewById(R.id.btn_profile);
        if(session.getFlag().equals("3")){
            menuProfile.setVisibility(View.INVISIBLE);
        }
        menuProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                isHome = false;
                Intent i = new Intent(HomeActivity.this, Profile.class);
                startActivity(i);
            }
        });

//        LinearLayout menuKostum = findViewById(R.id.menuScanBerkostum);
//        menuKostum.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                kondisi = kostum;
//                openScanBarcode();
//            }
//        });

//        LinearLayout menuTanpaKostum = findViewById(R.id.menuScanNoKostum);
//        menuTanpaKostum.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                kondisi = tanpaKostum;
//                openScanBarcode();
//            }
//        });

//        Tiket konser
//        LinearLayout menuTiketKonser = findViewById(R.id.menuTiketKonser);
//        menuTiketKonser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tiketKonser();
//            }
//        });

//        Kategori e-kupon
        menuSettingKupon = findViewById(R.id.menuSettingKupon);
        menuSettingKupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kategoriKupon();
            }
        });

//        Tambah akun tenant
        llTambahAkun = findViewById(R.id.ll_tambah_akun);
        llTambahAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                isHome = false;
                Intent i = new Intent(HomeActivity.this, ListAkunActivity.class);
                startActivity(i);
            }
        });

//        Buat promo
        LinearLayout menuPromo = findViewById(R.id.menuPromo);
        menuPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuCreatePromo();
            }
        });

//        Kirim e-kupon
        LinearLayout menuCreateKupon = findViewById(R.id.menuCreateKupon);
        menuCreateKupon.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                kirimKupon();
            }
        });

//        History penjualan
        LinearLayout menuInfoPenjualan = findViewById(R.id.menuInfoPenjualan);
        menuInfoPenjualan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(session.getFlag().equals("3")){
                    Intent intent = new Intent(HomeActivity.this, HistoryPenjualanVolunterActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(HomeActivity.this, HistoryPenjualanActivity.class);
                    startActivity(intent);
                }
//                finish();
            }
        });

//        Buat kuis
        LinearLayout menuBuatKuis = findViewById(R.id.menuBuatKuis);
        menuBuatKuis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, KuisActivity.class);
                startActivity(intent);
//                finish();
            }
        });


//		/*LinearLayout menuMyQRCode = findViewById(R.id.menuMyQRCode);
//		menuMyQRCode.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				isHome = false;
//				Intent i = new Intent(HomeActivity.this, MyQRCode.class);
//				startActivity(i);
//			}
//		});*/

//		LinearLayout menuPromo = (LinearLayout) findViewById(R.id.menuPromo);
//		menuPromo.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				isHome = false;
//				Intent i = new Intent(HomeActivity.this, CreatePromoActivity.class);
//				startActivity(i);
//			}
//		});


//		LinearLayout menuScanBarcode = findViewById(R.id.menuscanbarcode);
//		menuScanBarcode.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				getCaraBayarData(0);
//			}
//		});
//		LinearLayout menuEmail = findViewById(R.id.menuEmail);
//		menuEmail.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//
//				getCaraBayarData(1);
//			}
//		});
//        LinearLayout menuWhatsapp = findViewById(R.id.menuWhatsapp);
//		menuWhatsapp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                getCaraBayarData(2);
//            }
//        });


    }

    private void permissionCheck() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            SettingPromoActivity.openCamera.setEnabled(true);
            super.requestAppPermissions(new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.INTERNET,
                            Manifest.permission.WRITE_SETTINGS,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    R.string.runtime_permissions_txt, REQUEST_PERMISSIONS);
        }
    }

    private void visibilityCheck() {
        layoutSettingKupon = findViewById(R.id.layoutSettingKuponHome);
        layoutTambahAkun = findViewById(R.id.layoutTambahAkunHome);
        layoutCreatePromo = findViewById(R.id.layoutPromoHome);
        layoutKuis = findViewById(R.id.layoutBuatKuis);
        LinearLayout layout = findViewById(R.id.layoutSettinganKupon);
        LinearLayout layoutNama = findViewById(R.id.layoutNamaMerchant);

//        CardView layoutKostum = findViewById(R.id.layoutScanBerkostum);
//        CardView layoutNoKostum = findViewById(R.id.layoutScanNoKostum);

//        CardView layoutKonser = findViewById(R.id.layoutTiketKonser);

        if (session.getFlag().equals("2")) {
            layoutSettingKupon.setVisibility(View.GONE);
            layoutCreatePromo.setVisibility(View.VISIBLE);
            layoutTambahAkun.setVisibility(View.GONE);
            layoutKuis.setVisibility(View.VISIBLE);
            layout.setVisibility(View.VISIBLE);
        }else if(session.getFlag().equals("3")){
            layoutTambahAkun.setVisibility(View.GONE);
            layoutCreatePromo.setVisibility(View.GONE);
            layoutKuis.setVisibility(View.GONE);
            layoutSettingKupon.setVisibility(View.GONE);
            layout.setVisibility(View.GONE);
            layoutNama.setVisibility(View.GONE);
        } else {
            layoutCreatePromo.setVisibility(View.VISIBLE);
            layoutSettingKupon.setVisibility(View.VISIBLE);
            layoutTambahAkun.setVisibility(View.VISIBLE);
            layoutKuis.setVisibility(View.VISIBLE);
            layout.setVisibility(View.GONE);
            layoutNama.setVisibility(View.GONE);
        }

//        if(session.getFlagParade().equals("1")){
//            layoutKostum.setVisibility(View.VISIBLE);
//            layoutNoKostum.setVisibility(View.VISIBLE);
//        }
//        else{
//            layoutKostum.setVisibility(View.GONE);
//            layoutNoKostum.setVisibility(View.GONE);
//        }
//
//        if(session.getFlagTiket().equals("1")){
//            layoutKonser.setVisibility(View.VISIBLE);
//        }
//        else{
//            layoutKonser.setVisibility(View.GONE);
//        }
    }

    private void menuSettings() {
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.popup_tombol_setting);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        RelativeLayout qrLayout = dialog.findViewById(R.id.layout_qr);
        qrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MyQRCode.class));
            }
        });

        if (session.getFlag().equals("2")) {
            qrLayout.setVisibility(View.VISIBLE);
        } else {
            qrLayout.setVisibility(View.GONE);
        }

        RelativeLayout logoutNew = dialog.findViewById(R.id.layout_logout);
        logoutNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.popup_logout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                RelativeLayout ya = dialog.findViewById(R.id.logoutYa);
                RelativeLayout tidak = dialog.findViewById(R.id.logoutTidak);
                ya.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        session.logoutUser();
                    }
                });
                tidak.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        RelativeLayout gantiPasswordNew = dialog.findViewById(R.id.layout_gantiPass);
        gantiPasswordNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(HomeActivity.this);
                dialog.setContentView(R.layout.popup_ganti_password);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final ImageView visiblePassLama = dialog.findViewById(R.id.visiblePassLama);
                final ImageView visiblePassBaru = dialog.findViewById(R.id.visiblePassBaru);
                final ImageView visibleRePassBaru = dialog.findViewById(R.id.visibleRePassBaru);
                passLama = dialog.findViewById(R.id.passLama);
                passBaru = dialog.findViewById(R.id.passBaru);
                rePassBaru = dialog.findViewById(R.id.reTypePassBaru);
                visiblePassLama.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (klikToVisiblePassLama) {
                            visiblePassLama.setImageDrawable(getResources().getDrawable(R.drawable.visible));
                            passLama.setInputType(InputType.TYPE_CLASS_TEXT);
                            klikToVisiblePassLama = false;
                        } else {
                            visiblePassLama.setImageDrawable(getResources().getDrawable(R.drawable.invisible));
                            passLama.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            passLama.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            klikToVisiblePassLama = true;
                        }
                    }
                });
                visiblePassBaru.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (klikToVisiblePassBaru) {
                            visiblePassBaru.setImageDrawable(getResources().getDrawable(R.drawable.visible));
                            passBaru.setInputType(InputType.TYPE_CLASS_TEXT);
                            klikToVisiblePassBaru = false;
                        } else {
                            visiblePassBaru.setImageDrawable(getResources().getDrawable(R.drawable.invisible));
                            passBaru.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            passBaru.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            klikToVisiblePassBaru = true;
                        }
                    }
                });
                visibleRePassBaru.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (klikToVisibleRePassBaru) {
                            visibleRePassBaru.setImageDrawable(getResources().getDrawable(R.drawable.visible));
                            rePassBaru.setInputType(InputType.TYPE_CLASS_TEXT);
                            klikToVisibleRePassBaru = false;
                        } else {
                            visibleRePassBaru.setImageDrawable(getResources().getDrawable(R.drawable.invisible));
                            rePassBaru.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            rePassBaru.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            klikToVisibleRePassBaru = true;
                        }
                    }
                });
                RelativeLayout OK = dialog.findViewById(R.id.tombolOKgantiPassword);
                OK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (passLama.getText().toString().equals("")) {
                            passLama.setError("Harap diisi");
                            passLama.requestFocus();
                            return;
                        }

                        if (passBaru.getText().toString().equals("")) {
                            passBaru.setError("Harap diisi");
                            passBaru.requestFocus();
                            return;
                        }

                        if (rePassBaru.getText().toString().equals("")) {
                            rePassBaru.setError("Harap diisi");
                            rePassBaru.requestFocus();
                            return;
                        }

                        AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this)
                                .setTitle("Konfirmasi")
                                .setMessage("Apakah Anda yakin ingin menyimpan data?")
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(session.getFlag().equals("3")){
                                            prepareDataGantiPasswordVolunteer();
                                        }else{
                                            prepareDataGantiPassword();
                                        }
                                    }
                                })
                                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .show();
                        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.color_grey_new));

                        dialog.dismiss();
                    }
                });
                RelativeLayout cancel = dialog.findViewById(R.id.tombolcancelGantiPassword);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        LinearLayout layoutUtama = (LinearLayout) dialog.findViewById(R.id.layoutUtamaPopupTombolSetting);
        layoutUtama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void kategoriKupon() {
        Intent intent = new Intent(HomeActivity.this, SettingKategoriKuponActivity.class);
        startActivity(intent);
    }

    private void menuCreatePromo() {

        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_create_promo);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.gravity = Gravity.BOTTOM;
            lp.windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setAttributes(lp);
        }

        LinearLayout menuDiskon = dialog.findViewById(R.id.menuDiskon);
        menuDiskon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, CreatePromoActivity.class);
                startActivity(i);
            }
        });

        LinearLayout menuVoucher = dialog.findViewById(R.id.menuVoucher);
        menuVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, VoucherActivity.class);
                startActivity(i);
            }
        });
        dialog.show();

    }

    private void tiketKonser() {

        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_tiket_konser);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.gravity = Gravity.BOTTOM;
            lp.windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setAttributes(lp);
        }

        LinearLayout menuTiketEmail = dialog.findViewById(R.id.menuTiketEmail);
        menuTiketEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kondisi = tiketEmail;
                Intent intent = new Intent(HomeActivity.this, KonserActivity.class);
                intent.putExtra("editable", "true");
                startActivity(intent);
                finish();

                dialog.dismiss();
            }
        });

        LinearLayout menuTiketScan = dialog.findViewById(R.id.menuTiketScanQr);
        menuTiketScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kondisi = tiketScan;
                openScanBarcode();

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void kirimKupon() {
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_create_kupon);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.gravity = Gravity.BOTTOM;
            lp.windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setAttributes(lp);
        }

        LinearLayout menuScanBarcode = dialog.findViewById(R.id.menuscanbarcode);
        menuScanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(session.getFlag().equals("3")){
                    Intent intent = new Intent(getBaseContext(), KirimScanQrVolActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getBaseContext(), KirimScanQrMerActivity.class);
                    startActivity(intent);
                }
            }
        });

        LinearLayout menuEmail = dialog.findViewById(R.id.menuEmail);
        menuEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getCaraBayarData(1);
                if(session.getFlag().equals("3")){
                    Intent intent = new Intent(getBaseContext(), KirimEmailVolActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getBaseContext(), KirimEmailMerActivity.class);
                    startActivity(intent);
                }
            }
        });

        LinearLayout menuWhatsapp = dialog.findViewById(R.id.menuWhatsapp);
        menuWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getCaraBayarData(2);

                if(session.getFlag().equals("3")){
                    Intent intent = new Intent(getBaseContext(), KirimSmsVolActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getBaseContext(), KirimSmsMerActivity.class);
                    startActivity(intent);
                }
            }
        });
        dialog.show();

    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(HomeActivity.this,
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

    @Override
    protected void onResume() {
        super.onResume();
        checkVersion();
        if (session.getFlag().equals("2")){
            prepareDataDashboardTenant();
        }else if(session.getFlag().equals("1")){
            prepareDataDashboard();
        }else{
            textSisaKupon.setVisibility(View.INVISIBLE);
            sisaKupon.setVisibility(View.INVISIBLE);
            prepareDataProfileVolunter();
        }

    }

    private void checkVersion() {

        PackageInfo pInfo = null;
        version = "";

        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        version = pInfo.versionName;
//        getSupportActionBar().setSubtitle(getResources().getString(R.string.app_name) + " v "+ version);
        latestVersion = "";
        link = "";

        new ApiVolley(HomeActivity.this, new JSONObject(), "GET", URL.urlCheckVersion, "", "", 0, new ApiVolley.VolleyCallback() {

            @Override
            public void onSuccess(String result) {

                JSONObject responseAPI;
                try {
                    responseAPI = new JSONObject(result);
                    String status = responseAPI.getJSONObject("metadata").getString("status");

                    if (status.equals("200")) {
                        latestVersion = responseAPI.getJSONObject("response").getString("build_version");
                        link = responseAPI.getJSONObject("response").getString("link_update");

                        if (!version.trim().equals(latestVersion.trim()) && link.length() > 0) {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                            builder.setIcon(R.mipmap.ic_launcher)
                                    .setTitle("Update")
                                    .setMessage("Versi terbaru " + latestVersion + " telah tersedia, mohon download versi terbaru.")
                                    .setPositiveButton("Update Sekarang", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                                            startActivity(browserIntent);
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
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

    private void prepareDataDashboard() {
        new ApiVolley(HomeActivity.this, new JSONObject(), "GET", URL.urlDashboard, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("message");
                    String statusJson = object.getJSONObject("metadata").getString("status");
                    if (statusJson.equals("200")) {
                        JSONObject isi = object.getJSONObject("response");
                        FormatRupiah request = new FormatRupiah();
//						totalPenjualan.setText(request.ChangeToRupiahFormat(isi.getString("total_transaksi")));

                        String sisaEkupon = isi.getString("sisa_kupon");
                        String gabungan = sisaEkupon + " E-Kupon";

                        sisaKupon.setText(gabungan);
                        settinganKupon.setText(request.ChangeToRupiahFormat(isi.getString("setting_kupon")));
                        settingKupon = isi.getString("setting_kupon");
                        prepareDataProfile();
                    }

                    /*else if (status.equals("Unauthorized")){d
                        Toast.makeText(getApplicationContext(),"Silahkan LoginActivity Ulang",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }*/

                    else {
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

    private void prepareDataDashboardTenant() {
        new ApiVolley(HomeActivity.this, new JSONObject(), "GET", URL.urlDashboardTenant, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(">>>>>", result);
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("message");
                    String statusJson = object.getJSONObject("metadata").getString("status");
                    if (statusJson.equals("200")) {
                        JSONObject isi = object.getJSONObject("response");
                        FormatRupiah request = new FormatRupiah();
//						totalPenjualan.setText(request.ChangeToRupiahFormat(isi.getString("total_transaksi")));

                        String sisaEkupon = isi.getString("sisa_kupon");
                        String gabungan = sisaEkupon + " E-Kupon";

                        sisaKupon.setText(gabungan);
                        settinganKupon.setText(request.ChangeToRupiahFormat(isi.getString("setting_kupon")));
                        settingKupon = isi.getString("setting_kupon");
                        prepareDataProfile();
                        prepareDataProfileTenant();
                    }

                    else {
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


    private void prepareDataProfile() {
        new ApiVolley(HomeActivity.this, new JSONObject(), "GET", URL.urlProfile, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(">>>>>>",result);
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("message");
                    if (status.equals("Success!")) {
                        if (session.getFlag().equals("2")){
                            namaMerchant.setText(object.getJSONObject("response").getString("nama"));
                        }
                        else{
                            textHome.setText(object.getJSONObject("response").getString("nama"));
                        }

                        Picasso.with(HomeActivity.this).load(object.getJSONObject("response").getString("foto"))
                                //.resize(275, 256)
                                .transform(new Utils.RoundedTransformation(30, 0))
                                .into(imageHome);

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
        new ApiVolley(HomeActivity.this, new JSONObject(), "GET", URL.urlProfileTenant, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    if (status.equals("200")) {
//                        Log.d(">>>>>profil", String.valueOf(object.getJSONObject("response")));
                        textHome.setText(object.getJSONObject("response").getJSONObject("profil").getString("nama_tenant"));
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

    private void prepareDataProfileVolunter() {
        new ApiVolley(HomeActivity.this, new JSONObject(), "GET", URL.urlProfileVolunter, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(">>>>>>>>",result);
                try {
                    Log.d(">>>>>",result);
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    if (status.equals("200")) {
                        textHome.setText(object.getJSONObject("response").getString("nama"));
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


    private void prepareDataGantiPasswordVolunteer() {
        showProgressDialog();

        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("password_lama", passLama.getText());
            jBody.put("password_baru", passBaru.getText());
            jBody.put("re_password", rePassBaru.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(HomeActivity.this, jBody, "POST", URL.urlGantiPasswordVolunteer, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(">>>>>",result);
                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    String pesan = object.getJSONObject("metadata").getString("message");
                    int status = object.getJSONObject("metadata").getInt("status");
                    if (status ==200) {
                        Toast.makeText(getApplicationContext(), pesan, Toast.LENGTH_LONG).show();
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

    private void prepareDataGantiPassword() {
        showProgressDialog();

        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("password_lama", passLama.getText());
            jBody.put("password_baru", passBaru.getText());
            jBody.put("re_password", rePassBaru.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(HomeActivity.this, jBody, "POST", URL.urlGantiPassword, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    String pesan = object.getJSONObject("metadata").getString("message");
                    String status = object.getJSONObject("response").getString("status");
                    if (status.equals("1")) {
                        Toast.makeText(getApplicationContext(), pesan, Toast.LENGTH_LONG).show();
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

    private void openScanBarcode() {
        IntentIntegrator integrator = new IntentIntegrator(HomeActivity.this);
//        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);

        if(kondisi.equals("")){
            integrator.setPrompt("Kirim e-Kupon");
        }
        else{
            integrator.setPrompt(kondisi);
        }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        resultScanBarcode = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (resultScanBarcode != null) {

            if (resultScanBarcode.getContents() != null) {
                if (kondisi.equals("")){
//                    if (session.getFlag().equals("2")){
//                        prepareDataScanBarcodeTenant();
//                    }
//                    else if(session.getFlag().equals("1")){
//                        prepareDataScanBarcode();
//                    }
                }
                else if(kondisi.equals(tiketScan)){
                    Intent intent = new Intent(HomeActivity.this, KonserActivity.class);
                    intent.putExtra("editable", "false");
                    intent.putExtra("qr_code", resultScanBarcode.getContents());
                    startActivity(intent);
                    finish();
                }
                else if(kondisi.equals(kostum)){
                    dataParade("1");
//                    isiSettingTotalBelanja.setText("5");
//                    prepareDataScanBarcode();
                }
                else if(kondisi.equals(tanpaKostum)){
                    dataParade("0");
//                    isiSettingTotalBelanja.setText("1");
//                    prepareDataScanBarcode();
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void dataParade(String kostumKode){
        showProgressDialog();

        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("qr_data", resultScanBarcode.getContents());
            jBody.put("kostum", kostumKode);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(HomeActivity.this, jBody, "POST", URL.urlParade, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                dismissProgressDialog();
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Klik sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

}
