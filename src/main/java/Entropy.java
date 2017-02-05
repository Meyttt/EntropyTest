import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

    Set<String> topWords = new HashSet<>();

    Set<Pair> allPairs = new HashSet<>();
    HashMap<Pair, Integer> allPairReferencies = new HashMap<>();
    HashMap<Pair, Integer> documentPairCounter = new HashMap<>();
    //конец

    /**
     * Чтение файла в Ramos
     * @param filename
     * @return Ramos
     * @throws IOException
     *
     */
    public Ramos readRamos(String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Ramos ramos;
        ramos = mapper.readValue(new File(filename), Ramos.class);
        HashSet<String> stopWords = new HashSet<>();
        //todo нужно загрузить это один раз
        readStopWords(ramos, stopWords);
        return ramos;

    }
// вынес эти строки в метод, это нужно инициализировать только один раз
    private void readStopWords(Ramos ramos, HashSet<String> stopWords) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream("serviceData/stop_words_list.txt"));
        while (scanner.hasNext()) {
            stopWords.add(scanner.next());
        }
        ramos.setWordCount(stopWords);
    }


    /**
     * Подсчёт количества слов allLength
     * заполнение:
     *  allLength
     *  popularWords -- слова, которые есть в 3 и более документах
     * запуск mapsCalculation();
     */
    public void allPreparing() {
        allLength = mapsCalculation();
        Set<String> allFirstWords = documentCounter.keySet();
        popularWords.addAll(allFirstWords.stream().filter(word -> documentCounter.get(word) >= 3).collect(Collectors.toList()));
        for (Ramos ramos : ramoses) {
            ramos.removeRareWords(popularWords);
        }
        mapsCalculation();
    }

    /**
     * Информационная энтропия показывает равномерность распределения термина w в документах коллекции
     * (4)
     * заполняется:
     *  HashMap<String, Double> hw
     * используется:
     *  ArrayList<Ramos> ramoses
     *  HashMap<String, Integer> allReferencies
     */
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


    /**
     * Индикатор, основанный на Дивергенции Кульбака — Лейблера, рассчитывается для слов и словосочетаний.
     * Он характеризует различие между реальным распределением термина w с теоретическим, в соответствии с длиной документа (чем документ больше,
     * тем больше в нём различных терминов, а значит больше вероятность случайного попадания термина w в документ d).
     * (1-4)
     * заполняется:
     *  HashMap<String, Double> dw
     * используется:
     *  ArrayList<Ramos> ramoses
     *  HashMap<String, Integer> allReferencies
     *  int allLength
     */
    public void getDW() {
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

    /**
     * Веса на основе распределения Бернулли
     * Данный тип индикаторов основывается на сравнении реального распределения терминов в коллекции с теоретическим распределением Бернулли.
     * Мы используем веса W1 и W2 в качестве индикаторов
     * (5-14)
     * заполняется:
     *  HashMap<String, Double> w1s = new HashMap<>();
     *  HashMap<String, Double> w2s = new HashMap<>();
     *  HashMap<String, Double> dfe1 = new HashMap<>();
     *  HashMap<String, Double> dfe2 = new HashMap<>();
     * используется:
     *  ArrayList<Ramos> ramoses
     *  HashMap<String, Integer> allReferencies
     *  int allLength
     */
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

    /**
     * Пока не использован
     * @param hashMap<String, Double>
     */
    private LinkedHashMap<String,Integer> getRang(HashMap<String, Double> hashMap) {
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
            if( !(pare.getValue()>=current-0.00001||pare.getValue()<=current+0.00001)){
                number++;
            }
            linkedHashMap.put(pare.getKey(),number);
        }
        return linkedHashMap;
    }
    protected void getBest(HashMap<String,Integer> sumRang, int n) {
        List<Map.Entry<String, Integer>> hList = new ArrayList<Map.Entry<String, Integer>>(sumRang.entrySet());
        hList.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return Integer.compare(o1.getValue(), o2.getValue());
            }
        });
        for (int i=0;i<n;i++){
            topWords.add(hList.get(i).getKey());
        }

    }

    /**
     *
     * @return словарь с ключем - элементом и значением - суммарным рангом.
     */
    public HashMap<String,Integer> getSumRang(){
        LinkedHashMap<String,Integer> Hrang = getRang(hw);
        LinkedHashMap<String,Integer> W1rang = getRang(w1s);
        LinkedHashMap<String,Integer> W2Rang = getRang(w2s);
        LinkedHashMap<String,Integer> DF1rang = getRang(dfe1);
        LinkedHashMap<String,Integer> DF2rang = getRang(dfe2);
        Set<String> allKeys=Hrang.keySet();
        HashMap<String,Integer> sumRang = new HashMap<>();
        for(String key:allKeys){
            sumRang.put(key,Hrang.get(key)*7+W1rang.get(key)+W2Rang.get(key)+DF1rang.get(key)+DF2rang.get(key));
        }
        return sumRang;

    }

    /**
     * Метод для подсчёта числа вхождений слов и нахождения их суммарного количества
     *
     * @return allLength
     *
     * заполняется:
     *   HashMap<String, Integer> allReferencies = new HashMap<>();
     *   HashMap<String, Integer> documentCounter = new HashMap<>();
     *   HashSet<String> popularWords = new HashSet<>();
     * используется:
     *  ArrayList<Ramos> ramoses
     */
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
    public void pairsMapsCalculation(){
        Set<String> sentenceText = new HashSet<>();
        for(Ramos ramos:ramoses){
            for(ArrayList<Word> sentence:ramos.sentences){
                for(Word word:sentence){
                    sentenceText.add(word.getLem());
                }
                sentenceText.retainAll(popularWords);
                Pair pair;
                ArrayList<String> wordList = new ArrayList<>(sentenceText);
                for(int i=0; i<wordList.size()-1;i++){
                    for(int j=i+1;j<wordList.size();j++){
                        pair = new Pair(wordList.get(i),wordList.get(j));
                        allPairs.add(pair);
                        if(ramos.pairCounter.containsKey(pair)){
                            ramos.pairCounter.put(pair,ramos.pairCounter.get(pair)+1);
                        }else {
                            ramos.pairCounter.put(pair,1);
                        }
                        if(allPairReferencies.containsKey(pair)){
                            allPairReferencies.put(pair,allPairReferencies.get(pair)+1);
                        }else{
                            allPairReferencies.put(pair,1);
                        }
                    }
                }
                sentenceText.clear();
            }
            for(Pair pair:allPairs){
                if(documentPairCounter.containsKey(pair)){
                    documentPairCounter.put(pair,documentPairCounter.get(pair)+1);
                }else{
                    documentPairCounter.put(pair,1);
                }
            }
        }
        Pair pair = (Pair)allPairs.toArray()[1];
        for(Pair pair1:allPairs){
            if(pair1.equals(pair)){
                System.out.println("WTF?");
            }
        }
    }
    //// TODO: 05.02.2017 А как обрабатывается ситуация, если в предложении слова встречается дважды?




}
