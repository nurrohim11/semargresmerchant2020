package gmedia.net.id.semargres2019merchant.util;

/**
 * Created by Bayu on 20/03/2018.
 */

public class CustomKategoriModel {

    private String id, nama;

    public CustomKategoriModel(String id, String nama) {
        this.id = id;
        this.nama = nama;
    }

    @Override
    public String toString() {
        return this.nama;
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
}
