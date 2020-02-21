package gmedia.net.id.semargres2020merchant.kuis.berlangsung;

public class KuisBerlangsungModel {
    String id, soal, hadiah, periode_start, periode_end, nama_pemenang, created_at, total_jawaban;

    public KuisBerlangsungModel(String id, String soal, String hadiah, String periode_start, String periode_end, String created_at, String total_jawaban) {
        this.id = id;
        this.soal = soal;
        this.hadiah = hadiah;
        this.periode_start = periode_start;
        this.periode_end = periode_end;
        this.created_at = created_at;
        this.total_jawaban = total_jawaban;
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

    public String getHadiah() {
        return hadiah;
    }

    public void setHadiah(String hadiah) {
        this.hadiah = hadiah;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
