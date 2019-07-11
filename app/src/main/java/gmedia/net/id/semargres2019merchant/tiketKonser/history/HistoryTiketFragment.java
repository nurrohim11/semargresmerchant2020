package gmedia.net.id.semargres2019merchant.tiketKonser.history;


import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gmedia.net.id.semargres2019merchant.R;
import gmedia.net.id.semargres2019merchant.util.ApiVolley;
import gmedia.net.id.semargres2019merchant.util.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryTiketFragment extends Fragment {


    public HistoryTiketFragment() {
        // Required empty public constructor
    }

    private RecyclerView rvView;
    private ArrayList<HistoryTiketModel> arrayList;
    private ProgressDialog progressDialog;
    private HistoryTiketAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_tiket, container, false);

        rvView = view.findViewById(R.id.rv_historyTiketKonser);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvView.setLayoutManager(layoutManager);

        getHistoryTiket();

        return view;
    }

    private void getHistoryTiket(){
        arrayList = new ArrayList<>();
        showProgressDialog();
        new ApiVolley(getActivity(), new JSONObject(), "POST", URL.urlHistoryTiket,
                "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                dismissProgressDialog();
                try {
                    JSONObject object = new JSONObject(result);
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        JSONArray jsonArray = object.getJSONObject("response").getJSONArray("history");
                        Log.d(URL.TAG, result);

                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject isi = jsonArray.getJSONObject(i);
                            arrayList.add(new HistoryTiketModel(isi.getString("order_id"),
                                    isi.getString("nama_tiket"),
                                    isi.getString("jumlah_tiket"),
                                    isi.getString("harga_total"),
                                    isi.getString("referensi"),
                                    isi.getString("nama_pembeli"),
                                    isi.getString("nik_pembeli"),
                                    isi.getString("email_pembeli"),
                                    isi.getString("status_bayar"),
                                    isi.getString("kode_promo")));
                        }

                        rvView.setAdapter(null);
                        adapter = new HistoryTiketAdapter(getContext(), arrayList);
                        rvView.setAdapter(adapter);
//                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
//                        showBerhasil(txt_email.getText().toString(), txt_total.getText().toString(), listJenisTiket.get(selected_jenis).getNama(), txt_jumlah.getText().toString());
                    } else if(!status.equals("404")) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
//                        showGagal();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
                dismissProgressDialog();
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
//                showGagal();
            }
        });
    }

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(getActivity(),
                R.style.AppTheme_Custom_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setMessage("Mohon tunggu sebentar...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
