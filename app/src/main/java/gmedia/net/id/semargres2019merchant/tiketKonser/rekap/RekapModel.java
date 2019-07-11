package gmedia.net.id.semargres2019merchant.tiketKonser.rekap;

public class RekapModel {
    private String id;
    private String nama;
    private double total;

    public RekapModel(String id, String nama, double total){
        this.id = id;
        this.nama = nama;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public double getTotal() {
        return total;
    }
}
