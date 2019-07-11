package gmedia.net.id.semargres2019merchant.kuis;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import gmedia.net.id.semargres2019merchant.R;
import gmedia.net.id.semargres2019merchant.util.ApiVolley;
import gmedia.net.id.semargres2019merchant.util.URL;

public class CreateQuizActivity extends AppCompatActivity {

    private EditText soal, hadiah, term_condition;
    private TextView periode_start, periode_end;
    private SimpleDateFormat dateFormatUpload, dateFormatShow;
    private Integer dayStart, dayEnd, monthStart, monthEnd, yearStart, yearEnd;
    private String startUpload, endUpload;
    private ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar));
        }

        soal = findViewById(R.id.edtSoalKuis);
        hadiah = findViewById(R.id.edtHadiahKuis);
        term_condition = findViewById(R.id.edtKeteranganKuis);

        periode_start = findViewById(R.id.startDate);
        periode_end = findViewById(R.id.endDate);

        ImageView imgStart = findViewById(R.id.imgStartDate);
        ImageView imgEnd = findViewById(R.id.imgEndDate);

        dateFormatUpload = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateFormatShow = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        imgStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCalendarFirst();
            }
        });

        imgEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCalendarEnd();
            }
        });

        Button btnSave = findViewById(R.id.saveKuis);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(CreateQuizActivity.this)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah Anda yakin ingin menyimpan data?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveQuiz();
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.color_grey_new));

            }
        });

        RelativeLayout back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getCalendarFirst() {
        final Calendar mCurrentCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateQuizActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        yearStart = year;
                        monthStart = month;
                        dayStart = day;

                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, month, day);
                        final String tanggalUpload = dateFormatUpload.format(newDate.getTime());
                        final String tanggalShow = dateFormatShow.format(newDate.getTime());

                        periode_start.setText(tanggalShow);
                        startUpload = tanggalUpload;
                    }
                }, mCurrentCalendar.get(Calendar.YEAR),
                mCurrentCalendar.get(Calendar.MONTH),
                mCurrentCalendar.get(Calendar.DAY_OF_MONTH));
        long minDate = new Date().getTime();
        datePickerDialog.getDatePicker().setMinDate(minDate);
        datePickerDialog.show();
    }

    private void getCalendarEnd() {
        final Calendar mCurrentCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(CreateQuizActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        yearEnd = year;
                        monthEnd = month;
                        dayEnd = day;

                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, month, day);
                        final String tanggalUpload = dateFormatUpload.format(newDate.getTime());
                        final String tanggalShow = dateFormatShow.format(newDate.getTime());

//                        if(yearEnd <= yearStart && monthEnd <= monthStart && dayEnd <= dayStart){
//                            getCalendarEnd();
//                            Toast.makeText(CreateQuizActivity.this, "Coba ulangi lagi", Toast.LENGTH_SHORT).show();
//                            return;
//                        }

                        periode_end.setText(tanggalShow);
                        endUpload = tanggalUpload;
                    }
                }, mCurrentCalendar.get(Calendar.YEAR),
                mCurrentCalendar.get(Calendar.MONTH),
                mCurrentCalendar.get(Calendar.DAY_OF_MONTH));
        long minDate = new Date().getTime();
        datePickerDialog.getDatePicker().setMinDate(minDate);
        datePickerDialog.show();
    }

    private void saveQuiz() {

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("soal", soal.getText());
            jBody.put("periode_start", startUpload);
            jBody.put("periode_end", endUpload);
            jBody.put("hadiah", hadiah.getText());
            jBody.put("term_condition", term_condition.getText());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog();
        new ApiVolley(CreateQuizActivity.this, jBody, "POST", URL.urlCreateQuiz, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    dismissProgressDialog();
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//                        Log.d("hasil upload", object.toString());
                        Intent i = new Intent(CreateQuizActivity.this, KuisActivity.class);
                        startActivity(i);
                        finish();
                    } else {
//                        Log.d("hasil upload gagal", object.toString());
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(CreateQuizActivity.this,
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
