package gmedia.net.id.semargres2020merchant;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.akun.ListAkunActivity;
import gmedia.net.id.semargres2020merchant.historyPenjualan.HistoryPenjualanActivity;
import gmedia.net.id.semargres2020merchant.kategoriKupon.SettingKategoriKuponActivity;
import gmedia.net.id.semargres2020merchant.kategoriKupon.SettingKategoriKuponModel;
import gmedia.net.id.semargres2020merchant.kuis.KuisActivity;
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
    TextView totalPenjualan, sisaKupon, settinganKupon, textHome, namaMerchant;
    LinearLayout menuSettingKupon;
    LinearLayout llTambahAkun;
    CardView layoutSettingKupon, layoutTambahAkun;
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
                Intent intent = new Intent(HomeActivity.this, HistoryPenjualanActivity.class);
                startActivity(intent);
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
        LinearLayout layout = findViewById(R.id.layoutSettinganKupon);
        LinearLayout layoutNama = findViewById(R.id.layoutNamaMerchant);

//        CardView layoutKostum = findViewById(R.id.layoutScanBerkostum);
//        CardView layoutNoKostum = findViewById(R.id.layoutScanNoKostum);

//        CardView layoutKonser = findViewById(R.id.layoutTiketKonser);

        if (session.getFlag().equals("2")) {
            layoutSettingKupon.setVisibility(View.GONE);
            layoutTambahAkun.setVisibility(View.GONE);
            layout.setVisibility(View.VISIBLE);
        } else {
            layoutSettingKupon.setVisibility(View.VISIBLE);
            layoutTambahAkun.setVisibility(View.VISIBLE);
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
                                        prepareDataGantiPassword();
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
//						dialog.setCanceledOnTouchOutside(false);
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
//				dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void kategoriKupon() {
        Intent intent = new Intent(HomeActivity.this, SettingKategoriKuponActivity.class);
        startActivity(intent);
//        finish();

//				final Dialog dialog = new Dialog(HomeActivity.this);
//				dialog.setContentView(R.layout.popup_setting_kupon);
//				isiSettingKupon = dialog.findViewById(R.id.isiSettingKupon);
//				isiSettingKupon.setText(settingKupon);
//				//if(settingKupon!= null && settingKupon.length() > 0) isiSettingKupon.setSelection(settinganKupon.length() - 1);
//				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//				RelativeLayout btnSave = dialog.findViewById(R.id.btnSave);
//				btnSave.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//
//						if (isiSettingKupon.getText().toString().equals("")) {
//							isiSettingKupon.setError("Nominal harap diisi");
//							isiSettingKupon.requestFocus();
//							return;
//						}
//						prepareDataSettingKupon();
//						dialog.dismiss();
//					}
//				});
//				RelativeLayout btnCancel = dialog.findViewById(R.id.btnCancel);
//				btnCancel.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						dialog.dismiss();
//					}
//				});
//				dialog.setCanceledOnTouchOutside(false);
//				dialog.show();

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
//                isHome = false;
                Intent i = new Intent(HomeActivity.this, CreatePromoActivity.class);
                startActivity(i);
            }
        });

        LinearLayout menuVoucher = dialog.findViewById(R.id.menuVoucher);
        menuVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                isHome = false;
                Intent i = new Intent(HomeActivity.this, VoucherActivity.class);
                startActivity(i);

//						final Dialog dialog = new Dialog(HomeActivity.this);
//						dialog.setContentView(R.layout.popup_create_voucher);
//						dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

//						isiSettingTotalBelanja = dialog.findViewById(R.id.isiSettingTotalBelanja);
//                      radioGroupScanBarcode = dialog.findViewById(R.id.radioGrubScanBarcode);

//						final Spinner spCaraBayarBarcode = (Spinner) dialog.findViewById(R.id.sp_cara_bayar_barcode);
//						ArrayAdapter adapter = new ArrayAdapter(HomeActivity.this, R.layout.layout_simple_list, listItem);
//						spCaraBayarBarcode.setAdapter(adapter);
//						spCaraBayarBarcode.setSelection(0, true);

//        				final Spinner spKategoriBarcode = (Spinner) dialog.findViewById(R.id.sp_kategori_barcode);
//        				ArrayAdapter adapter2 = new ArrayAdapter(HomeActivity.this, R.layout.layout_simple_list, listItem2);
//        				spKategoriBarcode.setAdapter(adapter2);
//        				spKategoriBarcode.setSelection(0, true);

//						RelativeLayout btnSave = dialog.findViewById(R.id.btnSave);
//						btnSave.setOnClickListener(new View.OnClickListener() {
//							@Override
//							public void onClick(View v) {

//								IDscanBarcode = "0";
//								TwoItemModel item = (TwoItemModel) spCaraBayarBarcode.getSelectedItem();
//								IDscanBarcode = item.getItem1();
//
//								if (!isiSettingTotalBelanja.getText().toString().equals("")) {
//									openScanBarcode();
//									dialog.dismiss();
//								} else {
//									//Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
//									isiSettingTotalBelanja.setError("Silahkan isi nominal belanja");
//									isiSettingTotalBelanja.requestFocus();
//									return;
//								}
//							}
//						});
//						RelativeLayout btnCancel = dialog.findViewById(R.id.btnCancel);
//						btnCancel.setOnClickListener(new View.OnClickListener() {
//							@Override
//							public void onClick(View v) {
//								dialog.dismiss();
//							}
//						});
//						dialog.setCanceledOnTouchOutside(false);
//						dialog.show();

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
                getCaraBayarData(0);
            }
        });

        LinearLayout menuEmail = dialog.findViewById(R.id.menuEmail);
        menuEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCaraBayarData(1);
            }
        });

        LinearLayout menuWhatsapp = dialog.findViewById(R.id.menuWhatsapp);
        menuWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCaraBayarData(2);
            }
        });
        dialog.show();

//                isiSettingKupon = dialog.findViewById(R.id.isiSettingKupon);
//                isiSettingKupon.setText(settingKupon);
//                //if(settingKupon!= null && settingKupon.length() > 0) isiSettingKupon.setSelection(settinganKupon.length() - 1);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                RelativeLayout btnSave = dialog.findViewById(R.id.btnSave);
//                btnSave.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        if (isiSettingKupon.getText().toString().equals("")) {
//                            isiSettingKupon.setError("Nominal harap diisi");
//                            isiSettingKupon.requestFocus();
//                            return;
//                        }
//                        prepareDataSettingKupon();
//                        dialog.dismiss();
//                    }
//                });
//                RelativeLayout btnCancel = dialog.findViewById(R.id.btnCancel);
//                btnCancel.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//                dialog.setCanceledOnTouchOutside(false);
//                dialog.show();

    }

    private void getCaraBayarData(final int jenis) {
        // 0. barcode, 1. by email, 2. whatsapp

        new ApiVolley(HomeActivity.this, new JSONObject(), "GET", URL.urlCaraBayar, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    String status = object.getJSONObject("metadata").getString("status");
                    caraBayarList = new ArrayList<>();

                    if (status.equals("200")) {

                        JSONArray jArray = object.getJSONArray("response");
                        for (int i = 0; i < jArray.length(); i++) {

                            JSONObject jo = jArray.getJSONObject(i);
                            caraBayarList.add(new TwoItemModel(jo.getString("value"), jo.getString("text")));
                        }

                        getKategoriKupon(jenis, caraBayarList);

                    } else {
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

    private void getKategoriKupon(final int jenis, final List<TwoItemModel> list) {
        dataSet = new ArrayList<>();

        if (session.getFlag().equals("2")){
            dataSet.add(new SettingKategoriKuponModel(
                    "id",
                    "nama",
                    "nominal"
            ));
            createKupon(jenis, list, dataSet);
        }
        else{
            new ApiVolley(HomeActivity.this, new JSONObject(), "POST", URL.urlKategoriKupon, "", "", 0, new ApiVolley.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JSONObject object = new JSONObject(result);
                        String status = object.getJSONObject("metadata").getString("status");
                        String message = object.getJSONObject("metadata").getString("message");
                        if (status.equals("200")) {
                            JSONArray array = object.getJSONObject("response").getJSONArray("kategori");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject isi = array.getJSONObject(i);
                                dataSet.add(new SettingKategoriKuponModel(
                                        isi.getString("id"),
                                        isi.getString("nama"),
                                        isi.getString("nominal")
                                ));
                            }
                            createKupon(jenis, list, dataSet);
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
    }

    private void createKupon(int jenis, List<TwoItemModel> list, ArrayList<SettingKategoriKuponModel> list2) {
        if (jenis == 0 && list.size() > 0 && list2.size() > 0) {
            showScanBarcodeDialog(list, list2);
        } else if (jenis == 1 && list.size() > 0 && list2.size() > 0) {
            showByEmailTrans(list, list2);
        } else if (jenis == 2 && list.size() > 0 && list2.size() > 0) {
            showByWhatsappTrans(list, list2);
        }
    }

    private void showScanBarcodeDialog(List<TwoItemModel> listItem, ArrayList<SettingKategoriKuponModel> listItem2) {

        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.popup_scan_barcode);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        isiSettingTotalBelanja = dialog.findViewById(R.id.isiSettingTotalBelanja);
        //radioGroupScanBarcode = dialog.findViewById(R.id.radioGrubScanBarcode);

        final Spinner spCaraBayarBarcode = (Spinner) dialog.findViewById(R.id.sp_cara_bayar_barcode);
        ArrayAdapter adapter = new ArrayAdapter(HomeActivity.this, R.layout.layout_simple_list, listItem);
        spCaraBayarBarcode.setAdapter(adapter);
        spCaraBayarBarcode.setSelection(0, true);

        final Spinner spKategoriBarcode = (Spinner) dialog.findViewById(R.id.sp_kategori_barcode);
        final TextView kategoriKupon = dialog.findViewById(R.id.kategoriKupon);

        if (session.getFlag().equals("2")) {
            spKategoriBarcode.setVisibility(View.GONE);
            kategoriKupon.setVisibility(View.GONE);
        } else {
            spKategoriBarcode.setVisibility(View.VISIBLE);
            kategoriKupon.setVisibility(View.VISIBLE);
        }

        ArrayAdapter adapter2 = new ArrayAdapter(HomeActivity.this, R.layout.layout_simple_list, listItem2);
        spKategoriBarcode.setAdapter(adapter2);
        spKategoriBarcode.setSelection(0, true);

        RelativeLayout btnSave = dialog.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IDscanBarcode = "0";
                TwoItemModel item = (TwoItemModel) spCaraBayarBarcode.getSelectedItem();
                IDscanBarcode = item.getItem1();

                kategoriScan = "0";
                SettingKategoriKuponModel kategori = (SettingKategoriKuponModel) spKategoriBarcode.getSelectedItem();
                kategoriScan = kategori.getId();

                if (!isiSettingTotalBelanja.getText().toString().equals("")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this)
                            .setTitle("Konfirmasi")
                            .setMessage("Apakah Anda yakin ingin menyimpan data?")
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    openScanBarcode();
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
                } else {
                    //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
                    isiSettingTotalBelanja.setError("Silahkan isi nominal belanja");
                    isiSettingTotalBelanja.requestFocus();
                    return;
                }
            }
        });
        RelativeLayout btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
//		dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showByEmailTrans(List<TwoItemModel> listItem, ArrayList<SettingKategoriKuponModel> listItem2) {

        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.popup_send_email);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // radioGroupEmail = dialog.findViewById(R.id.radioGrubEmail);
        isiEmailSendEmail = dialog.findViewById(R.id.isiEmailSendEmail);
        isiNominalSendEMail = dialog.findViewById(R.id.isiNominalSendMail);

        final Spinner spCaraBayarEmail = (Spinner) dialog.findViewById(R.id.sp_cara_bayar_email);
        ArrayAdapter adapter = new ArrayAdapter(HomeActivity.this, R.layout.layout_simple_list, listItem);
        spCaraBayarEmail.setAdapter(adapter);
        spCaraBayarEmail.setSelection(0, true);

        final Spinner spKategoriEmail = (Spinner) dialog.findViewById(R.id.sp_kategori_email);
        final TextView kategoriKupon = dialog.findViewById(R.id.kategoriKupon);

        if (session.getFlag().equals("2")) {
            spKategoriEmail.setVisibility(View.GONE);
            kategoriKupon.setVisibility(View.GONE);
        } else {
            spKategoriEmail.setVisibility(View.VISIBLE);
            kategoriKupon.setVisibility(View.VISIBLE);
        }

        ArrayAdapter adapter2 = new ArrayAdapter(HomeActivity.this, R.layout.layout_simple_list, listItem2);
        spKategoriEmail.setAdapter(adapter2);
        spKategoriEmail.setSelection(0, true);

        ArrayAdapter adapter3 = new ArrayAdapter(HomeActivity.this, R.layout.layout_simple_list, listItem2);
        SearchableSpinner spMerchant = dialog.findViewById(R.id.sp_merchant);
        spMerchant.setAdapter(adapter3);
        spMerchant.setTitle("Select Merchant");
        spMerchant.setPositiveButton("close");
        spMerchant.setSelection(0, true);

        RelativeLayout btnSave = dialog.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isiEmailSendEmail.getText().toString().equals("")) {
                    isiEmailSendEmail.setError("Silahkan mengisi email");
                    isiEmailSendEmail.requestFocus();
                    return;
                }

                if (isiNominalSendEMail.getText().toString().equals("")) {
                    isiNominalSendEMail.setError("Silahkan mengisi nominal belanja");
                    isiNominalSendEMail.requestFocus();
                    return;
                }

                IDemail = "0";
                TwoItemModel item = (TwoItemModel) spCaraBayarEmail.getSelectedItem();
                IDemail = item.getItem1();

                kategoriEmail = "0";
                SettingKategoriKuponModel kategori = (SettingKategoriKuponModel) spKategoriEmail.getSelectedItem();
                kategoriEmail = kategori.getId();

//                        Toast.makeText(getApplicationContext(),String.valueOf(IDemail),Toast.LENGTH_LONG).show();
                AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah Anda yakin ingin menyimpan data?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (session.getFlag().equals("2")){
                                    prepareDataSendEmailTenant();
                                }
                                else{
                                    prepareDataSendEmail();
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
        RelativeLayout btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
//		dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void showByWhatsappTrans(List<TwoItemModel> listItem, ArrayList<SettingKategoriKuponModel> listItem2) {

        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.popup_send_whatsapp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // radioGroupEmail = dialog.findViewById(R.id.radioGrubEmail);
        isiWhatsapp = dialog.findViewById(R.id.isiWhatsapp);
        isiNominalSendWhatsapp = dialog.findViewById(R.id.isiNominalSendWhatsapp);

        final Spinner spCaraBayarWhatsapp = (Spinner) dialog.findViewById(R.id.sp_cara_bayar_whatsapp);
        ArrayAdapter adapter = new ArrayAdapter(HomeActivity.this, R.layout.layout_simple_list, listItem);
        spCaraBayarWhatsapp.setAdapter(adapter);
        spCaraBayarWhatsapp.setSelection(0, true);

        final Spinner spKategoriWhatsapp = (Spinner) dialog.findViewById(R.id.sp_kategori_whatsapp);
        final TextView kategoriKupon = dialog.findViewById(R.id.kategoriKupon);

        if (session.getFlag().equals("2")) {
            spKategoriWhatsapp.setVisibility(View.GONE);
            kategoriKupon.setVisibility(View.GONE);
        } else {
            spKategoriWhatsapp.setVisibility(View.VISIBLE);
            kategoriKupon.setVisibility(View.VISIBLE);
        }

        ArrayAdapter adapter2 = new ArrayAdapter(HomeActivity.this, R.layout.layout_simple_list, listItem2);
        spKategoriWhatsapp.setAdapter(adapter2);
        spKategoriWhatsapp.setSelection(0, true);


        ArrayAdapter adapter3 = new ArrayAdapter(HomeActivity.this, R.layout.layout_simple_list, listItem2);
        SearchableSpinner spMerchant = dialog.findViewById(R.id.sp_merchant);
        spMerchant.setAdapter(adapter3);
        spMerchant.setTitle("Select Merchant");
        spMerchant.setPositiveButton("close");
        spMerchant.setSelection(0, true);

        RelativeLayout btnSave = dialog.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isiWhatsapp.getText().toString().equals("")) {
                    isiWhatsapp.setError("Silahkan mengisi nomor Whatsapp");
                    isiWhatsapp.requestFocus();
                    return;
                }

                if (isiNominalSendWhatsapp.getText().toString().equals("")) {
                    isiNominalSendWhatsapp.setError("Silahkan mengisi nominal belanja");
                    isiNominalSendWhatsapp.requestFocus();
                    return;
                }

                IDwhatsapp = "0";
                TwoItemModel item = (TwoItemModel) spCaraBayarWhatsapp.getSelectedItem();
                IDwhatsapp = item.getItem1();

                kategoriWhatsapp = "0";
                SettingKategoriKuponModel kategori = (SettingKategoriKuponModel) spKategoriWhatsapp.getSelectedItem();
                kategoriWhatsapp = kategori.getId();

//                        Toast.makeText(getApplicationContext(),String.valueOf(IDemail),Toast.LENGTH_LONG).show();
                AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah Anda yakin ingin menyimpan data?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (session.getFlag().equals("2")){
                                    prepareDataSendWhatsappTenant();
                                }
                                else{
                                    prepareDataSendWhatsapp();
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
        RelativeLayout btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
//        dialog.setCanceledOnTouchOutside(false);
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
        }
        else{
            prepareDataDashboard();
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

    private void prepareDataProfile() {
        new ApiVolley(HomeActivity.this, new JSONObject(), "GET", URL.urlProfile, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
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

    private void prepareDataSendEmail() {
        showProgressDialog();

        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("kepada", isiEmailSendEmail.getText());
            jBody.put("total", isiNominalSendEMail.getText());
            jBody.put("cara_bayar", String.valueOf(IDemail));
            jBody.put("id_kategori_kupon", String.valueOf(kategoriEmail));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(HomeActivity.this, jBody, "POST", URL.urlSendEmail, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    Log.d("hasil object email", object.toString());
                    final String status = object.getJSONObject("response").getString("status");
                    String message = object.getJSONObject("response").getString("message");
                    if (status.equals("200")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (session.getFlag().equals("2")){
                    prepareDataDashboardTenant();
                }
                else{
                    prepareDataDashboard();
                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                dismissProgressDialog();
                if (session.getFlag().equals("2")){
                    prepareDataDashboardTenant();
                }
                else{
                    prepareDataDashboard();
                }
            }
        });
    }

    private void prepareDataSendEmailTenant() {
        showProgressDialog();

        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("kepada", isiEmailSendEmail.getText());
            jBody.put("total", isiNominalSendEMail.getText());
            jBody.put("cara_bayar", String.valueOf(IDemail));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(HomeActivity.this, jBody, "POST", URL.urlSendEmailTenant, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    Log.d("hasil object email", object.toString());
                    final String status = object.getJSONObject("response").getString("status");
                    String message = object.getJSONObject("response").getString("message");
                    if (status.equals("200")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (session.getFlag().equals("2")){
                    prepareDataDashboardTenant();
                }
                else{
                    prepareDataDashboard();
                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                dismissProgressDialog();
                if (session.getFlag().equals("2")){
                    prepareDataDashboardTenant();
                }
                else{
                    prepareDataDashboard();
                }
            }
        });
    }

    private void prepareDataSendWhatsapp() {
        showProgressDialog();

        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("nomor", isiWhatsapp.getText());
            jBody.put("total_bayar", isiNominalSendWhatsapp.getText());
            jBody.put("cara_bayar", String.valueOf(IDwhatsapp));
            jBody.put("id_kategori_kupon", String.valueOf(kategoriWhatsapp));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(HomeActivity.this, jBody, "POST", URL.urlSendWhatsapp, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    } else if (message.equals("OTP")) {

                        final Dialog dialog = new Dialog(HomeActivity.this);
                        dialog.setContentView(R.layout.popup_otp);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        final EditText otp = dialog.findViewById(R.id.edt_otp);
                        TextView request = dialog.findViewById(R.id.txt_request);
                        Button check = dialog.findViewById(R.id.btn_check);

                        check.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final JSONObject jBody = new JSONObject();
                                try {
                                    jBody.put("no_telp", isiWhatsapp.getText());
                                    jBody.put("kode_otp", otp.getText());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                new ApiVolley(HomeActivity.this, jBody, "POST", URL.urlKuponCheckOTP, "", "", 0, new ApiVolley.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(result);
//                                            Log.d("hasil check", jsonObject.toString());

                                            String status = jsonObject.getJSONObject("metadata").getString("status");
                                            String message = jsonObject.getJSONObject("metadata").getString("message");
                                            if (status.equals("200")) {
                                                dialog.dismiss();
                                                prepareDataSendWhatsapp();
                                            } else {
                                                Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
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
                        });

                        request.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final JSONObject jBody = new JSONObject();
                                try {
                                    jBody.put("no_telp", isiWhatsapp.getText());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                new ApiVolley(HomeActivity.this, jBody, "POST", URL.urlKuponRequestOTP, "", "", 0, new ApiVolley.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(result);
//                                            Log.d("hasil request", jsonObject.toString());

                                            String status = jsonObject.getJSONObject("metadata").getString("status");
                                            String message = jsonObject.getJSONObject("metadata").getString("message");
                                            if (status.equals("200")) {
                                                Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
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
                        });
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (session.getFlag().equals("2")){
                    prepareDataDashboardTenant();
                }
                else{
                    prepareDataDashboard();
                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                dismissProgressDialog();
                if (session.getFlag().equals("2")){
                    prepareDataDashboardTenant();
                }
                else{
                    prepareDataDashboard();
                }
            }
        });
    }

    private void prepareDataSendWhatsappTenant() {
        showProgressDialog();

        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("nomor", isiWhatsapp.getText());
            jBody.put("total_bayar", isiNominalSendWhatsapp.getText());
            jBody.put("cara_bayar", String.valueOf(IDwhatsapp));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(HomeActivity.this, jBody, "POST", URL.urlSendWhatsappTenant, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    } else if (message.equals("OTP")) {

                        final Dialog dialog = new Dialog(HomeActivity.this);
                        dialog.setContentView(R.layout.popup_otp);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        final EditText otp = dialog.findViewById(R.id.edt_otp);
                        TextView request = dialog.findViewById(R.id.txt_request);
                        Button check = dialog.findViewById(R.id.btn_check);

                        check.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final JSONObject jBody = new JSONObject();
                                try {
                                    jBody.put("no_telp", isiWhatsapp.getText());
                                    jBody.put("kode_otp", otp.getText());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                new ApiVolley(HomeActivity.this, jBody, "POST", URL.urlKuponCheckOTP, "", "", 0, new ApiVolley.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(result);
//                                            Log.d("hasil check", jsonObject.toString());

                                            String status = jsonObject.getJSONObject("metadata").getString("status");
                                            String message = jsonObject.getJSONObject("metadata").getString("message");
                                            if (status.equals("200")) {
                                                dialog.dismiss();
                                                prepareDataSendWhatsappTenant();
                                            } else {
                                                Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
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
                        });

                        request.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final JSONObject jBody = new JSONObject();
                                try {
                                    jBody.put("no_telp", isiWhatsapp.getText());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                new ApiVolley(HomeActivity.this, jBody, "POST", URL.urlKuponRequestOTP, "", "", 0, new ApiVolley.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(result);
//                                            Log.d("hasil request", jsonObject.toString());

                                            String status = jsonObject.getJSONObject("metadata").getString("status");
                                            String message = jsonObject.getJSONObject("metadata").getString("message");
                                            if (status.equals("200")) {
                                                Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(HomeActivity.this, message, Toast.LENGTH_SHORT).show();
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
                        });
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (session.getFlag().equals("2")){
                    prepareDataDashboardTenant();
                }
                else{
                    prepareDataDashboard();
                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                dismissProgressDialog();
                if (session.getFlag().equals("2")){
                    prepareDataDashboardTenant();
                }
                else{
                    prepareDataDashboard();
                }
            }
        });
    }

//    private void prepareDataSettingKupon() {
//
//        showProgressDialog();
//        final JSONObject jBody = new JSONObject();
//        try {
//            jBody.put("kupon", isiSettingKupon.getText());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        ApiVolley request = new ApiVolley(HomeActivity.this, jBody, "POST", URL.urlSettingKupon, "", "", 0, new ApiVolley.VolleyCallback() {
//            @Override
//            public void onSuccess(String result) {
//
//                dismissProgressDialog();
//                try {
//                    JSONObject object = new JSONObject(result);
//                    final String status = object.getJSONObject("response").getString("status");
//                    String message = object.getJSONObject("response").getString("message");
//                    if (status.equals("1")) {
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                        /*Intent i = new Intent(HomeActivity.this,HomeActivity.class);
//                        startActivity(i);
//                        finish();*/
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                prepareDataDashboard();
//
//            }
//
//            @Override
//            public void onError(String result) {
//                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
//                dismissProgressDialog();
//                prepareDataDashboard();
//            }
//        });
//    }

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
                    if (session.getFlag().equals("2")){
                        prepareDataScanBarcodeTenant();
                    }
                    else if(session.getFlag().equals("1")){
                        prepareDataScanBarcode();
                    }
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

                //Toast.makeText(getApplicationContext(), "gagal brooh", Toast.LENGTH_LONG).show();
            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void prepareDataScanBarcode() {
        showProgressDialog();

        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("user_id", resultScanBarcode.getContents());
            jBody.put("total", isiSettingTotalBelanja.getText().toString());
            jBody.put("cara_bayar", IDscanBarcode);
            jBody.put("id_kategori_kupon", String.valueOf(kategoriScan));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(HomeActivity.this, jBody, "POST", URL.urlScanBarcodeMerchant, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("response").getString("status");
                    String message = object.getJSONObject("response").getString("message");
                    if (status.equals("1")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        JSONObject detail = object.getJSONObject("response");

                        Intent i = new Intent(HomeActivity.this, BerhasilQrCodeActivity.class);
                        i.putExtra("nama", detail.getString("nama"));
                        i.putExtra("email", detail.getString("email"));
                        i.putExtra("telpon", detail.getString("no_telp"));
                        i.putExtra("gambar", detail.getString("foto"));
                        i.putExtra("jumlah_kupon", detail.getString("jumlah_kupon"));
                        startActivity(i);
                        finish();

                        isHome = false;
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (session.getFlag().equals("2")){
                    prepareDataDashboardTenant();
                }
                else{
                    prepareDataDashboard();
                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                dismissProgressDialog();
                if (session.getFlag().equals("2")){
                    prepareDataDashboardTenant();
                }
                else{
                    prepareDataDashboard();
                }
            }
        });
    }

    private void prepareDataScanBarcodeTenant() {
        showProgressDialog();

        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("user_id", resultScanBarcode.getContents());
            jBody.put("total_bayar", isiSettingTotalBelanja.getText().toString());
            jBody.put("cara_bayar", IDscanBarcode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(HomeActivity.this, jBody, "POST", URL.urlScanBarcodeTenant, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("response").getString("status");
                    String message = object.getJSONObject("response").getString("message");
                    if (status.equals("1")) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        JSONObject detail = object.getJSONObject("response");

                        Intent i = new Intent(HomeActivity.this, BerhasilQrCodeActivity.class);
                        i.putExtra("nama", detail.getString("nama"));
                        i.putExtra("email", detail.getString("email"));
                        i.putExtra("telpon", detail.getString("no_telp"));
                        i.putExtra("gambar", detail.getString("foto"));
                        i.putExtra("jumlah_kupon", detail.getString("jumlah_kupon"));
                        startActivity(i);
                        finish();

                        isHome = false;
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (session.getFlag().equals("2")){
                    prepareDataDashboardTenant();
                }
                else{
                    prepareDataDashboard();
                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                dismissProgressDialog();
                if (session.getFlag().equals("2")){
                    prepareDataDashboardTenant();
                }
                else{
                    prepareDataDashboard();
                }
            }
        });
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
