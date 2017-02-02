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
    //осторожно начало бреда
    HashMap<String, Double> w1s = new HashMap<>();
    HashMap<String, Double> w2s = new HashMap<>();
    HashMap<String, Double> dfe1 = new HashMap<>();
    HashMap<String, Double> dfe2 = new HashMap<>();
    HashMap<String, Double> dw = new HashMap<>();
    //конец

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

    public void getHW() { //(4)  Информационная энтропия
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


    //осторожно начало бреда
    public void getDW() { //(1) Дивергенция Кульбака — Лейблера
        Set<String> allKeys = allReferencies.keySet();
        for (String word : allKeys) {
            double dw = 0;
            for (Ramos ramos : ramoses) {
                if (ramos.wordCount.containsKey(word)) {
                    double pdoc = (double) ramos.wordCount.get(word) / (double) allReferencies.get(word);
                    double pnd = (double) ramos.length / (double) allLength;
                    dw += pdoc * (Math.log10(pdoc / pnd));
                }
            }
            this.dw.put(word, dw);
        }
    }
    //<не> конец

    public void getWW(){
        double p = (double) 1 / ramoses.size();
        double q = (double) (ramoses.size() - 1) / ramoses.size();
        Set<String> allKeys = allReferencies.keySet();
        for(String word: allKeys){
            double probWX = 0;
            for(Ramos ramos:ramoses){
                if(ramos.wordCount.containsKey(word)){
                    double prob1=((double)allReferencies.get(word)/(double)ramos.wordCount.get(word))*
                            Math.pow(p,ramos.wordCount.get(word))*Math.pow(q,(allReferencies.get(word)-ramos.wordCount.get(word)));
                    double probWD=Math.pow(2,(double)-1*((double)Math.log10(prob1)/(double)Math.log10(2)));
                    probWX+=probWD;

                }
            }
            double Wrisk2wd=0;
            double Wrisk1wd=0;
            Double W1w= Double.valueOf(0);
            Double W2w= Double.valueOf(0);
            for(Ramos ramos:ramoses){
                if(ramos.wordCount.containsKey(word)){
                    double prob1=((double)allReferencies.get(word)/(double)ramos.wordCount.get(word))*
                            Math.pow(p,ramos.wordCount.get(word))*Math.pow(q,(allReferencies.get(word)-ramos.wordCount.get(word)));
                    double probWD=Math.pow(2,(double)-1*((double)Math.log10(prob1)/(double)Math.log10(2)));
                    double probNorm = probWD/probWX;
                    ramos.probnorms.put(word,probNorm);
                    Wrisk2wd = ((allReferencies.get(word))*((double)-1*(Math.log10(probNorm)/Math.log10(2))))/(documentCounter.get(word)*ramos.wordCount.get(word));
                    Wrisk1wd = ((double)-1*(Math.log10(probNorm)/Math.log10(2))/(ramos.wordCount.get(word)+1));
                    W2w+=Wrisk2wd;
                    W1w+=Wrisk1wd;
                }
            }
            w1s.put(word,W1w);
            w2s.put(word,W2w);
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
