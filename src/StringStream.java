/**
 * Created by Мирон on 24.10.2014 PACKAGE_NAME.
 */
public class StringStream implements ICharStream {
    private int pos;
    private String stream;
    public StringStream(String s) {
        stream = s;
        pos = 0;
    }

    @Override
    public char getChar() {
        return stream.charAt(pos++);
    }

    @Override
    public boolean isEmpty() {
        return (pos >= stream.length());
    }

    @Override
    public int streamSize() {
        return stream.length();
    }
}
