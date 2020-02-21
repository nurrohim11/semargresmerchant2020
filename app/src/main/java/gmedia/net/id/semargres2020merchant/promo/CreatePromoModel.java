package gmedia.net.id.semargres2020merchant.promo;

public class CreatePromoModel {
    private String id, gambar, judul, status, idKategori;

    public CreatePromoModel(String id, String gambar, String judul, String status, String idKategori) {
        this.id = id;
        this.gambar = gambar;
        this.judul = judul;
        this.status = status;
        this.idKategori = idKategori;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIdKategori() {
        return idKategori;
    }

    public void setIdKategori(String idKategori) {
        this.idKategori = idKategori;
    }
}
