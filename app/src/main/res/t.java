/**
 * Created by cmy on 2016/9/26.
 */
public class t {
    private static t ourInstance = new t();

    public static t getInstance() {
        return ourInstance;
    }

    private t() {
    }
}
