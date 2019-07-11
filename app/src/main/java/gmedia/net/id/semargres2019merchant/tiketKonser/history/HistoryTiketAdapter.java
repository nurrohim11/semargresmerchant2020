package gmedia.net.id.semargres2019merchant.tiketKonser.history;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import gmedia.net.id.semargres2019merchant.R;
//import gmedia.net.id.semargres2019merchant.historyPenjualan.HistoryPenjualanModel;


public class HistoryTiketAdapter extends RecyclerView.Adapter<HistoryTiketAdapter.ViewHolder> {
    private ArrayList<HistoryTiketModel> rvData;

    public HistoryTiketAdapter(Context context, ArrayList<HistoryTiketModel> rvData) {
        Context context1 = context;
        this.rvData = rvData;
    }

    public void addMoreData() {
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_history_tiket, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryTiketAdapter.ViewHolder holder, int position) {
        HistoryTiketModel item = rvData.get(position);
        holder.order_id.setText(item.getOrder_id());
        holder.nama_tiket.setText(item.getNama_tiket());
        holder.jumlah_tiket.setText(item.getJumlah_tiket());
        holder.harga_total.setText(item.getHarga_total());
        holder.referensi.setText(item.getReferensi());
        holder.nama_pembeli.setText(item.getNama_pembeli());
        holder.nik_pembeli.setText(item.getNik_pembeli());
        holder.email_pembeli.setText(item.getEmail_pembeli());
        holder.status_bayar.setText(item.getStatus_bayar());
        holder.txt_promocode.setText(item.getPromocode());
    }

    @Override
    public int getItemCount() {
        return rvData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView order_id, nama_tiket, jumlah_tiket, harga_total,
                referensi, nama_pembeli, nik_pembeli, email_pembeli, status_bayar, txt_promocode;

        public ViewHolder(View itemView) {
            super(itemView);
            order_id = itemView.findViewById(R.id.order_id);
            nama_tiket = itemView.findViewById(R.id.nama_tiket);
            jumlah_tiket = itemView.findViewById(R.id.jml_tiket);
            harga_total = itemView.findViewById(R.id.harga_total);
            referensi = itemView.findViewById(R.id.referensi);
            nama_pembeli = itemView.findViewById(R.id.nama_pembeli);
            nik_pembeli = itemView.findViewById(R.id.nik_pembeli);
            email_pembeli = itemView.findViewById(R.id.email_pembeli);
            status_bayar = itemView.findViewById(R.id.status_bayar);
            txt_promocode = itemView.findViewById(R.id.txt_promocode);
        }
    }
}
