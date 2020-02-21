package gmedia.net.id.semargres2020merchant.kuis;

public class JawabanKuisModel {
    String id, email_user, nama_user, jawaban, answered_at;
    private String telepon = "";

    public JawabanKuisModel(String id, String email_user, String nama_user, String jawaban, String answered_at, String telepon) {
        this.id = id;
        this.email_user = email_user;
        this.nama_user = nama_user;
        this.jawaban = jawaban;
        this.answered_at = answered_at;
        this.telepon = telepon;
    }

    public JawabanKuisModel(String id, String email_user, String nama_user, String jawaban, String answered_at) {
        this.id = id;
        this.email_user = email_user;
        this.nama_user = nama_user;
        this.jawaban = jawaban;
        this.answered_at = answered_at;
    }

    public String getTelepon() {
        return telepon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail_user() {
        return email_user;
    }

    public void setEmail_user(String email_user) {
        this.email_user = email_user;
    }

    public String getNama_user() {
        return nama_user;
    }

    public void setNama_user(String nama_user) {
        this.nama_user = nama_user;
    }

    public String getJawaban() {
        return jawaban;
    }

    public void setJawaban(String jawaban) {
        this.jawaban = jawaban;
    }

    public String getAnswered_at() {
        return answered_at;
    }

    public void setAnswered_at(String answered_at) {
        this.answered_at = answered_at;
    }
}
