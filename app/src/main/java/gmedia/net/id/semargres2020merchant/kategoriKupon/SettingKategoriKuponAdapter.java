package gmedia.net.id.semargres2020merchant.kategoriKupon;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.FormatRupiah;
import gmedia.net.id.semargres2020merchant.util.URL;

public class SettingKategoriKuponAdapter extends RecyclerView.Adapter<SettingKategoriKuponAdapter.ViewHolder> {
    public static ArrayList<SettingKategoriKuponModel> rvData;
    private Context context;
    private ProgressDialog progressDialog;

    public SettingKategoriKuponAdapter(Context context, ArrayList<SettingKategoriKuponModel> rvData) {
        this.context = context;
        this.rvData = rvData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_setting_kupon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SettingKategoriKuponAdapter.ViewHolder holder, final int position) {
        FormatRupiah request = new FormatRupiah();
        holder.judul.setText(rvData.get(position).getNama());
        holder.nominal.setText(request.ChangeToRupiahFormat(rvData.get(position).getNominal()));
        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.popup_tambah_kategori_kupon);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                final EditText edt_namaKupon = dialog.findViewById(R.id.edt_namaKupon);
                final EditText edt_hargaPerKupon = dialog.findViewById(R.id.edt_hargaPerKupon);
                RelativeLayout rvOK = dialog.findViewById(R.id.rv_ok);
                RelativeLayout rvCancel = dialog.findViewById(R.id.rv_cancel);

                edt_namaKupon.setText(rvData.get(position).getNama());
                edt_hargaPerKupon.setText(rvData.get(position).getNominal());

                rvOK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (edt_namaKupon.getText().toString().equals("")) {
                            edt_namaKupon.setError("Nama kupon harap diisi");
                            edt_namaKupon.requestFocus();
                            return;
                        }

                        if (edt_hargaPerKupon.getText().toString().equals("")) {
                            edt_hargaPerKupon.setError("Harga per kupon harap diisi");
                            edt_hargaPerKupon.requestFocus();
                            return;
                        }

                        final JSONObject jBody = new JSONObject();
                        try {
                            jBody.put("id", rvData.get(position).getId());
                            jBody.put("nama", edt_namaKupon.getText());
                            jBody.put("nominal", edt_hargaPerKupon.getText());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        new ApiVolley(context, jBody, "POST", URL.urlKategoriKuponUpdate, "", "", 0, new ApiVolley.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    JSONObject object = new JSONObject(result);
                                    String pesan = object.getJSONObject("metadata").getString("message");
                                    String status = object.getJSONObject("metadata").getString("status");
                                    if (status.equals("200")) {
//                                        Toast.makeText(context, pesan, Toast.LENGTH_LONG).show();
                                        rvData.remove(position);
                                        rvData.add(position, new SettingKategoriKuponModel(
                                                jBody.getString("id"),
                                                jBody.getString("nama"),
                                                jBody.getString("nominal")
                                        ));
                                        notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(context, pesan, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(String result) {
                                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                            }
                        });

                        dialog.dismiss();
                    }
                });

                rvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.popup_delete_kategori_kupon);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                RelativeLayout ya = dialog.findViewById(R.id.logoutYa);
                ya.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final JSONObject jBody = new JSONObject();
                        try {
                            jBody.put("id", rvData.get(position).getId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        showProgressDialog();
                        new ApiVolley(context, jBody, "POST", URL.urlKategoriKuponDelete, "", "", 0, new ApiVolley.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {

                                dismissProgressDialog();
                                try {
                                    JSONObject object = new JSONObject(result);
                                    final String status = object.getJSONObject("metadata").getString("status");
                                    String message = object.getJSONObject("metadata").getString("message");
                                    if (status.equals("200")) {
//                                        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
                                        rvData.remove(position);
                                        notifyDataSetChanged();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(String result) {
                                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                                dismissProgressDialog();
                            }
                        });
                    }
                });
                RelativeLayout tidak = dialog.findViewById(R.id.logoutTidak);
                tidak.setOnClickListener(new View.OnClickListener() {
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

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Custom_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setMessage("Menghapus...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public int getItemCount() {
        return rvData.size();
    }

    public void addMoreData() {
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView judul;
        private TextView nominal;
        private RelativeLayout delete;
        private LinearLayout detail;

        public ViewHolder(View itemView) {
            super(itemView);
            judul = itemView.findViewById(R.id.namaSettingKupon);
            nominal = itemView.findViewById(R.id.nominalSettingKupon);
            detail = itemView.findViewById(R.id.viewDetailSettingKupon);
            delete = itemView.findViewById(R.id.deleteKupon);
        }
    }

}
