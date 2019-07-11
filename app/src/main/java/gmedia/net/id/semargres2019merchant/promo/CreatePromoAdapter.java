package gmedia.net.id.semargres2019merchant.promo;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gmedia.net.id.semargres2019merchant.R;
import gmedia.net.id.semargres2019merchant.util.ApiVolley;
import gmedia.net.id.semargres2019merchant.util.CircleTransform;
import gmedia.net.id.semargres2019merchant.util.URL;

public class CreatePromoAdapter extends RecyclerView.Adapter<CreatePromoAdapter.ViewHolder> {
    public static ArrayList<CreatePromoModel> rvData;
    private Context context;
    private ProgressDialog progressDialog;

    public CreatePromoAdapter(Context context, ArrayList<CreatePromoModel> rvData) {
        this.context = context;
        CreatePromoAdapter.rvData = rvData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_create_promo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CreatePromoAdapter.ViewHolder holder, final int position) {

        Picasso.with(context).load(rvData.get(position).getGambar())
                .resize(256, 256)
                .transform(new CircleTransform()).into(holder.gambar);

        CreatePromoModel selectedItem = rvData.get(position);

        if (selectedItem.getStatus().toLowerCase().equals("verified")) {
            holder.judul.setTextColor(context.getResources().getColor(R.color.color_black));
        } else if (selectedItem.getStatus().toLowerCase().equals("unverify")) {
            holder.judul.setTextColor(context.getResources().getColor(R.color.color_orange));
        } else {
            holder.judul.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }

        holder.judul.setText(rvData.get(position).getJudul());
        holder.detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ApiVolley(context, new JSONObject(), "GET", URL.urlEditPromo + rvData.get(position).getId(), "", "", 0, new ApiVolley.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String status = object.getJSONObject("metadata").getString("message");
                            if (status.equals("Success!")) {
                                JSONArray array = object.getJSONArray("response");
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject isi = array.getJSONObject(i);

                                    Intent intent = new Intent(context, EditPromoActivity.class);
                                    intent.putExtra("id", isi.getString("id"));
                                    intent.putExtra("title", isi.getString("title"));
                                    intent.putExtra("link", isi.getString("link"));
                                    intent.putExtra("keterangan", isi.getString("keterangan"));
                                    intent.putExtra("gambar", isi.getString("gambar"));
                                    intent.putExtra("id_k", isi.getString("id_k"));

                                    context.startActivity(intent);
                                    ((Activity) context).finish();
                                }
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
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.popup_delete_promo);
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
                        new ApiVolley(context, jBody, "POST", URL.urlDeletePromo, "", "", 0, new ApiVolley.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {

                                dismissProgressDialog();
                                try {
                                    JSONObject object = new JSONObject(result);
                                    final String status = object.getJSONObject("response").getString("status");
                                    String message = object.getJSONObject("response").getString("message");
                                    if (status.equals("1")) {
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

    @Override
    public int getItemCount() {
        return rvData.size();
    }

    public void addMoreData() {
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView judul;
        private ImageView gambar;
        private RelativeLayout delete;
        private LinearLayout detail;

        public ViewHolder(View itemView) {
            super(itemView);
            gambar = itemView.findViewById(R.id.gambarCreatePromo);
            judul = itemView.findViewById(R.id.judulCreatePromo);
            detail = itemView.findViewById(R.id.viewDetailCreatePromo);
            delete = itemView.findViewById(R.id.deletePromo);
        }
    }
}
