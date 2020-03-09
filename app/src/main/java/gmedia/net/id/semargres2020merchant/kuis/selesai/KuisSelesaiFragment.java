package gmedia.net.id.semargres2020merchant.kuis.selesai;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import gmedia.net.id.semargres2020merchant.R;
import gmedia.net.id.semargres2020merchant.kuis.CreateQuizActivity;
import gmedia.net.id.semargres2020merchant.util.ApiVolley;
import gmedia.net.id.semargres2020merchant.util.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class KuisSelesaiFragment extends Fragment {

    RecyclerView rvView;
    KuisSelesaiAdapter adapter;
    ArrayList<KuisSelesaiModel> selesaiLama, selesaiBaru;
    ProgressDialog progressDialog;
    int start = 0, count = 10;

    public KuisSelesaiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_kuis_selesai, container, false);

        rvView = view.findViewById(R.id.rv_selesai);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvView.setLayoutManager(layoutManager);

        selesaiBaru = new ArrayList<>();

//        getSelesai();

//        Button btnCreate = view.findViewById(R.id.btn_selesai);
//        btnCreate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), CreateQuizActivity.class));
//            }
//        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        selesaiLama = new ArrayList<>();
        getSelesai();
    }

    private void getSelesai() {
        showProgressDialog();
        new ApiVolley(getContext(), new JSONObject(), "POST", URL.urlKuisSelesai,
                "", "", 0, new ApiVolley.VolleyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(String result) {
                try {
                    Log.d(URL.TAG, result);

                    dismissProgressDialog();
                    JSONObject object = new JSONObject(result);
//                    Log.d("hasil selesai", object.toString());
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        JSONArray array = object.getJSONObject("response").getJSONArray("quiz");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject isi = array.getJSONObject(i);
                            selesaiLama.add(new KuisSelesaiModel(
                                    isi.getString("id"),
                                    isi.getString("soal"),
                                    isi.getString("periode_start"),
                                    isi.getString("periode_end"),
                                    isi.getString("nama_pemenang"),
                                    isi.getString("email_pemenang"),
                                    isi.getString("telp_pemenang"),
                                    isi.getString("created_at")
                            ));
                        }

                        rvView.setAdapter(null);
                        adapter = new KuisSelesaiAdapter(getContext(), selesaiLama);
                        rvView.setAdapter(adapter);

//                        Log.d("hasil selesai message", message);
//                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                    } else {
//                        Toast.makeText(getContext(), "Kuis selesai : " + message, Toast.LENGTH_LONG).show();
                        Log.d("Kuis selesai", message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
//                Toast.makeText(getActivity(), "Kuis selesai : " + result, Toast.LENGTH_LONG).show();
                Log.d("Kuis selesai", result);
                dismissProgressDialog();
            }
        });
    }

    private void showProgressDialog(){
        progressDialog = new ProgressDialog(getContext(),
                R.style.AppTheme_Custom_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setMessage("Memuat...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void dismissProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }
}
