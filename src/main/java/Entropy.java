import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by Александра on 21.01.2017.
 */
public class Entropy {
    HashMap<String, Integer> allReferencies = new HashMap<>();
    HashMap<String, Integer> documentCounter = new HashMap<>();
    HashSet<String> popularWords = new HashSet<>();
    ArrayList<Ramos> ramoses = new ArrayList<>();
    HashMap<String, Double> hw = new HashMap<>();
    int allLength = 0;

    HashMap<String, Double> w1s = new HashMap<>();
    HashMap<String, Double> w2s = new HashMap<>();
    HashMap<String, Double> dfe1 = new HashMap<>();
    HashMap<String, Double> dfe2 = new HashMap<>();
    HashMap<String, Double> dw = new HashMap<>();





    public Ramos readRamos(String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Ramos ramos;
        ramos = mapper.readValue(new File(filename), Ramos.class);
        HashSet<String> stopWords = new HashSet<>();
        Scanner scanner = new Scanner(new FileInputStream("serviceData/stop_words_list.txt"));
        while (scanner.hasNext()) {
            stopWords.add(scanner.next());
        }
        ramos.setWordCount(stopWords);
        return ramos;

    }


    public void allPreparing() {
        allLength = mapsCalculation();
        Set<String> allFirstWords = documentCounter.keySet();
        for (String word : allFirstWords) {
            if (documentCounter.get(word) >= 3) {
                popularWords.add(word);
            }
        }
        for (Ramos ramos : ramoses) {
            ramos.removeRareWords(popularWords);
        }
        mapsCalculation();
    }

    public void getHW() {
        Set<String> allKeys = allReferencies.keySet();
        for (String word : allKeys) {
            double hw = 0;
            for (Ramos ramos : ramoses) {
                if (ramos.wordCount.containsKey(word)) {
                    double pdoc = (double) ramos.wordCount.get(word) /
                            (double) allReferencies.get(word);
                    hw += pdoc * (Math.log10(1 / pdoc));
                }
            }
            this.hw.put(word, hw);
        }

    }

    public int mapsCalculation() {
        this.allReferencies.clear();
        this.documentCounter.clear();
        int allLength = 0;
        for (Ramos ramos : this.ramoses) {
            Set<String> words = ramos.wordCount.keySet();
            for (String word : words) {
                allLength += ramos.wordCount.get(word);
                if (this.documentCounter.containsKey(word)) {
                    this.documentCounter.put(word,
                            this.documentCounter.get(word) + 1);
                } else {
                    this.documentCounter.put(word, 1);
                }
                if (this.allReferencies.containsKey(word)) {
                    this.allReferencies.put(word,
                            this.allReferencies.get(word) + ramos.wordCount.get(word));
                } else {
                    this.allReferencies.put(word, ramos.wordCount.get(word));
                }
            }
        }
        return allLength;
    }


}
