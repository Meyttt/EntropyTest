package p;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by svkreml on 21.12.2016.
 */
public class Writer {
    HashMap<String, Double> w1s = new HashMap<>();
    HashMap<String,Double> w2s = new HashMap<>();
    HashMap<String,Double> dfe1 = new HashMap<>();
    HashMap<String,Double> dfe2 = new HashMap<>();
    HashMap<String,Double> hw = new HashMap<>();
    HashMap<String, Double> dw = new HashMap<>();




    void write() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("output.csv", "Cp1251");
        writer.println("words;e_w;F;df;H;W2;W1;D4;D3;sum_rank;D1");
        for (String key: hw.keySet()){
            writer.print(key);writer.print(';');
            writer.print(0);writer.print(';');//e_w
            writer.print(0);writer.print(';');//F
            writer.print(0);writer.print(';');//df
            writer.print(hw.get(key));writer.print(';');//H
            writer.print(w2s.get(key));writer.print(';');//W2
            writer.print(w1s.get(key));writer.print(';');//W1
            writer.print(0);writer.print(';');//D4
            writer.print(0);writer.print(';');//D3
            writer.print(0);writer.print(';');//sun_rank
            writer.println(0);//D1
        }
        writer.close();
    }
}
