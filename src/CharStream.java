import java.util.Scanner;

/**
 * Created by Мирон on 10.10.2014 PACKAGE_NAME.
 */
public class CharStream implements ICharStream {
    private Scanner scanner = new Scanner(System.in);
    private String stream = "";
    private int pos = 0;
    @Override
    public char getChar() {
        while (pos >= stream.length()) {
            stream = scanner.nextLine();
            pos = 0;
        }
        return stream.charAt(pos++);
    }

    @Override
    public boolean isEmpty() {
        if (pos >= stream.length() && !scanner.hasNext()) {
            return true;
        }
        return false;
    }
}
