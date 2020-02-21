package gmedia.net.id.semargres2020merchant.kuis.selesai;

import android.content.Context;
import android.content.Intent;
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
import gmedia.net.id.semargres2020merchant.util.URL;
import gmedia.net.id.semargres2020merchant.util.Utils;

public class KuisSelesaiAdapter extends RecyclerView.Adapter<KuisSelesaiAdapter.ViewHolder> {

    public ArrayList<KuisSelesaiModel> rvData;
    private Context context;

    public KuisSelesaiAdapter(Context context, ArrayList<KuisSelesaiModel> rvData) {
        this.context = context;
        this.rvData = rvData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_kuis_selesai, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final KuisSelesaiModel model = rvData.get(i);

        String tanggalMulai = "";
        String tanggalAkhir = "";

        SimpleDateFormat dateFormat = Utils.dateFormat("yyyy-MM-dd");
        try {
            Date newDateStart = dateFormat.parse(model.getPeriode_start());
            Date newDateEnd = dateFormat.parse(model.getPeriode_end());

//            dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            String dateStartCustom = Utils.dateFormat("dd MMM").format(newDateStart);
            String dateEndCustom = Utils.dateFormat("dd MMM yyyy").format(newDateEnd);

//            String dateStart = dateFormat.format(dateStartCustom);
//            String dateEnd = dateFormat.format(dateEndCustom);

            viewHolder.txtStartBerlangsung.setText(dateStartCustom);
            viewHolder.txtEndtBerlangsung.setText(dateEndCustom);

            tanggalMulai = dateStartCustom;
            tanggalAkhir = dateEndCustom;
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if(!model.getNama_pemenang().isEmpty()){
            viewHolder.layout_pemenang.setVisibility(View.VISIBLE);
            viewHolder.txtNamaPemenang.setText(model.getNama_pemenang());
            viewHolder.txt_email.setText(" : " + model.getEmail_pemenang());
            viewHolder.txt_telepon.setText(" : " + model.getTelp_pemenang());
        }
        else{
            viewHolder.layout_pemenang.setVisibility(View.GONE);
        }

        viewHolder.txtSoalBerlangsung.setText(model.getSoal());
//        viewHolder.txtStartBerlangsung.setText(model.getPeriode_start());
//        viewHolder.txtEndtBerlangsung.setText(model.getPeriode_end());

        final String finalTanggalMulai = tanggalMulai;
        final String finalTanggalAkhir = tanggalAkhir;
        viewHolder.nextBerlangsung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailKuisSelesaiActivity.class);
                i.putExtra("id", model.getId());
                i.putExtra("startSomething", finalTanggalMulai);
                i.putExtra("end", finalTanggalAkhir);
                i.putExtra("soal", model.getSoal());
                if(!model.getNama_pemenang().isEmpty()){
                    i.putExtra(URL.EXTRA_KUIS_SUDAH_MENANG, true);
                }
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
        private TextView txtNamaPemenang;
        private ImageView nextBerlangsung;
        TextView txt_email, txt_telepon;
        View layout_pemenang;

        public ViewHolder(View itemView) {
            super(itemView);
            txtSoalBerlangsung = itemView.findViewById(R.id.txt_soalBerlangsung);
            txtStartBerlangsung = itemView.findViewById(R.id.txt_startBerlangsung);
            txtEndtBerlangsung = itemView.findViewById(R.id.txt_endBerlangsung);
            txtNamaPemenang = itemView.findViewById(R.id.txt_namaPemenang);
            nextBerlangsung = itemView.findViewById(R.id.btn_next);
            txt_email = itemView.findViewById(R.id.txt_email);
            txt_telepon = itemView.findViewById(R.id.txt_telepon);
            layout_pemenang = itemView.findViewById(R.id.layout_pemenang);
        }
    }
}
