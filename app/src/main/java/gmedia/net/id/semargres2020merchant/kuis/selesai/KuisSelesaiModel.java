package gmedia.net.id.semargres2020merchant.kuis.selesai;

public class KuisSelesaiModel {
    String id, soal, periode_start, periode_end, nama_pemenang, created_at, email_pemenang, telp_pemenang;

    public KuisSelesaiModel(String id, String soal, String periode_start, String periode_end,
                            String nama_pemenang, String email_pemenang, String telp_pemenang, String created_at) {
        this.id = id;
        this.soal = soal;
        this.periode_start = periode_start;
        this.periode_end = periode_end;
        this.nama_pemenang = nama_pemenang;
        this.created_at = created_at;
        this.email_pemenang = email_pemenang;
        this.telp_pemenang = telp_pemenang;
    }

    public String getEmail_pemenang() {
        return email_pemenang;
    }

    public String getTelp_pemenang() {
        return telp_pemenang;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSoal() {
        return soal;
    }

    public void setSoal(String soal) {
        this.soal = soal;
    }

    public String getPeriode_start() {
        return periode_start;
    }

    public void setPeriode_start(String periode_start) {
        this.periode_start = periode_start;
    }

    public String getPeriode_end() {
        return periode_end;
    }

    public void setPeriode_end(String periode_end) {
        this.periode_end = periode_end;
    }

    public String getNama_pemenang() {
        return nama_pemenang;
    }

    public void setNama_pemenang(String nama_pemenang) {
        this.nama_pemenang = nama_pemenang;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
