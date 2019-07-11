package gmedia.net.id.semargres2019merchant.voucher;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gmedia.net.id.semargres2019merchant.R;
import gmedia.net.id.semargres2019merchant.util.ApiVolley;
import gmedia.net.id.semargres2019merchant.util.FormatRupiah;
import gmedia.net.id.semargres2019merchant.util.URL;

/**
 * Created by Bayu on 22/03/2018.
 */

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.ViewHolder> {
    private ArrayList<VoucherModel> rvData;
    private Context context;
    private ProgressDialog progressDialog;

    public VoucherAdapter(Context context, ArrayList<VoucherModel> rvData) {
        this.context = context;
        this.rvData = rvData;
    }

    public void addMoreData() {
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_voucher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VoucherAdapter.ViewHolder holder, final int position) {
        FormatRupiah request = new FormatRupiah();
        VoucherModel item = rvData.get(position);

        holder.namaVoucher.setText(item.getNama_voucher());

        String persen = "Diskon " + item.getNominal() + "%";

        if (item.getTipe().equals("P")) {
            holder.nominalVoucher.setText(persen);
        } else {
            holder.nominalVoucher.setText(request.ChangeToRupiahFormat(item.getNominal()));
        }

//        holder.nominalVoucher.setText(request.ChangeToRupiahFormat(item.getNominal()));
        holder.jmlVoucher.setText(item.getJumlah());

        holder.deleteVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.popup_delete_promo);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                RelativeLayout ya = dialog.findViewById(R.id.logoutYa);
                ya.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final JSONObject jBody = new JSONObject();
                        try {
                            jBody.put("uid", rvData.get(position).getUid());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        showProgressDialog();
                        ApiVolley requst = new ApiVolley(context, jBody, "POST", URL.urlDeleteVoucher, "", "", 0, new ApiVolley.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                dismissProgressDialog();
                                try {
                                    JSONObject object = new JSONObject(result);
                                    final String status = object.getJSONObject("metadata").getString("status");
                                    String message = object.getJSONObject("metadata").getString("message");
                                    if (status.equals("200")) {
//                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                        rvData.remove(position);
                                        notifyDataSetChanged();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(String result) {
                                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
                                dismissProgressDialog();
                                dialog.dismiss();
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

    @Override
    public int getItemCount() {
        return rvData.size();
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(context,
                R.style.AppTheme_Custom_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setMessage("Menghapus...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView namaVoucher, jmlVoucher, nominalVoucher;
        private ImageView deleteVoucher;

        public ViewHolder(View itemView) {
            super(itemView);
            namaVoucher = itemView.findViewById(R.id.namaVoucher);
            jmlVoucher = itemView.findViewById(R.id.jmlVoucher);
            nominalVoucher = itemView.findViewById(R.id.nominalVoucher);
            deleteVoucher = itemView.findViewById(R.id.deleteVoucher);
        }
    }
}
