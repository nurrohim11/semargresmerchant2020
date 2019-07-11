package gmedia.net.id.semargres2019merchant.tiketKonser.history;

public class HistoryTiketModel {
    String order_id, nama_tiket, jumlah_tiket,
            harga_total, referensi, nama_pembeli,
            nik_pembeli, email_pembeli, status_bayar, promocode;

    public HistoryTiketModel(String order_id, String nama_tiket, String jumlah_tiket, String harga_total, String referensi, String nama_pembeli, String nik_pembeli, String email_pembeli, String status_bayar, String promocode) {
        this.order_id = order_id;
        this.nama_tiket = nama_tiket;
        this.jumlah_tiket = jumlah_tiket;
        this.harga_total = harga_total;
        this.referensi = referensi;
        this.nama_pembeli = nama_pembeli;
        this.nik_pembeli = nik_pembeli;
        this.email_pembeli = email_pembeli;
        this.status_bayar = status_bayar;
        this.promocode = promocode;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getNama_tiket() {
        return nama_tiket;
    }

    public void setNama_tiket(String nama_tiket) {
        this.nama_tiket = nama_tiket;
    }

    public String getJumlah_tiket() {
        return jumlah_tiket;
    }

    public void setJumlah_tiket(String jumlah_tiket) {
        this.jumlah_tiket = jumlah_tiket;
    }

    public String getHarga_total() {
        return harga_total;
    }

    public void setHarga_total(String harga_total) {
        this.harga_total = harga_total;
    }

    public String getReferensi() {
        return referensi;
    }

    public void setReferensi(String referensi) {
        this.referensi = referensi;
    }

    public String getNama_pembeli() {
        return nama_pembeli;
    }

    public void setNama_pembeli(String nama_pembeli) {
        this.nama_pembeli = nama_pembeli;
    }

    public String getNik_pembeli() {
        return nik_pembeli;
    }

    public void setNik_pembeli(String nik_pembeli) {
        this.nik_pembeli = nik_pembeli;
    }

    public String getEmail_pembeli() {
        return email_pembeli;
    }

    public void setEmail_pembeli(String email_pembeli) {
        this.email_pembeli = email_pembeli;
    }

    public String getStatus_bayar() {
        return status_bayar;
    }

    public void setStatus_bayar(String status_bayar) {
        this.status_bayar = status_bayar;
    }

    public String getPromocode() {
        return promocode;
    }
}
