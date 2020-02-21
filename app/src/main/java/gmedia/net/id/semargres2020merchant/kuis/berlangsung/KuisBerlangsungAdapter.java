package gmedia.net.id.semargres2020merchant.kuis.berlangsung;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.journeyapps.barcodescanner.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.util.Utils;

public class KuisBerlangsungAdapter extends RecyclerView.Adapter<KuisBerlangsungAdapter.ViewHolder> {

    public ArrayList<KuisBerlangsungModel> rvData;
    private Context context;

    public KuisBerlangsungAdapter(Context context, ArrayList<KuisBerlangsungModel> rvData) {
        this.context = context;
        this.rvData = rvData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_kuis_berlangsung, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final KuisBerlangsungModel model = rvData.get(i);

        String tanggalMulai = "";
        String tanggalAkhir = "";

        SimpleDateFormat dateFormat = Utils.dateFormat("yyyy-MM-dd");
        try {
            Date newDateStart = dateFormat.parse(model.getPeriode_start());
            Date newDateEnd = dateFormat.parse(model.getPeriode_end());

//            dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
//            String dateStart = dateFormat.format(newDateStart);
//            String dateEnd = dateFormat.format(newDateEnd);

            String dateStartCustom = Utils.dateFormat("dd MMM").format(newDateStart);
            String dateEndCustom = Utils.dateFormat("dd MMM yyyy").format(newDateEnd);

            viewHolder.txtStartBerlangsung.setText(dateStartCustom);
            viewHolder.txtEndtBerlangsung.setText(dateEndCustom);

            tanggalMulai = dateStartCustom;
            tanggalAkhir = dateEndCustom;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.txtSoalBerlangsung.setText(model.getSoal());
//        viewHolder.txtStartBerlangsung.setText(model.getPeriode_start());
//        viewHolder.txtEndtBerlangsung.setText(model.getPeriode_end());
        viewHolder.txtHadiahBerlangsung.setText(model.getHadiah());

        final String finalTanggalMulai = tanggalMulai;
        final String finalTanggalAkhir = tanggalAkhir;
        viewHolder.nextBerlangsung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailKuisBerlangsungActivity.class);
                i.putExtra("id", model.getId());
                i.putExtra("startSomething", finalTanggalMulai);
                i.putExtra("end", finalTanggalAkhir);
                i.putExtra("soal", model.getSoal());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return rvData.size();
    }

    public void addMoreData() {
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtSoalBerlangsung;
        private TextView txtStartBerlangsung;
        private TextView txtEndtBerlangsung;
        private TextView txtHadiahBerlangsung;
        private ImageView nextBerlangsung;

        public ViewHolder(View itemView) {
            super(itemView);
            txtSoalBerlangsung = itemView.findViewById(R.id.txt_soalBerlangsung);
            txtStartBerlangsung = itemView.findViewById(R.id.txt_startBerlangsung);
            txtEndtBerlangsung = itemView.findViewById(R.id.txt_endBerlangsung);
            txtHadiahBerlangsung = itemView.findViewById(R.id.txt_namaHadiah);
            nextBerlangsung = itemView.findViewById(R.id.btn_next);
        }
    }
}
