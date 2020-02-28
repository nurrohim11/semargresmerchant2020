package gmedia.net.id.semargres2020merchant.volunteer;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionMerchant {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context mContext;
    public static final String SP_APP = "merchant";

    public static final String SP_ID_MERCHANT_SMS = "id_sms";
    public static final String SP_NAMA_MERCHANT_SMS = "nama_sms";
    public static final String SP_ID_MERCHANT_EMAIL = "id_email";
    public static final String SP_NAMA_MERCHANT_EMAIL = "nama_email";
    public static final String SP_ID_MERCHANT_QR = "id_qr";
    public static final String SP_NAMA_MERCHANT_QR = "nama_qr";

    public SessionMerchant(Context context){
        preferences = context.getSharedPreferences(SP_APP, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void saveSPString(String keySP, String value){
        editor.putString(keySP, value);
        editor.commit();
    }

    public void saveSPInt(String keySP, int value){
        editor.putInt(keySP, value);
        editor.commit();
    }

    public void saveSPBoolean(String keySP, boolean value){
        editor.putBoolean(keySP, value);
        editor.commit();
    }

    public String getSpIdSms(){
        return preferences.getString(SP_ID_MERCHANT_SMS, "");
    }

    public String getSpNamaSms(){
        return preferences.getString(SP_NAMA_MERCHANT_SMS, "");
    }

    public String getSpIdEmail(){
        return preferences.getString(SP_ID_MERCHANT_EMAIL, "");
    }

    public String getSpNamaEmail(){
        return preferences.getString(SP_NAMA_MERCHANT_EMAIL, "");
    }

    public String getSpIdQr(){
        return preferences.getString(SP_ID_MERCHANT_QR, "");
    }

    public String getSpNamaQr(){
        return preferences.getString(SP_NAMA_MERCHANT_QR, "");
    }


}
