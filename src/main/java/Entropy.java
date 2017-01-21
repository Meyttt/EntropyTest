import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by Александра on 21.01.2017.
 */
public class Entropy {
    HashMap<String,Integer> allReferencies= new HashMap<>();
    HashMap<String,Integer> documentCounter = new HashMap<>();
    HashSet<String> popularWords = new HashSet<>();
    ArrayList<Ramos> ramoses = new ArrayList<>();
    HashMap<String,Double> Hw = new HashMap<>();
    int allLength =0;
    static class Ramos{
        @JsonView(Views.Normal.class)
        private String text;
        @JsonView(Views.Normal.class)
        private ArrayList<ArrayList<Word>> sentences = new ArrayList<>();
        HashMap<String, Integer> wordCount = new HashMap<>();
        public Integer length = 0;
        public Ramos(){}
        public void setWordCount(Set<String> stopWords) {
            for (ArrayList<Word> sentence : sentences) {
                for (Word word : sentence) {
                    String lem = word.getLem();
                    if(isAllowed(lem)){
                        length++;
                        if(lem.length()>=3&&(!stopWords.contains(lem))){
                            if(wordCount.containsKey(lem)){
                                wordCount.put(lem, wordCount.get(lem)+1);
                            }else{
                                wordCount.put(lem,1);
                            }
                        }
                    }
                }
            }
        }
        public void removeRareWords(Set<String> frequentWords){
            System.out.println(this.wordCount.keySet().size());
            Iterator iterator= this.wordCount.keySet().iterator();
            while(iterator.hasNext()){
                if(!frequentWords.contains(iterator.next())){
                    iterator.remove();
                }
            }
            System.out.println(this.wordCount.keySet().size());
            System.out.println("=====");
        }
        public boolean isAllowed(String currentString){
            for(int i=0; i<currentString.length();i++){
                if(Character.isLetter(currentString.charAt(i))){
                    return true;
                }
            }
            return false;
        }

    }
    public static class Word {
        @JsonView(Views.Normal.class)
        private String forma;
        @JsonView(Views.Normal.class)
        private String dom;
        @JsonView(Views.Normal.class)
        private String pos;
        @JsonView(Views.Normal.class)
        private int len;
        @JsonView(Views.Normal.class)
        private String lemma;
        @JsonView(Views.Normal.class)
        private String link;
        @JsonView(Views.Normal.class)
        private int posStart;
        @JsonView(Views.Normal.class)
        private String grm;

        String getLem(){return  this.lemma;}

        @Override
        public String toString() {
            return "Word{" +
                    "forma='" + forma + '\'' +
                    ", dom='" + dom + '\'' +
                    ", pos='" + pos + '\'' +
                    ", len=" + len +
                    ", lemma='" + lemma + '\'' +
                    ", link='" + link + '\'' +
                    ", posStart=" + posStart +
                    ", grm='" + grm + '\'' +
                    '}';
        }
    }
    public Ramos readRamos(String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Ramos ramos;
        ramos = mapper.readValue(new File(filename), Ramos.class);
        HashSet<String> stopWords = new HashSet<>();
        Scanner scanner = new Scanner(new FileInputStream("serviceData/stop_words_list.txt"));
        while(scanner.hasNext()){
            stopWords.add(scanner.next());
        }
        ramos.setWordCount(stopWords);
        return ramos;

    }

    public static void main(String[] args) throws IOException {
        Entropy entropy = new Entropy();
        File directory = new File("data/compareData");
        File[] files=directory.listFiles();
        for(File oneFile:files){
            entropy.ramoses.add(entropy.readRamos("data/compareData/"+oneFile.getName()));
        }

        entropy.allPreparing();
        entropy.getHW();
        System.out.println(false);

    }
    public void allPreparing(){
        allLength=mapsCalculation();
        Set<String> allFirstWords = documentCounter.keySet();
        for (String word:allFirstWords){
            if(documentCounter.get(word)>=3){
                popularWords.add(word);
            }
        }
        for (Ramos ramos:ramoses){
            ramos.removeRareWords(popularWords);
        }
        mapsCalculation();
    }
    public void getHW(){
        Set<String> allKeys= allReferencies.keySet();
        for(String word:allKeys){
            double hw=0;
            for(Ramos ramos:ramoses){
                if(ramos.wordCount.containsKey(word)){
                    double pdoc=(double)ramos.wordCount.get(word)/
                            (double)allReferencies.get(word);
                    hw+=pdoc*(Math.log10(1/pdoc));
                }
            }
            Hw.put(word, hw);
        }

    }
    public int mapsCalculation(){
        this.allReferencies.clear();
        this.documentCounter.clear();
        int allLength =0;
        for(Ramos ramos:this.ramoses){
            Set<String> words=ramos.wordCount.keySet();
            for(String word:words){
                allLength+=ramos.wordCount.get(word);
                if(this.documentCounter.containsKey(word)){
                    this.documentCounter.put(word,
                            this.documentCounter.get(word)+1);
                }else {
                    this.documentCounter.put(word,1);
                }
                if(this.allReferencies.containsKey(word)){
                    this.allReferencies.put(word,
                            this.allReferencies.get(word)+ramos.wordCount.get(word));
                }else{
                    this.allReferencies.put(word,ramos.wordCount.get(word));
                }
            }
        }
        return allLength;
    }


}
