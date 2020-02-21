package gmedia.net.id.semargres2020merchant.akun;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gmedia.net.id.semargres2020merchant.MyQRCode;
import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.FourItemModel;
import gmedia.net.id.semargres2020merchant.util.URL;

/**
 * Created by Bayu on 19/03/2018.
 */

public class ListAkunAdapter extends RecyclerView.Adapter<ListAkunAdapter.ViewHolder> {
    public static ArrayList<FourItemModel> rvData;
    private Context context;
    private ProgressDialog progressDialog;

    public ListAkunAdapter(Context context, ArrayList<FourItemModel> rvData) {
        this.context = context;
        this.rvData = rvData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_list_akun, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListAkunAdapter.ViewHolder holder, final int position) {

        final FourItemModel selectedItem = rvData.get(position);
        holder.tvItem1.setText(selectedItem.getItem2());

        holder.rvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.popup_delete_promo);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView tvTitle = (TextView) dialog.findViewById(R.id.tv_title);
                String textTitle = "Apakah anda yakin ingin menghapus akun " + selectedItem.getItem2() + " ?";
                tvTitle.setText(textTitle);
                RelativeLayout ya = dialog.findViewById(R.id.logoutYa);
                ya.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final JSONObject jBody = new JSONObject();
                        try {
                            jBody.put("id", selectedItem.getItem1());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        showProgressDialog();
                        new ApiVolley(context, jBody, "POST", URL.urlDeleteUserAkun, "", "", 0, new ApiVolley.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {

                                dismissProgressDialog();
                                try {
//                                    Log.d("hasil delete", object.toString());
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

        holder.llContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ApiVolley(context, new JSONObject(), "GET", URL.urlQrCodeMerchant + "/" + selectedItem.getItem1(), "", "", 0, new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        dismissProgressDialog();
                        try {
                            JSONObject object = new JSONObject(result);
                            Log.d("hasil qr code", object.toString());
                            final String status = object.getJSONObject("metadata").getString("status");
                            String message = object.getJSONObject("metadata").getString("message");
                            if (status.equals("200")) {
//                                Toast.makeText(context,message,Toast.LENGTH_LONG).show();
                                Intent i = new Intent(context, MyQRCode.class);
                                i.putExtra("qr_code", object.getJSONObject("response").getString("qr_code"));
                                i.putExtra("nama", selectedItem.getItem2());
                                i.putExtra("id", selectedItem.getItem1());
                                context.startActivity(i);
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
        private TextView tvItem1;
        private RelativeLayout rvDelete;
        private RelativeLayout llContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            tvItem1 = (TextView) itemView.findViewById(R.id.tv_item1);
            llContainer = (RelativeLayout) itemView.findViewById(R.id.layout_rv_tambahAkun);
            rvDelete = (RelativeLayout) itemView.findViewById(R.id.rv_delete);
        }
    }
}
