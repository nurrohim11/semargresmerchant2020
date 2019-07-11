package gmedia.net.id.semargres2019merchant.tiketKonser.rekap;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.DateTimeChooser;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import gmedia.net.id.semargres2019merchant.R;
import gmedia.net.id.semargres2019merchant.util.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class RekapFragment extends Fragment {

    private Activity activity;

    private List<RekapModel> listRekap = new ArrayList<>();
    private RekapAdapter adapter;

    private TextView txt_datestart, txt_dateend, txt_total;

    public RekapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_rekap, container, false);

        RecyclerView rv_rekap = v.findViewById(R.id.rv_rekap);
        rv_rekap.setItemAnimator(new DefaultItemAnimator());
        rv_rekap.setLayoutManager(new LinearLayoutManager(activity));
        adapter = new RekapAdapter(activity, listRekap);
        rv_rekap.setAdapter(adapter);

        txt_total = v.findViewById(R.id.txt_total);
        txt_datestart = v.findViewById(R.id.txt_datestart);
        txt_dateend = v.findViewById(R.id.txt_dateend);
        txt_datestart.setText(Converter.DToString(new Date()));
        txt_dateend.setText(Converter.DToString(new Date()));

        v.findViewById(R.id.btn_datestart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(activity, new DateTimeChooser.DateTimeListener() {
                    @Override
                    public void onFinished(String dateString) {
                        txt_datestart.setText(dateString);
                    }
                });
            }
        });

        v.findViewById(R.id.btn_dateend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeChooser.getInstance().selectDate(activity, new DateTimeChooser.DateTimeListener() {
                    @Override
                    public void onFinished(String dateString) {
                        txt_dateend.setText(dateString);
                    }
                });
            }
        });

        v.findViewById(R.id.btn_proses).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRekap();
            }
        });

        loadRekap();

        return v;
    }

    private void loadRekap(){
        AppLoading.getInstance().showLoading(activity);
        JSONBuilder body = new JSONBuilder();
        body.add("datestart", txt_datestart.getText().toString());
        body.add("dateend", txt_dateend.getText().toString());

        ApiVolleyManager.getInstance().addSecureRequest(activity, URL.urlRekapTiket,
                ApiVolleyManager.METHOD_POST, URL.getHeaders(activity), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        listRekap.clear();
                        adapter.notifyDataSetChanged();

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            listRekap.clear();
                            JSONArray response = new JSONObject(result).getJSONArray("rekap");

                            double sum = 0;
                            for(int i = 0; i < response.length(); i++){
                                JSONObject rekap = response.getJSONObject(i);
                                listRekap.add(new RekapModel(rekap.getString("id"),
                                        rekap.getString("nama"), rekap.getDouble("total")));
                                sum += rekap.getDouble("total");
                            }

                            txt_total.setText(Converter.doubleToRupiah(sum));

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(activity, "Terjadi kesalahan data", Toast.LENGTH_SHORT).show();
                            Log.e(URL.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }
}
