package gmedia.net.id.semargres2019merchant.voucher;

public class VoucherModel {
    private String uid, nama_voucher, valid_start, valid_end, tipe, nominal, jumlah;

    public VoucherModel(String uid, String nama_voucher, String valid_start, String valid_end, String tipe, String nominal, String jumlah) {
        this.uid = uid;
        this.nama_voucher = nama_voucher;
        this.valid_start = valid_start;
        this.valid_end = valid_end;
        this.tipe = tipe;
        this.nominal = nominal;
        this.jumlah = jumlah;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNama_voucher() {
        return nama_voucher;
    }

    public void setNama_voucher(String nama_voucher) {
        this.nama_voucher = nama_voucher;
    }

    public String getValid_start() {
        return valid_start;
    }

    public void setValid_start(String valid_start) {
        this.valid_start = valid_start;
    }

    public String getValid_end() {
        return valid_end;
    }

    public void setValid_end(String valid_end) {
        this.valid_end = valid_end;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }
}
