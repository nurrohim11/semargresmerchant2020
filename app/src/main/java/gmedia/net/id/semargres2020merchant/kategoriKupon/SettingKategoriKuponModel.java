package gmedia.net.id.semargres2020merchant.kategoriKupon;

public class SettingKategoriKuponModel {
    private String id, nama, nominal;

    public SettingKategoriKuponModel(String id, String nama, String nominal) {
        this.id = id;
        this.nama = nama;
        this.nominal = nominal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNominal() {
        return nominal;
    }

    public void setNominal(String nominal) {
        this.nominal = nominal;
    }

    @Override
    public String toString() {
        return getNama();
    }
}
