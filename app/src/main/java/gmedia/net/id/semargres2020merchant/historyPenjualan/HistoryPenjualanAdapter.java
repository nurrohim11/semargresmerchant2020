package gmedia.net.id.semargres2020merchant.historyPenjualan;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import gmedia.net.id.semargres2020merchant.R;


public class HistoryPenjualanAdapter extends RecyclerView.Adapter<HistoryPenjualanAdapter.ViewHolder> {
    private ArrayList<HistoryPenjualanModel> rvData;

    public HistoryPenjualanAdapter(Context context, ArrayList<HistoryPenjualanModel> rvData) {
        Context context1 = context;
        this.rvData = rvData;
    }

    public void addMoreData() {
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_history_penjualan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryPenjualanAdapter.ViewHolder holder, int position) {

        HistoryPenjualanModel item = rvData.get(position);
        holder.tanggal.setText(rvData.get(position).getTanggal());
        holder.email.setText(rvData.get(position).getEmail());
        holder.jumlah_kupon.setText(rvData.get(position).getJumlah_kupon());
//        holder.total_belanja.setText(rvKuis.get(position).getTotal_belanja());
        holder.tvNama.setText(item.getNama());
        holder.tvJam.setText(item.getTime());
        holder.tvPemberi.setText(item.getUser());
    }

    @Override
    public int getItemCount() {
        return rvData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tanggal, email, jumlah_kupon, total_belanja, tvNama, tvJam, tvPemberi;

        public ViewHolder(View itemView) {
            super(itemView);
            tanggal = itemView.findViewById(R.id.tanggal_history_penjualan);
            email = itemView.findViewById(R.id.email_history_penjualan);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvJam = itemView.findViewById(R.id.tv_jam);
            jumlah_kupon = itemView.findViewById(R.id.jumlah_kupon_history_penjualan);
//            total_belanja = itemView.findViewById(R.id.total_belanja_history_penjualan);
            tvPemberi = itemView.findViewById(R.id.tv_pemberi);
        }
    }
}
