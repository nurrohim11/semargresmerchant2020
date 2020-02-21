package gmedia.net.id.semargres2020merchant.util;

/**
 * Created by Shinmaul on 3/30/2018.
 */

public class TwoItemModel {

    private String item1, item2;

    public TwoItemModel(String item1, String item2) {
        this.item1 = item1;
        this.item2 = item2;
    }

    @Override
    public String toString() {
        return item2;
    }

    public String getItem1() {
        return item1;
    }

    public void setItem1(String item1) {
        this.item1 = item1;
    }

    public String getItem2() {
        return item2;
    }

    public void setItem2(String item2) {
        this.item2 = item2;
    }
}
