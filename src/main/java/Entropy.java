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
        double p = (double) 1 / (double)ramoses.size();
        double q = (double) ((double)ramoses.size() - (double)1) / (double)ramoses.size();
        Set<String> allKeys = allReferencies.keySet();
        for(String word: allKeys){
            double probWX = 0;
            for(Ramos ramos:ramoses){
                if(ramos.wordCount.containsKey(word)){
                    double prob1=((double)allReferencies.get(word)/(double)ramos.wordCount.get(word))*
                            Math.pow(p,(double)ramos.wordCount.get(word))*Math.pow(q,((double)allReferencies.get(word)-(double)ramos.wordCount.get(word)));
                    double probWD=Math.pow((double)2,(double)-1*((double)Math.log(prob1)/(double)Math.log(2)));
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
                            Math.pow(p,(double)ramos.wordCount.get(word))*Math.pow(q,(double)(allReferencies.get(word)-ramos.wordCount.get(word)));
                    double probWD=Math.pow((double)2,(double)-1*((double)Math.log(prob1)/(double)Math.log(2)));
                    double probNorm = probWD/probWX;
                    ramos.probnorms.put(word,probNorm);
                    Wrisk2wd = ((allReferencies.get(word))*((double)-1*(Math.log(probNorm)/(double)Math.log(2))))/(documentCounter.get(word)*(ramos.wordCount.get(word)+1));
                    Wrisk1wd = ((double)-1*(Math.log10(probNorm)/Math.log10(2))/((double)ramos.wordCount.get(word)+(double)1));
                    W2w+=Wrisk2wd;
                    W1w+=Wrisk1wd;

                }
            }
            w1s.put(word,W1w);
            w2s.put(word,W2w);
            double Dfe1 = 0;
            double Dfe2 = 0;
            double pdoc, Wrisk2norm;
            for(Ramos ramos:ramoses){
                if(ramos.wordCount.containsKey(word)){
                    pdoc=((double)ramos.wordCount.get(word)/(double)allReferencies.get(word));
                    Dfe1+=pdoc*(Math.log10(pdoc/ramos.probnorms.get(word))/Math.log10(2));
                    Wrisk2wd = ((allReferencies.get(word))*((double)-1*(Math.log(ramos.probnorms.get(word))/(double)Math.log(2))))/(documentCounter.get(word)*(ramos.wordCount.get(word)+1));
                    Wrisk2norm= Wrisk2wd/w2s.get(word);
                    Dfe2+=pdoc*(Math.log10(pdoc/Wrisk2norm)/Math.log10(2));
                }
            }
            dfe1.put(word,Dfe1);
            dfe2.put(word,Dfe2);

        }

    }

    private void getRang(HashMap<String, Double> hashMap) {
        List<Map.Entry<String, Double>> hList = new ArrayList<Map.Entry<String, Double>>(hashMap.entrySet());
        int j = 0;
        //Создание сортированного списка пар ключ-значение из исходного словаря для последовательной обработки
        hList.sort(new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                return Double.compare(o1.getValue(), o2.getValue());
            }
        });
        double current = 0;
        int number = 1;
        LinkedHashMap<String,Integer> linkedHashMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> pare : hList) {
            if( pare.getValue()!=current){
                number++;
            }
            linkedHashMap.put(pare.getKey(),number);
        }
    }

    public HashMap<String,Integer> getSumRang(){
        return null;
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
