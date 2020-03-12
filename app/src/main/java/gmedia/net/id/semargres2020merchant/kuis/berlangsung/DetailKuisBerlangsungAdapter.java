package gmedia.net.id.semargres2020merchant.kuis.berlangsung;

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

import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.kuis.JawabanKuisModel;

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
