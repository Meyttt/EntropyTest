import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by svkreml on 21.12.2016.
 */
public class Writer {


    /**
     * Статичный метод для запичи в "output.csv"
     * Для правильной записи всех параметров неодходимые словари должны быть добавленны сюда
     * @param entropy
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    static void write(Entropy entropy) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("output.csv", "Cp1251");
        writer.println("words;e_w;F;df;H;W2;W1;D4;D3;sum_rank;D1;dw");
        for (String key : entropy.hw.keySet()) {
            writer.print(key);
            writer.print(';');
            writer.print(0);
            writer.print(';');//e_w
            writer.print(0);
            writer.print(';');//F
            writer.print(0);
            writer.print(';');//df
            writer.print(entropy.hw.get(key));
            writer.print(';');//H
            writer.print(entropy.w2s.get(key));
            writer.print(';');//W2
            writer.print(entropy.w1s.get(key));
            writer.print(';');//W1
            writer.print(0);
            writer.print(';');//D4
            writer.print(0);
            writer.print(';');//D3
            writer.print(0);
            writer.print(';');//sun_ran
            writer.print(0);//D1
            writer.print(';');
            writer.println(entropy.dw.get(key));//dw            writer.print(';');//sun_rank
        }
        writer.close();
        System.out.println("output.csv saved");
    }
}
