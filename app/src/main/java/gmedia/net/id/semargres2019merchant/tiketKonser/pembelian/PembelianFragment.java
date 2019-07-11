package gmedia.net.id.semargres2019merchant.tiketKonser.pembelian;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.DialogFactory;
import com.leonardus.irfan.JSONBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import gmedia.net.id.semargres2019merchant.R;
import gmedia.net.id.semargres2019merchant.tiketKonser.GambarDenahActivity;
import gmedia.net.id.semargres2019merchant.tiketKonser.KonserActivity;
import gmedia.net.id.semargres2019merchant.util.ApiVolley;
import gmedia.net.id.semargres2019merchant.util.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class PembelianFragment extends Fragment {


    public PembelianFragment() {
        // Required empty public constructor
    }

    private Activity activity;
    private List<TiketKonserModel> listJenisTiket = new ArrayList<>();
    private ArrayAdapter<TiketKonserModel> adapter;

    private int selected_jenis = -1;

    private ImageView img_konser;
    private EditText txt_email, txt_kodePromo, txt_whatsapp, txt_nik, txt_nama, txt_referral;
    private TextView txt_diskon, txt_total, txt_denah, lbl_jenis_tiket;
    private AppCompatSpinner spn_jumlah;

    private AppCompatSpinner spn_jenis;

    private Double diskon = 0.0;

    private ProgressDialog progressDialog;

    private String qrCode = "";
    private String denah = "";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        View view =  inflater.inflate(R.layout.fragment_pembelian, container, false);

        spn_jenis = view.findViewById(R.id.spn_jenis);
        img_konser = view.findViewById(R.id.img_konser);
        spn_jumlah = view.findViewById(R.id.spn_jumlah);
        txt_email = view.findViewById(R.id.txt_emailTiket);
        txt_kodePromo = view.findViewById(R.id.txt_kodePromo);
        txt_whatsapp = view.findViewById(R.id.txt_whatsapp);
        txt_diskon = view.findViewById(R.id.txt_diskonPromo);
        txt_nik = view.findViewById(R.id.txt_NIK);
        lbl_jenis_tiket = view.findViewById(R.id.lbl_jenis_tiket);
        txt_nama = view.findViewById(R.id.txt_namaTiket);
        txt_referral = view.findViewById(R.id.txt_referral);
        txt_total = view.findViewById(R.id.txt_total);
        txt_total.setText(Converter.doubleToRupiah(0));

        String editable = ((KonserActivity) Objects.requireNonNull(getActivity())).editable;
        qrCode = ((KonserActivity) Objects.requireNonNull(getActivity())).qrCode;

        if(editable.equals("false")){
            getEmail();
            txt_email.setEnabled(false);
            txt_nama.setEnabled(false);
            txt_nik.setEnabled(false);
        }
        else{
            txt_email.setEnabled(true);
            txt_nama.setEnabled(true);
            txt_nik.setEnabled(true);
        }

        getKonser();

        spn_jenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_jenis = position;
                if(spn_jumlah.getSelectedItemPosition() < 0){
                    txt_total.setText(Converter.doubleToRupiah(0));
                }
                else{

//                    txt_total.setText(Converter.doubleToRupiah(
//                            listJenisTiket.get(selected_jenis).getHarga() *
//                                    Integer.parseInt(txt_jumlah.getText().toString())));

                    txt_total.setText(Converter.doubleToRupiah(
                            (listJenisTiket.get(selected_jenis).getHarga() *
                                    (spn_jumlah.getSelectedItemPosition() + 1)) -
                                    (listJenisTiket.get(selected_jenis).getHarga() *
                                            (spn_jumlah.getSelectedItemPosition() + 1) *
                                            diskon)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        view.findViewById(R.id.btn_cekKode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cekKodePromo();
            }
        });

        view.findViewById(R.id.btn_beli).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txt_nama.getText().toString().equals("")) {
                    txt_nama.setError("Silahkan mengisi nama");
                    txt_nama.requestFocus();
                    return;
                }

                if (txt_nik.getText().toString().equals("")) {
                    txt_nik.setError("Silahkan mengisi NIK");
                    txt_nik.requestFocus();
                    return;
                }

                if (txt_email.getText().toString().equals("")) {
                    txt_email.setError("Silahkan mengisi email");
                    txt_email.requestFocus();
                    return;
                }


                if(spn_jumlah.getSelectedItemPosition() < 0){
                    Toast.makeText(getActivity(), "Jumlah tiket belum terisi", Toast.LENGTH_SHORT).show();
                    spn_jumlah.requestFocus();

//                    Toast.makeText(getActivity(), "Jumlah tiket tidak boleh kosong", Toast.LENGTH_SHORT).show();

                }
                else if(selected_jenis == -1){
                    Toast.makeText(getActivity(), "Jenis tiket belum dipilih", Toast.LENGTH_SHORT).show();
                }
                else{
                    prepayment();
                }
            }
        });

        spn_jumlah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(selected_jenis != -1){
                    txt_total.setText(Converter.doubleToRupiah(
                            (listJenisTiket.get(selected_jenis).getHarga() *
                                    (spn_jumlah.getSelectedItemPosition() + 1)) -
                                    (listJenisTiket.get(selected_jenis).getHarga() *
                                            (spn_jumlah.getSelectedItemPosition() + 1) *
                                            diskon) ));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txt_denah = view.findViewById(R.id.txt_denah);
        txt_denah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!denah.isEmpty()){
                    Intent i = new Intent(getActivity(), GambarDenahActivity.class);
                    i.putExtra("denah", denah);
                    startActivity(i);
                }
                else{
                    Toast.makeText(getActivity(), "Data denah belum termuat", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void prepayment(){
        AppLoading.getInstance().showLoading(activity);
        JSONBuilder body = new JSONBuilder();
        body.add("email", txt_email.getText().toString());
        body.add("id_jenis_tiket", listJenisTiket.get(selected_jenis).getId());
        body.add("jumlah", spn_jumlah.getSelectedItemPosition() + 1);
        body.add("kode_promo", txt_kodePromo.getText().toString());
        body.add("nomor_wa", txt_whatsapp.getText().toString());
        body.add("nama", txt_nama.getText().toString());
        body.add("nik", txt_nik.getText().toString());
        body.add("kode_referral", txt_referral.getText().toString());

        ApiVolleyManager.getInstance().addSecureRequest(activity, URL.urlPrepayment, ApiVolleyManager.METHOD_POST,
                URL.getHeaders(activity), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            Log.d(URL.TAG, result);
                            JSONObject konfirmasi = new JSONObject(result);
                            showKonfirmasi(konfirmasi.getDouble("total_payment"));
                        }
                        catch (JSONException e){
                            Toast.makeText(activity, "Terjadi kesalahan data", Toast.LENGTH_SHORT).show();
                            Log.e(URL.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    private void showKonfirmasi(double pembayaran){
        final Dialog dialog = DialogFactory.getInstance().createDialog(activity,
                R.layout.popup_konfirmasi_beli_tiket, 85, 55);

        TextView txt_dialog_email, txt_dialog_jenis, txt_dialog_jumlah, txt_dialog_total;
        txt_dialog_email = dialog.findViewById(R.id.txt_dialog_email);
        txt_dialog_jenis = dialog.findViewById(R.id.txt_dialog_jenis);
        txt_dialog_jumlah = dialog.findViewById(R.id.txt_dialog_jumlah);
        txt_dialog_total = dialog.findViewById(R.id.txt_dialog_total);

        String email = " : " + txt_email.getText().toString();
        txt_dialog_email.setText(email);
        String jenis = " : " + listJenisTiket.get(selected_jenis).getNama();
        txt_dialog_jenis.setText(jenis);
        String jumlah = " : " + (spn_jumlah.getSelectedItemPosition() + 1) + " Tiket";
        txt_dialog_jumlah.setText(jumlah);
        String total = " : " + Converter.doubleToRupiah(pembayaran);
        txt_dialog_total.setText(total);

        dialog.findViewById(R.id.btn_ya).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(qrCode.equals("")){
                    beliTiketEmail();
                }
                else{
                    beliTiketScanQR();
                }

                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btn_batal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void getEmail(){
        showProgressDialog();
        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("qr_data", qrCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(getActivity(), jBody, "POST", URL.urlGetEmail, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    Log.d("hasil email", object.toString());
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        txt_email.setText(object.getJSONObject("response").getString("email"));
                        txt_nama.setText(object.getJSONObject("response").getString("nama"));
                        txt_nik.setText(object.getJSONObject("response").getString("nik"));
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                dismissProgressDialog();
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getKonser(){
        new ApiVolley(getContext(), new JSONObject(), "GET", URL.urlGetKonser,
                "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    Log.d(URL.TAG, result);
                    JSONObject object = new JSONObject(result);
//                    Log.d("hasil konser", object.toString());
                    final String status = object.getJSONObject("metadata").getString("status");
                    final String message = object.getJSONObject("metadata").getString("message");

                    if(status.equals("200")){
                        JSONObject konser = object.getJSONObject("response").getJSONObject("konser");
                        Picasso.with(getActivity()).load(konser.getString("gambar")).into(img_konser);
                        denah = konser.getString("denah");

                        String jenis_tiket = "Jenis Tiket (" + object.
                                getJSONObject("response").getString("kategori_tiket") + ")";
                        lbl_jenis_tiket.setText(jenis_tiket);

                        listJenisTiket.clear();

                        JSONArray jenisTiket = object.getJSONObject("response").getJSONArray("jenis_tiket");
                        for (int i = 0; i < jenisTiket.length(); i++) {
                            JSONObject isi = jenisTiket.getJSONObject(i);
                            if(isi.getInt("status_sold_out")==0){
                                listJenisTiket.add(new TiketKonserModel(
                                        isi.getString("id"),
                                        isi.getString("nama"),
                                        Double.parseDouble(isi.getString("harga")),
                                        Boolean.parseBoolean(isi.getString("status_sold_out"))
                                ));
                            }

                        }
                        adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, listJenisTiket);
                        spn_jenis.setAdapter(adapter);
                    }
                    else{
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cekKodePromo(){
        showProgressDialog();
        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("email", txt_email.getText().toString());
            jBody.put("id_jenis_tiket", listJenisTiket.get(selected_jenis).getId());
            jBody.put("jumlah", spn_jumlah.getSelectedItemPosition() + 1);
            jBody.put("kode_promo", txt_kodePromo.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(getActivity(), jBody, "POST", URL.urlCekPromo, "",
                "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    Log.d("hasil check", object.toString());
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        String persenDiskon = object.getJSONObject("response").getString("discount_percent");
                        diskon = Double.parseDouble(persenDiskon) / 100;

                        String diskonTampil = persenDiskon + "%";
                        txt_diskon.setText(diskonTampil);

                        if(selected_jenis != -1){
                            if(spn_jumlah.getSelectedItemPosition() < 0){
                                txt_total.setText(Converter.doubleToRupiah(0));
                            }
                            else{
                                txt_total.setText(Converter.doubleToRupiah(
                                        (listJenisTiket.get(selected_jenis).getHarga() *
                                                (spn_jumlah.getSelectedItemPosition() + 1)) -
                                                (listJenisTiket.get(selected_jenis).getHarga() *
                                                        (spn_jumlah.getSelectedItemPosition() + 1) *
                                                        diskon) ));
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                dismissProgressDialog();
                Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void beliTiketEmail(){
        showProgressDialog();
        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("email", txt_email.getText().toString());
            jBody.put("id_jenis_tiket", listJenisTiket.get(selected_jenis).getId());
            jBody.put("jumlah", spn_jumlah.getSelectedItemPosition() + 1);
            jBody.put("kode_promo", txt_kodePromo.getText().toString());
            jBody.put("nomor_wa", txt_whatsapp.getText().toString());
            jBody.put("nama", txt_nama.getText().toString());
            jBody.put("nik", txt_nik.getText().toString());
            jBody.put("kode_referral", txt_referral.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(getActivity(), jBody, "POST", URL.urlTiketEmail, "",
                "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        showBerhasil(txt_email.getText().toString(), txt_total.getText().toString(),
                                listJenisTiket.get(selected_jenis).getNama(), String.valueOf(spn_jumlah.getSelectedItemPosition() + 1));
                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        showGagal();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                dismissProgressDialog();
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                showGagal();
            }
        });
    }

    private void beliTiketScanQR(){
        showProgressDialog();
        final JSONObject jBody = new JSONObject();
        try {
            jBody.put("qr_data", qrCode);
            jBody.put("email", txt_email.getText().toString());
            jBody.put("id_jenis_tiket", listJenisTiket.get(selected_jenis).getId());
            jBody.put("jumlah", spn_jumlah.getSelectedItemPosition() + 1);
            jBody.put("kode_promo", txt_kodePromo.getText().toString());
            jBody.put("nomor_wa", txt_whatsapp.getText().toString());
            jBody.put("nama", txt_nama.getText().toString());
            jBody.put("nik", txt_nik.getText().toString());
            jBody.put("kode_referral", txt_referral.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        new ApiVolley(getActivity(), jBody, "POST", URL.urlTiketScanQR,
                "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        showBerhasil(txt_email.getText().toString(), txt_total.getText().toString(),
                                listJenisTiket.get(selected_jenis).getNama(), String.valueOf(spn_jumlah.getSelectedItemPosition() + 1));
                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        showGagal();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                dismissProgressDialog();
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                showGagal();
            }
        });
    }

    private void showBerhasil(String email, String total, String jenis, String jumlah){
        final Dialog dialog = DialogFactory.getInstance().createDialog(getActivity(), R.layout.popup_tiket_berhasil,
                80, 60);

        TextView txt_email = dialog.findViewById(R.id.txt_email);
        txt_email.setText(email);

        TextView txt_jenis = dialog.findViewById(R.id.txt_jenisTiket);
        txt_jenis.setText(jenis);

        TextView txt_total = dialog.findViewById(R.id.txt_total);
        txt_total.setText(total);

        TextView txt_jumlah = dialog.findViewById(R.id.txt_jumlahTiket);
        txt_jumlah.setText(jumlah);

        Button btn_ok = dialog.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                try {
                    getActivity().onBackPressed();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }

    private void showGagal(){
        final Dialog dialog = DialogFactory.getInstance().createDialog(getActivity(), R.layout.popup_tiket_gagal,
                80, 45);
        Button btn_ulang = dialog.findViewById(R.id.btn_ulang);
        btn_ulang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Custom_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setMessage("Mohon tunggu sebentar...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

}
