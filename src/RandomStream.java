import java.util.Random;

/**
 * Created by Мирон on 07.11.2014 PACKAGE_NAME.
 */
public class RandomStream implements ICharStream {
    private String stream;
    private int Len;
    private int pos;
    public RandomStream(int alphabetSize, int N)  {
        Random random = new Random(System.nanoTime());
        Len = N;
        if (Len == 0) {
            Len = Math.abs(random.nextInt()) % 10000;
        }
        pos = 0;
        StringBuilder builder = new StringBuilder("");
        for (int i = 0; i < Len; i++) {
            builder.append((char) (Math.abs(random.nextInt()) % alphabetSize + 'a'));
        }
        stream = builder.toString();
    }
    public String getString() {
        return stream;
    }

    @Override
    public char getChar() {
        return stream.charAt(pos++);
    }

    @Override
    public boolean isEmpty() {
        return pos >= Len;
    }

    @Override
    public int streamSize() {
        return stream.length();
    }
}
