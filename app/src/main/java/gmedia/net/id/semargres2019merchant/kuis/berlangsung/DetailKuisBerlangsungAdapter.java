package gmedia.net.id.semargres2019merchant.kuis.berlangsung;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import gmedia.net.id.semargres2019merchant.R;
import gmedia.net.id.semargres2019merchant.kuis.JawabanKuisModel;

public class DetailKuisBerlangsungAdapter extends RecyclerView.Adapter<DetailKuisBerlangsungAdapter.ViewHolder> {

    public ArrayList<JawabanKuisModel> rvJawaban;

    public DetailKuisBerlangsungAdapter(Context context, ArrayList<JawabanKuisModel> rvJawaban) {
        Context context1 = context;
        this.rvJawaban = rvJawaban;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_jawaban_berlangsung, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final JawabanKuisModel modelJawaban = rvJawaban.get(i);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
        try {
            Date newDate = dateFormat.parse(modelJawaban.getAnswered_at());
            dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault());
            String date = dateFormat.format(newDate);

            viewHolder.txt_tanggalKuis.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.txt_jawabanKuis.setText(modelJawaban.getJawaban());
        viewHolder.txt_namaKuis.setText(modelJawaban.getNama_user());

//        viewHolder.nextBerlangsung.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Dialog dialog = new Dialog(context);
//                dialog.setContentView(R.layout.popup_pilih_pemenang);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                TextView textView = dialog.findViewById(R.id.txt_namaPemenang);
//                String pemenang = "Apakah Anda yakin memilih "+modelJawaban.getNama_user()+" sebagai pemenang?";
//                textView.setText(pemenang);
//
//                Button btnYa = dialog.findViewById(R.id.btn_ya);
//                btnYa.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        final JSONObject jBody = new JSONObject();
//                        try {
//                            jBody.put("id_jawaban", modelJawaban.getId());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        new ApiVolley(context, jBody, "POST", URL.urlKuisSetWinner, "", "", 0, new ApiVolley.VolleyCallback() {
//                            @Override
//                            public void onSuccess(String result) {
//                                try {
//                                    JSONObject object = new JSONObject(result);
//                                    Log.d("hasil set", object.toString());
//                                    final String status = object.getJSONObject("metadata").getString("status");
//                                    final String message = object.getJSONObject("metadata").getString("message");
//                                    if (status.equals("200")) {
//                                        Toast.makeText(context, "Pemenang berhasil dipilih", Toast.LENGTH_SHORT).show();
//                                        context.startActivity(new Intent(context, KuisActivity.class));
//                                    }
//                                    else{
//                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onError(String result) {
//                                Toast.makeText(context, result, Toast.LENGTH_LONG).show();
//                            }
//                        });
//                    }
//                });
//
//                Button btnTidak = dialog.findViewById(R.id.btn_tidak);
//                btnTidak.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//
//                dialog.show();
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return rvJawaban.size();
    }

    public void addMoreData() {
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_tanggalKuis;
        private TextView txt_namaKuis;
        private TextView txt_jawabanKuis;
        private ImageView nextBerlangsung;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_tanggalKuis = itemView.findViewById(R.id.txt_tanggalKuis);
            txt_namaKuis = itemView.findViewById(R.id.txt_namaKuis);
            txt_jawabanKuis = itemView.findViewById(R.id.txt_jawabanKuis);
            nextBerlangsung = itemView.findViewById(R.id.btn_next);
        }
    }
}
