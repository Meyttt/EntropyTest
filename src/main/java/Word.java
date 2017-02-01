
import com.fasterxml.jackson.annotation.JsonView;

/**
 * Created by svkreml on 01.02.2017.
 */
public class Word {
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