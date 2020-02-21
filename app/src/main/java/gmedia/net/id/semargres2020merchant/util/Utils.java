package gmedia.net.id.semargres2020merchant.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Utils {
    public static SimpleDateFormat dateFormat(String pattern){
        return new SimpleDateFormat(pattern, Locale.getDefault());
    }
}
