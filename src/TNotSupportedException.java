/**
 * Created by Мирон on 16.11.2014 PACKAGE_NAME.
 */
public class TNotSupportedException extends Exception {

    public TNotSupportedException(String s) {
        super(s);
    }

    public TNotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TNotSupportedException(Throwable cause) {
        super(cause);
    }
}
