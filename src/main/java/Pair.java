/**
 * Created by Александра on 05.02.2017.
 */
public class Pair {
    String first;
    String second;

    public Pair(String first, String second) {
        this.first = first;
        this.second = second;
    }

    public boolean equals(Pair pair){
        if ((pair.first.equalsIgnoreCase(this.first)&& pair.second.equalsIgnoreCase(this.second))||
                (pair.first.equalsIgnoreCase(this.second)&& pair.second.equalsIgnoreCase(this.first))){
            return true;
        }
        return false;
    }

    public boolean equals(Object o){
        if ((((Pair)o).first.equalsIgnoreCase(this.first)&& ((Pair)o).second.equalsIgnoreCase(this.second))||
                (((Pair)o).first.equalsIgnoreCase(this.second)&& ((Pair)o).second.equalsIgnoreCase(this.first))){
            return true;
        }
        return false;
    }
    public int hashCode(){
        return this.first.hashCode()+this.second.hashCode();
    }
}
