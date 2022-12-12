package com.evo.mitzoom.Fragments;

import static com.evo.mitzoom.ui.DipsVideoConfren.text_timer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.evo.mitzoom.API.ApiService;
import com.evo.mitzoom.API.Server;
import com.evo.mitzoom.R;
import com.evo.mitzoom.Session.SessionManager;
import com.evo.mitzoom.ui.DipsVideoConfren;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class frag_inputdata extends Fragment {
    private Context context;
    private EditText et_NamaNasabah, et_NikNasabah;
    private String Nama, NIK;
    private MaterialButton btnNext;
    private boolean isCust;
    public int seconds = 0;
    public boolean running = true;
    public boolean wasRunning;
    private boolean result = true;
    private LayoutInflater inflater;
    private View dialogView;
    private SessionManager session;
    private String idDips, TW_NIK, TW_NAMA;
    private SweetAlertDialog sweetAlertDialogTNC;
    private WebView views;
    private SwipeRefreshLayout swipe;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        isCust = getArguments().getBoolean("ISCUST");
        session = new SessionManager(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_input_nik_nama, container, false);
        //swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        //views = view.findViewById(R.id.views);
        et_NamaNasabah = view.findViewById(R.id.et_nama);
        et_NikNasabah = view.findViewById(R.id.et_nik);
        btnNext = view.findViewById(R.id.btnNext);
        sweetAlertDialogTNC = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        idDips = session.getKEY_IdDips();

        /*swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFormNIK();
            }
        });*/

        /*views.getSettings().setJavaScriptEnabled(true);
        views.addJavascriptInterface(new WebAppInterface(getActivity()), "Android");*/

        //getFormNIK();

        Popup();
        et_NamaNasabah.setFilters(new InputFilter[]{
            new InputFilter() {
                @Override
                public CharSequence filter(CharSequence cs, int start, int end, Spanned dest, int dstart, int dend) {
                    // TODO Auto-generated method stub
                    if(cs.equals("")){ // for backspace
                        return cs;
                    }
                    if(cs.toString().matches("[a-zA-Z ]+")){ // here no space character
                        return cs;
                    }
                    return "";
                }
            }
        });
        et_NamaNasabah.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                result = false;
                Mirroring(false,s,et_NikNasabah.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty() && !et_NikNasabah.getText().toString().isEmpty()){
                    btnNext.setClickable(true);
                    btnNext.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                }
                else {
                    btnNext.setClickable(false);
                    btnNext.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
                }
            }
        });
        et_NikNasabah.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                result = false;
                Mirroring(false,et_NamaNasabah.getText().toString(),s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty() && !et_NamaNasabah.getText().toString().isEmpty()){
                    btnNext.setClickable(true);
                    btnNext.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                }
                else {
                    btnNext.setClickable(false);
                    btnNext.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
                }

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nama = et_NamaNasabah.getText().toString();
                NIK = et_NikNasabah.getText().toString();
                if(Nama.isEmpty()){
                    et_NamaNasabah.setError(getResources().getString(R.string.error_field));
                }
                else if (NIK.isEmpty()){
                    et_NikNasabah.setError(getResources().getString(R.string.error_field));
                }
                else {
                    CekData();
                    //getByNIK();
                }
            }
        });
    }

    public class WebAppInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }

    private void getFormNIK() {
        Server.getAPIWAITING_PRODUCT().getFormNIK().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //swipe.setRefreshing(false);
                if (response.isSuccessful()) {
                    try {
                        String dataHtml = response.body().string();
                        JSONObject dataObj = new JSONObject(dataHtml);
                        String formHTML = dataObj.getString("form");
                        String encodedHtml = Base64.encodeToString(formHTML.getBytes(),
                                Base64.NO_PADDING);
                        views.loadData(encodedHtml, "text/html", "base64");
                        views.loadUrl("javascript:document.getElementById('inputNama').addEventListener('change',function(e){" +
                                " var val = e.target.value; Android.showToast(val);" +
                                "},false);");
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //swipe.setRefreshing(false);
            }
        });
    }
    private void Mirroring(boolean bool, CharSequence sequence1, CharSequence sequence2){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(sequence1);
            jsonArray.put(sequence2);
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",1);
            jsons.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.Mirroring(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("MIRROR","Mirroring Sukses");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("MIRROR","Mirroring Gagal");
            }
        });
    }
    private void Mirroring2(boolean bool){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",3);
            jsons.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.Mirroring(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("MIRROR","Mirroring Sukses");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("MIRROR","Mirroring Gagal");
            }
        });
    }
    private void Mirroring3(boolean bool){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",2);
            jsons.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.Mirroring(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("MIRROR","Mirroring Sukses");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("MIRROR","Mirroring Gagal");
            }
        });
    }
    private void Mirroring4(boolean bool){
        JSONObject jsons = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(bool);
            jsons.put("idDips",idDips);
            jsons.put("code",14);
            jsons.put("data",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());
        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.Mirroring(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("MIRROR","Mirroring Sukses");
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("MIRROR","Mirroring Gagal");
            }
        });
    }

    private void getByNIK() {
        String accessToken = session.getAuthToken();
        Log.e("CEK","accessToken : "+accessToken);
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("nik",NIK);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("CEK","REQUEST getByNIK : "+jsons.toString());
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        ApiService API = Server.getAPIService2();
        Call<JsonObject> call = API.IdentifybyNIK(requestBody,"Bearer "+accessToken);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.e("CEK","Response Code : "+response.code());
                if (response.isSuccessful()) {
                    String dataS = response.body().toString();
                    Log.e("CEK","Response dataS : "+dataS);
                    try {
                        JSONObject dataObj = new JSONObject(dataS);

                        if (dataObj.getJSONObject("data").isNull("noCif")) {
                            PopUpTnc();
                        } else {
                            Mirroring4(false);
                            session.clearCIF();
                            getFragmentPage(new frag_portfolio_new());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        String dataErr = response.errorBody().string();
                        Log.e("CEK","dataErr : "+dataErr);
                        if (dataErr != null) {
                            try {
                                JSONObject dataObj = new JSONObject(dataErr);
                                if(dataObj.has("message")) {
                                    String message = dataObj.getString("message");
                                    Toast.makeText(context, message,Toast.LENGTH_SHORT).show();
                                    if(response.code() == 404) {
                                        PopUpTnc();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(context, R.string.msg_error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void CekData(){
        String idDips = session.getKEY_IdDips();
        JSONObject jsons = new JSONObject();
        try {
            jsons.put("idDips",idDips);
            jsons.put("nik",NIK);
            jsons.put("name",Nama);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsons.toString());

        ApiService API = Server.getAPIService();
        Call<JsonObject> call = API.CekData(requestBody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body().size() > 0) {
                    Mirroring(true, Nama, NIK);
                    String dataS = response.body().toString();
                    try {
                        JSONObject jsObj = new JSONObject(dataS);
                        int err_code = jsObj.getInt("err_code");
                        String message = jsObj.getString("message");

                        Log.d("Cek Message", message);

                        if (err_code == 0){
                            PopUpTnc();
                        }
                        else {
                            Mirroring4(false);
                            session.clearCIF();
                            getFragmentPage(new frag_portfolio_new());
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.d("CEK","MASUK ELSE");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(context,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void runTimer(TextView timer_run) {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format(Locale.getDefault(),"%02d:%02d", minutes, secs);
                timer_run.setText(time);
                if (running) {
                    seconds++;
                }
                handler.postDelayed(this,1000);
            }
        });
    }
    private void Popup(){
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setContentText(getResources().getString(R.string.content_input));
        sweetAlertDialog.setConfirmText(getResources().getString(R.string.btn_continue));
        sweetAlertDialog.show();
        sweetAlertDialog.setCancelable(false);
        Button btnConfirm = (Button) sweetAlertDialog.findViewById(cn.pedant.SweetAlert.R.id.confirm_button);
        btnConfirm.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*DipsVideoConfren.timer.setVisibility(View.VISIBLE);
                runTimer(text_timer);*/
                sweetAlertDialog.dismiss();
            }
        });
    }

    private void getFragmentPage(Fragment fragment){
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.layout_frame2, fragment)
                .addToBackStack(null)
                .commit();
    }
    private void PopUpTnc(){
        Log.e("CEK","MASUK PopUpTnc");
        inflater = ((Activity)context).getLayoutInflater();
        if (sweetAlertDialogTNC == null) {
            dialogView = inflater.inflate(R.layout.item_tnc,null);
            sweetAlertDialogTNC = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
            sweetAlertDialogTNC.setCustomView(dialogView);
            sweetAlertDialogTNC.hideConfirmButton();
            sweetAlertDialogTNC.setCancelable(false);
        }
        sweetAlertDialogTNC.show();
        CheckBox checkBox = dialogView.findViewById(R.id.checktnc);
        Button btn = dialogView.findViewById(R.id.btnnexttnc);
        btn.setClickable(false);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()){
                    Log.d("CHECK","TRUE");
                    btn.setBackgroundTintList(context.getResources().getColorStateList(R.color.Blue));
                    btn.setClickable(true);
                }
                else {
                    Log.d("CHECK","FALSE");
                    btn.setBackgroundTintList(context.getResources().getColorStateList(R.color.btnFalse));
                    btn.setClickable(false);
                }
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()){
                    Mirroring2(true);
                    Mirroring3(true);
                    sweetAlertDialogTNC.dismiss();
                    sweetAlertDialogTNC.cancel();
                    getFragmentPage(new frag_opening_account());
                }
                else {
                    btn.setClickable(false);
                }
            }
        });
    }
}
