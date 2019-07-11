package gmedia.net.id.semargres2019merchant.kuis.berlangsung;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import gmedia.net.id.semargres2019merchant.R;
import gmedia.net.id.semargres2019merchant.kuis.CreateQuizActivity;
import gmedia.net.id.semargres2019merchant.util.ApiVolley;
import gmedia.net.id.semargres2019merchant.util.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class KuisBerlangsungFragment extends Fragment {

    RecyclerView rvView;
    KuisBerlangsungAdapter adapter;
    ArrayList<KuisBerlangsungModel> berlangsungLama, berlangsungBaru;
    ProgressDialog progressDialog;
    int start = 0, count = 10;

    public KuisBerlangsungFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_kuis_berlangsung, container, false);

        rvView = view.findViewById(R.id.rv_berlangsung);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rvView.setLayoutManager(layoutManager);

        berlangsungLama = new ArrayList<>();
        berlangsungBaru = new ArrayList<>();

        getBerlangsung();

        Button btnCreate = view.findViewById(R.id.btn_berlangsung);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CreateQuizActivity.class));
            }
        });

        return view;
    }

    private void getBerlangsung() {
        showProgressDialog();
        new ApiVolley(getContext(), new JSONObject(), "POST", URL.urlKuisBerlangsung, "", "", 0, new ApiVolley.VolleyCallback() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onSuccess(String result) {
                try {
                    dismissProgressDialog();
                    JSONObject object = new JSONObject(result);
//                    Log.d("hasil berlangsung", object.toString());
                    final String status = object.getJSONObject("metadata").getString("status");
                    String message = object.getJSONObject("metadata").getString("message");
                    if (status.equals("200")) {
                        JSONArray array = object.getJSONObject("response").getJSONArray("quiz");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject isi = array.getJSONObject(i);
                            berlangsungLama.add(new KuisBerlangsungModel(
                                    isi.getString("id"),
                                    isi.getString("soal"),
                                    isi.getString("hadiah"),
                                    isi.getString("periode_start"),
                                    isi.getString("periode_end"),
                                    isi.getString("created_at"),
                                    isi.getString("total_jawaban")
                            ));
                        }

                        rvView.setAdapter(null);
                        adapter = new KuisBerlangsungAdapter(getContext(), berlangsungLama);
                        rvView.setAdapter(adapter);

                    } else {
//                        Toast.makeText(getActivity(), "Kuis berlangsung : " + message, Toast.LENGTH_LONG).show();
                        Log.d("Kuis berlangsung", message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String result) {
//                Toast.makeText(getActivity(), "Kuis berlangsung : " + result, Toast.LENGTH_LONG).show();
                Log.d("Kuis berlangsung", result);
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
