package gmedia.net.id.semargres2020merchant.historyPenjualan.volunteer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.historyPenjualan.HistoryPenjualanAdapter;
import gmedia.net.id.semargres2020merchant.historyPenjualan.HistoryPenjualanModel;

public class HistoryPenjualanVolunteerAdapter extends RecyclerView.Adapter<HistoryPenjualanVolunteerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<HistoryPenjualanModel> models;

    public HistoryPenjualanVolunteerAdapter(Context context, ArrayList<HistoryPenjualanModel> models){
        this.context = context;
        this.models =models;
    }

    public void updateData(List<HistoryPenjualanModel> items) {
        models.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryPenjualanVolunteerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_history_penjualan_volunteer, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryPenjualanVolunteerAdapter.ViewHolder viewHolder, int i) {

        HistoryPenjualanModel item = models.get(i);
        viewHolder.tvTanggal.setText(models.get(i).getTanggal());
        viewHolder.tvEmail.setText(models.get(i).getEmail());
        viewHolder.tvJmlKupon.setText(models.get(i).getJumlah_kupon());
        viewHolder.tvNama.setText(item.getNama());
        viewHolder.tvJam.setText(item.getTime());
        viewHolder.tvPemberi.setText(item.getUser());
        viewHolder.tvMerchant.setText(item.getMerchant());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTanggal, tvEmail, tvJmlKupon, tvMerchant, tvNama, tvJam, tvPemberi;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTanggal = itemView.findViewById(R.id.tanggal_history_penjualan);
            tvEmail = itemView.findViewById(R.id.email_history_penjualan);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvJam = itemView.findViewById(R.id.tv_jam);
            tvJmlKupon= itemView.findViewById(R.id.jumlah_kupon_history_penjualan);
            tvMerchant = itemView.findViewById(R.id.tv_merchant);
            tvPemberi = itemView.findViewById(R.id.tv_pemberi);
        }
    }
}
