package gmedia.net.id.semargres2019merchant.tiketKonser.rekap;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leonardus.irfan.Converter;

import java.util.List;

import gmedia.net.id.semargres2019merchant.R;

public class RekapAdapter extends RecyclerView.Adapter<RekapAdapter.RekapViewHolder> {

    private Activity activity;
    private List<RekapModel> listRekap;

    RekapAdapter(Activity activity, List<RekapModel> listRekap){
        this.activity = activity;
        this.listRekap = listRekap;
    }

    @NonNull
    @Override
    public RekapViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RekapViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_rekap, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RekapViewHolder holder, int i) {
        RekapModel r = listRekap.get(i);

        holder.txt_nama.setText(r.getNama());
        holder.txt_total.setText(Converter.doubleToRupiah(r.getTotal()));
    }

    @Override
    public int getItemCount() {
        return listRekap.size();
    }

    class RekapViewHolder extends RecyclerView.ViewHolder{

        TextView txt_nama, txt_total;

        RekapViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_total = itemView.findViewById(R.id.txt_total);
        }
    }
}
