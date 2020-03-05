package gmedia.net.id.semargres2020merchant;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.SessionManager;
import gmedia.net.id.semargres2020merchant.util.URL;

public class LoginActivity extends AppCompatActivity {
    SessionManager session;
    EditText username, password, isianEmail;
    Boolean visible = true;
    private boolean doubleBackToExitPressedOnce = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar));
        }

        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        username = findViewById(R.id.edt_username);
        password = findViewById(R.id.edt_password);

//        ConstraintLayout utama = findViewById(R.id.layout_login);
//        utama.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                HideKeyboard.hideSoftKeyboard(LoginActivity.this);
//            }
//        });

        final ImageView btnVisible = findViewById(R.id.logo_visible);
        btnVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (visible) {
                    btnVisible.setImageDrawable(getResources().getDrawable(R.drawable.visible_red));
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    visible = false;
                } else {
                    btnVisible.setImageDrawable(getResources().getDrawable(R.drawable.invisible_red));
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    visible = true;
                }
            }
        });

        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = username.getText().toString();
                String pass = password.getText().toString();

                if (nama.trim().length() > 0 && pass.trim().length() > 0) {
                    loginRequest();
                } else {
                    final Dialog dialog = new Dialog(LoginActivity.this);
                    dialog.setContentView(R.layout.popup_enter_username_password);
                    RelativeLayout close = dialog.findViewById(R.id.closeenterusernamepassword);
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();

                    final Handler handler = new Handler();
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    };

                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            handler.removeCallbacks(runnable);
                        }
                    });

                    handler.postDelayed(runnable, 3000);
                }
            }
        });

        TextView forgot = findViewById(R.id.tv_forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.setContentView(R.layout.popup_forgot_password);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                isianEmail = dialog.findViewById(R.id.isianEmailForgotPassword);

                RelativeLayout save = dialog.findViewById(R.id.btnSaveForgotPassword);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prepareResetPassword();
                        dialog.dismiss();
                    }
                });

                RelativeLayout cancel = dialog.findViewById(R.id.btnCancelForgotPassword);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });
    }

    private void loginRequest() {
        showProgressDialog();

        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("username", username.getText().toString());
            jBody.put("password", password.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(LoginActivity.this, jBody, "POST", URL.urlLogin, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d(">>>>>>>>",result);
                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    Log.d(">>>>>", String.valueOf(object.getJSONObject("metadata").getString("status")));
                    final String status = object.getJSONObject("metadata").getString("status");
                    if (status.equals("200")) {
                        String token = object.getJSONObject("response").getString("token");
                        String id = object.getJSONObject("response").getString("id");
                        String nama = object.getJSONObject("response").getString("username");
                        String flag = object.getJSONObject("response").getString("flag");

                        session.createLoginSession(nama, nama, token, id, nama, flag);

                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        final Dialog dialog = new Dialog(LoginActivity.this);
                        dialog.setContentView(R.layout.popup_incorrect_username_password);

                        RelativeLayout close = dialog.findViewById(R.id.closeincorectusernamepassword);
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();

                        final Handler handler = new Handler();
                        final Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        };

                        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                handler.removeCallbacks(runnable);
                            }
                        });

                        handler.postDelayed(runnable, 3000);
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

    private void prepareResetPassword() {
        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("kepada", isianEmail.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ApiVolley(LoginActivity.this, jBody, "POST", URL.urlResetPassword, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("response").getString("status");
                    String message = object.getJSONObject("response").getString("message");
                    if (status.equals("1")) {
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


    private void showProgressDialog() {
        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Custom_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setMessage("Harap tunggu...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
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
