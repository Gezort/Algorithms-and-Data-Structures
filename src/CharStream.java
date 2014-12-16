import java.util.Scanner;

/**
 * Created by Мирон on 10.10.2014 PACKAGE_NAME.
 */
public class CharStream implements ICharStream {
    private Scanner scanner = new Scanner(System.in);
    private String stream = "";
    private int pos = 0;
    private int sz = 0;

    @Override
    public char getChar() {
        while (pos >= stream.length()) {
            stream = scanner.nextLine();
            pos = 0;
        }
        sz++;
        return stream.charAt(pos++);
    }

    @Override
    public boolean isEmpty() {
        if (pos >= stream.length() && !scanner.hasNext()) {
            return true;
        }
        return false;
    }

    @Override
    public int streamSize() {
        return sz;
    }
}
