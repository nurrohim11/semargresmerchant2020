package gmedia.net.id.semargres2020merchant.voucher;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.URL;

public class SettingVoucherActivity extends AppCompatActivity {

    private EditText edtJumlahVoucher, edtNamaVoucher, edtNilaiVoucher;
    private TextView startDate, endDate;
    private Spinner spinner;
    private SimpleDateFormat dateFormatUpload, dateFormatShow;
    private ProgressDialog progressDialog;
    private Integer hourStart, hourEnd, minuteStart, minuteEnd, dayStart, dayEnd, monthStart, monthEnd, yearStart, yearEnd;
    private String startUpload, endUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_voucher);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.statusbar));
        }

        hourStart = 0;
        hourEnd = 0;
        minuteStart = 0;
        minuteEnd = 0;
        dayStart = 0;
        dayEnd = 0;
        monthStart = 0;
        monthEnd = 0;
        yearStart = 0;
        yearEnd = 0;

        RelativeLayout back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        edtJumlahVoucher = findViewById(R.id.edtSoalKuis);
        edtNamaVoucher = findViewById(R.id.edtNamaVoucher);
        edtNilaiVoucher = findViewById(R.id.edtNilaiVoucher);

        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);

        ImageView imgStart = findViewById(R.id.imgStartDate);
        ImageView imgEnd = findViewById(R.id.imgEndDate);

        spinner = findViewById(R.id.sp_pilihan_voucher);

        Button btnSave = findViewById(R.id.saveVoucher);

        dateFormatUpload = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateFormatShow = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        List<String> pilihan = new ArrayList<>();
        pilihan.add("Persen");
        pilihan.add("Rupiah");

        imgStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCalendarFirst();
            }
        });

        imgEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCalendarEnd();
            }
        });

        ArrayAdapter adapter = new ArrayAdapter(SettingVoucherActivity.this, R.layout.layout_simple_list, pilihan);
        spinner.setAdapter(adapter);
        spinner.setSelection(0, true);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((hourEnd <= hourStart && minuteEnd <= minuteStart) &&
                        (yearEnd <= yearStart && monthEnd <= monthStart && dayEnd <= dayStart)) {

                    Toast.makeText(SettingVoucherActivity.this, "Coba ulangi lagi saat memasukkan periode", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (edtJumlahVoucher.getText().toString().equals("")) {
                    edtJumlahVoucher.setError("Silahkan mengisi jumlah voucher");
                    edtJumlahVoucher.requestFocus();
                    return;
                }


                if (edtNilaiVoucher.getText().toString().equals("")) {
                    edtNilaiVoucher.setError("Silahkan mengisi nilai voucher");
                    edtNilaiVoucher.requestFocus();
                    return;
                }

                AlertDialog alertDialog = new AlertDialog.Builder(SettingVoucherActivity.this)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah Anda yakin ingin menyimpan data?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveVoucher();
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
    }

    private void getCalendarFirst() {
        final Calendar mCurrentCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(SettingVoucherActivity.this,
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

                        TimePickerDialog timePickerDialog = new TimePickerDialog(SettingVoucherActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                        hourStart = hour;
                                        minuteStart = minute;

                                        String waktu = String.format(Locale.getDefault(),
                                                "%02d:%02d", hour, minute);
                                        String string = tanggalShow + " " + waktu;
                                        String string2 = tanggalUpload + " " + waktu;
                                        startDate.setText(string);
                                        startUpload = string2;
                                    }
                                }, mCurrentCalendar.get(Calendar.HOUR_OF_DAY),
                                mCurrentCalendar.get(Calendar.MINUTE),
                                DateFormat.is24HourFormat(SettingVoucherActivity.this));
                        timePickerDialog.show();
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(SettingVoucherActivity.this,
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

                        TimePickerDialog timePickerDialog = new TimePickerDialog(SettingVoucherActivity.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                        hourEnd = hour;
                                        minuteEnd = minute;

                                        if ((hourEnd <= hourStart && minuteEnd <= minuteStart) &&
                                                (yearEnd <= yearStart && monthEnd <= monthStart && dayEnd <= dayStart)) {

                                            getCalendarEnd();
                                            Toast.makeText(SettingVoucherActivity.this, "Coba ulangi lagi", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        String waktu = String.format(Locale.getDefault(),
                                                "%02d:%02d", hour, minute);
                                        String string = tanggalShow + " " + waktu;
                                        String string2 = tanggalUpload + " " + waktu;
                                        endDate.setText(string);
                                        endUpload = string2;
                                    }
                                }, mCurrentCalendar.get(Calendar.HOUR_OF_DAY),
                                mCurrentCalendar.get(Calendar.MINUTE),
                                DateFormat.is24HourFormat(SettingVoucherActivity.this));
                        timePickerDialog.show();
                    }
                }, mCurrentCalendar.get(Calendar.YEAR),
                mCurrentCalendar.get(Calendar.MONTH),
                mCurrentCalendar.get(Calendar.DAY_OF_MONTH));
        long minDate = new Date().getTime();
        datePickerDialog.getDatePicker().setMinDate(minDate);
        datePickerDialog.show();
    }

    private void saveVoucher() {
        String pilihanSp = (String) spinner.getSelectedItem();
        String pilihanSpinner;
        if (pilihanSp.equals("Persen")) {
            pilihanSpinner = "P";
        } else {
            pilihanSpinner = "R";
        }

        JSONObject jBody = new JSONObject();
        try {
            jBody.put("jumlah", Integer.parseInt(edtJumlahVoucher.getText().toString()));
            jBody.put("nama_voucher", edtNamaVoucher.getText());
            jBody.put("valid_start", startUpload);
            jBody.put("valid_end", endUpload);
            jBody.put("tipe", pilihanSpinner);
            jBody.put("nominal", Double.parseDouble(edtNilaiVoucher.getText().toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showProgressDialog();
        new ApiVolley(SettingVoucherActivity.this, jBody, "POST", URL.urlStoreVoucher, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    dismissProgressDialog();
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
//                        Log.d("hasil voucher", message);
                        /*Intent i  = new Intent(SettingVoucherActivity.this, VoucherActivity.class);
                        startActivity(i);
                        finish();*/
                        onBackPressed();

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
                dismissProgressDialog();
            }
        });
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(SettingVoucherActivity.this,
                R.style.AppTheme_Custom_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setMessage("Menyimpan...");
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
