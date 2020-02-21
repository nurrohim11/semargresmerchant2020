package gmedia.net.id.semargres2020merchant.util;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bayu on 15/03/2018.
 */

public class URL {

    //    private static final String baseURL = "http://49.128.182.219/panel/";
    private static final String baseURL = "https://semargres.gmedia.id/";
    public static String urlLogin = baseURL + "merchant/auth";
    public static String urlProfile = baseURL + "merchant/profile";
    public static final String TAG = "semargres_log";

    public static Map<String, String> getHeaders(Context context){
        SessionManager session = new SessionManager(context);

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Client-Service", "frontend-client");
        headers.put("Auth-Key", "gmedia_semargress");
        headers.put("Content-Type", "application/json");
        headers.put("Token", session.getToken());
        headers.put("Uid", session.getUid());
        headers.put("Username", session.getUsername());
        headers.put("Flag", session.getFlag());
        return headers;
    }

    public static String EXTRA_KUIS_SUDAH_MENANG = "menang";

    public static String urlSettingKupon = baseURL + "merchant/kupon";

    public static String urlSendEmail = baseURL + "merchant/email_kupon";
    public static String urlGantiPassword = baseURL + "merchant/ganti_password";
    public static String urlScanBarcode = baseURL + "merchant/scan_barcode";
    public static String urlViewPromo = baseURL + "merchant/view_promo";
    public static String urlSettingPromo = baseURL + "merchant/create_promo";
    public static String urlKategori = baseURL + "merchant/ms_kategori";
    public static String urlEditProfile = baseURL + "merchant/edit";
    public static String urlDashboard = baseURL + "merchant/dashboard";
    public static String urlHistoryPenjualan = baseURL + "merchant/transaksi";
    public static String urlEditPromo = baseURL + "merchant/view_promo/";
    public static String urlDeletePromo = baseURL + "merchant/delete_promo";
    public static String urlResetPassword = baseURL + "merchant/reset";
    public static String urlCaraBayar = baseURL + "api/merchant/cara_bayar";
    public static String urlCheckVersion = baseURL + "latest_version/merchant";

    public static String urlDeleteAkun = baseURL + "merchant/delete_user";
    public static String urlGetAkun = baseURL + "merchant/view_user";
    public static String urlTambahAkun = baseURL + "merchant/create_user";
    public static String urlMyQRCode = baseURL + "qrcode_merchant";

    public static String urlStoreKategoriKupon = baseURL + "master/kategori_kupon/store";
    public static String urlKategoriKupon = baseURL + "master/kategori_kupon";
    public static String urlKategoriKuponUpdate = baseURL + "master/kategori_kupon/update";
    public static String urlKategoriKuponDelete = baseURL + "master/kategori_kupon/delete";

    public static String urlSendWhatsapp = baseURL + "merchant/whatsapp_kupon";

    public static String urlStoreUserAkun = baseURL + "merchant/user_account/store";
    public static String urlUserAkun = baseURL + "merchant/user_account";
    public static String urlDeleteUserAkun = baseURL + "merchant/user_account/delete";
    public static String urlQrCodeMerchant = baseURL + "merchant/user_account/qr_code";

    public static String urlQrCodeTenant = baseURL + "tenant/qrcode/generate";

    public static String urlStoreVoucher = baseURL + "merchant/voucher/store";
    public static String urlVoucher = baseURL + "merchant/voucher";
    public static String urlDeleteVoucher = baseURL + "merchant/voucher/delete";

    public static String urlCreateQuiz = baseURL + "merchant/quiz/create";
    public static String urlKuisBerlangsung = baseURL + "merchant/quiz/on_progress";
    public static String urlKuisSelesai = baseURL + "merchant/quiz/done";
    public static String urlKuisJawaban = baseURL + "merchant/quiz/answer";
    public static String urlKuisSetWinner = baseURL + "merchant/quiz/set_winner";

    public static String urlProfileTenant = baseURL + "tenant/profile";
    public static String urlUpdateProfileTenant = baseURL + "tenant/profile/update";

    public static String urlKuponCheckOTP = baseURL + "kupon/check_otp";
    public static String urlKuponRequestOTP = baseURL + "kupon/request_otp";

    public static String urlScanBarcodeMerchant = baseURL + "merchant/scan_qrcode_kupon";
    public static String urlScanBarcodeTenant = baseURL + "tenant/kupon/scan_qrcode";

    public static String urlSendEmailTenant = baseURL + "tenant/kupon/email";
    public static String urlSendWhatsappTenant = baseURL + "tenant/kupon/whatsapp";

    public static String urlDashboardTenant = baseURL + "tenant/dashboard";

    public static String urlKirimSemua = baseURL + "merchant/qrcode/send_email/all";
    public static String urlKirim = baseURL + "merchant/qrcode/send_email";

    public static String urlCekPromo = baseURL + "merchant/tiket/konser/check_promo";
    public static String urlGetKonser = baseURL + "merchant/tiket/konser";
    public static String urlTiketEmail = baseURL + "merchant/tiket/konser/buy_email";
    public static String urlTiketScanQR = baseURL + "merchant/tiket/konser/buy_qrcode";
    public static String urlGetEmail = baseURL + "merchant/tiket/konser/check_data_user";
    public static String urlHistoryTiket = baseURL + "merchant/tiket/konser/history";
    public static String urlRekapTiket = baseURL + "merchant/tiket/konser/rekap";
    public static String urlPrepayment = baseURL + "merchant/tiket/konser/prepayment";

    public static String urlParade = baseURL + "merchant/parade/scan_qr";
}
