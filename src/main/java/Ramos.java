import com.fasterxml.jackson.annotation.JsonView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by svkreml on 01.02.2017.
 */
class Ramos{
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
