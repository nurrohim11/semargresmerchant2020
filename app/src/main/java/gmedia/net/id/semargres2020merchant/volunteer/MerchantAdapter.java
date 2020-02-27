package gmedia.net.id.semargres2020merchant.volunteer.kirimkupon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import gmedia.net.id.semargres2020merchant.HomeActivity;
import gmedia.net.id.semargres2020merchant.R;

import static gmedia.net.id.semargres2020merchant.volunteer.kirimkupon.SessionMerchant.SP_ID_MERCHANT_EMAIL;
import static gmedia.net.id.semargres2020merchant.volunteer.kirimkupon.SessionMerchant.SP_ID_MERCHANT_SMS;
import static gmedia.net.id.semargres2020merchant.volunteer.kirimkupon.SessionMerchant.SP_NAMA_MERCHANT_EMAIL;
import static gmedia.net.id.semargres2020merchant.volunteer.kirimkupon.SessionMerchant.SP_NAMA_MERCHANT_SMS;

public class MerchantAdapter extends RecyclerView.Adapter<MerchantAdapter.ViewHolder> {
    public static List<MerchantModel> merchantModels;
    private Context context;
    public static SessionMerchant sessionMerchant;
    int jenis=0;
    // jenis 1 = sms
    // jenis 2 = email
    // jenis 3 = scan qr

    public MerchantAdapter(Context context, List<MerchantModel> list, int jenis){
        this.merchantModels = list;
        this.context = context;
        this.jenis =jenis;

    }

    @NonNull
    @Override
    public MerchantAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_merchant, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int i) {
        final MerchantModel m = merchantModels.get(i);
        sessionMerchant = new SessionMerchant(context);
        h.tvNama.setText(m.getNama());
        h.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(jenis ==1){
                    sessionMerchant.saveSPString(SP_ID_MERCHANT_SMS, m.id);
                    sessionMerchant.saveSPString(SP_NAMA_MERCHANT_SMS, m.nama);
                    HomeActivity.tvMerchant.setText(sessionMerchant.getSpNamaSms());
                    HomeActivity.dgMerchant.dismiss();
                }else if(jenis ==2){
                    sessionMerchant.saveSPString(SP_ID_MERCHANT_EMAIL, m.id);
                    sessionMerchant.saveSPString(SP_NAMA_MERCHANT_EMAIL, m.nama);
                    HomeActivity.tvMerchantEmail.setText(sessionMerchant.getSpNamaEmail());
                    HomeActivity.dgMerchant.dismiss();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return merchantModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_nama);
        }
    }
}
