package sample.Automate;

import java.util.ArrayList;

/**
 * Created by amine_err on 02/08/2015.
 */
public class RE {
    String exp;
    ArrayList<String> alphabet;

    public RE(){}
    public RE(String exp){this.exp = exp;}
    public RE(String exp, ArrayList<String> alphabet) {
        this.exp = exp;
        this.alphabet = alphabet;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public ArrayList<String> getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(ArrayList<String> alphabet) {
        this.alphabet = alphabet;
    }

    public boolean isSimple(){
        return alphabet.contains(exp) || exp.equalsIgnoreCase(Trans.Epsilon);
    }

    private AF toAF2(){
        System.out.println(exp);
        if( isSimple() ){
            AF res = new AF(); res.setAlphabet(alphabet);
            State e = new State(); e.setInitial(true);
            State f = new State(); f.setFinal(true);
            Trans t = new Trans(exp);
            t.setFrom(e);
            t.setTo(f);
            e.addTansition(t);
            res.add(e);
            res.add(f);
            return res;
        }
        else{
            if( exp.charAt(0)=='(' && exp.charAt(exp.length()-1)==')' ){
                return new RE(exp.substring(1,exp.length()-1),alphabet).toAF2();
            }
            for(int i=0;i<exp.length();i++){
                if( exp.charAt(i)=='(' ){
                    int k = 0;i++;
                    while( !(k==0 && exp.charAt(i)==')') ){
                        //System.out.println("i : "+i+", c : "+exp.charAt(i)+", bool : "+(k==0 && exp.charAt(i)==')'));
                        if( exp.charAt(i)=='(' )
                            k++;
                        else if( exp.charAt(i)==')' )
                            k--;
                        i++;
                        //System.out.println("i : "+i+", c : "+exp.charAt(i)+", bool : "+(k==0 && exp.charAt(i)==')'));
                    }
                    i++;//System.out.println("OUT");
                }
                if( exp.length()>i && exp.charAt(i)=='|' ){
                    return new RE(exp.substring(0,i),alphabet).toAF2().unify(new RE(exp.substring(i+1, exp.length()),alphabet).toAF2());
                }
            }
            for(int i=0;i<exp.length();i++){
                if( exp.charAt(i)=='(' ){
                    int k = 0;i++;
                    while( !(k==0 && exp.charAt(i)==')') ){
                        if( exp.charAt(i)=='(' )
                            k++;
                        else if( exp.charAt(i)==')' )
                            k--;
                        i++;
                    }
                    i++;
                }
                if( exp.length()>i && exp.charAt(i)=='.' ){
                    AF r = new RE(exp.substring(0,i),alphabet).toAF2();
                    //System.out.println(r);
                    //System.out.println(exp.substring(i + 1, exp.length() ));
                    AF s = new RE(exp.substring(i + 1, exp.length() ),alphabet).toAF2();
                    return r.merge(s);
                }
            }
            for(int i=0;i<exp.length();i++){
                if( exp.charAt(i)=='(' ){
                    int k = 0;i++;
                    while( !(k==0 && exp.charAt(i)==')') ){
                        if( exp.charAt(i)=='(' )
                            k++;
                        else if( exp.charAt(i)==')' )
                            k--;
                        i++;
                    }
                    i++;
                }
                if( exp.length()>i && exp.charAt(i)=='*' ){
                    return new RE(exp.substring(0,i),alphabet).toAF2().loop();
                }
            }
        }
        return null;
    }

    public AF toAF(){
        AF af = toAF2();
        ArrayList<State> states = af.getQ();
        int i=0;
        for (State state : states ){
            state.setName("e"+i);i++;
            state.setAf(af);
        }
        return af;
    }

    public void compile(boolean alphabetTo) throws Exception {
        //if syntax correct
        // not empty
        if(exp.equals("")){
            throw new Exception("Empty RegExp Exception");
        }
        // no 2 [*,.,|] in a row
        if( exp.contains("**") ){
            throw new Exception("Two '*' in a row Exception");
        }
        if( exp.contains("..") ){
            throw new Exception("Two '.' in a row Exception");
        }
        if( exp.contains("||") ){
            throw new Exception("Two '|' in a row Exception");
        }
        // brackets compilation
        int k = 0;
        for(int i=0; i<exp.length(); i++){
            if( exp.charAt(i)=='(' ){
                k++;
            }
            if( exp.charAt(i)==')' ){
                k--;
            }
            if( k<0 ){
                throw new Exception("Brackets Exception: "+exp.substring(Math.max(i-3,0),Math.min(i + 3, exp.length())));
            }
        }
        if( k>0 ){
            throw new Exception("Brackets Exception");
        }
        //if expr in alphabet
        if( alphabetTo ){
            StringBuffer s = new StringBuffer(exp);
            for( int i=0; i<s.length(); i++ ){
                if( s.charAt(i)=='*' || s.charAt(i)=='(' || s.charAt(i)==')' ){
                    s.replace(i,i+1,"");
                    i--;
                }else if( s.charAt(i)=='|' || s.charAt(i)=='.' ){
                    s.replace(i,i+1,",");
                    i--;
                }
            }
            String st[] = s.toString().split(",");
            for( String s1 : st ){
                if( !alphabet.contains(s1) && !s1.equals(Trans.Epsilon) ){
                    throw new Exception("Alphabet Exception: '"+s1+"' does not exist.");
                }
            }
        }
    }
}
