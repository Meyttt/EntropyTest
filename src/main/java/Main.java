import java.io.File;
import java.io.IOException;

/**
 * Created by svkreml on 01.02.2017.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        Entropy entropy = new Entropy();
        File directory = new File("data/compareData");
        File[] files = directory.listFiles();

        for (File oneFile : files) {
            entropy.ramoses.add(entropy.readRamos("data/compareData/" + oneFile.getName()));
        }

        entropy.allPreparing();
        entropy.getHW();
        Writer.write(entropy);
        System.out.println(false);

    }
}
