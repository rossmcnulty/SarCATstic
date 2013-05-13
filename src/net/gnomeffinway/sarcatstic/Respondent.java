package net.gnomeffinway.sarcatstic;

import android.util.Log;

public class Respondent {
    
    public static final String TAG = Respondent.class.getSimpleName();
    
    // Pastebin: http://pastebin.com/rtWtwhvx

    Quip[] quips = { new Quip("Hmm|Cat's got my tongue", -1) };
    Quip currentQuip;
    int pos = -1;
    
    public void nextResponse() {
        if(!hasQuips()) {
            Log.d(TAG, "No quips in Respondent");
            return;
        }
        
        if(pos < quips.length-1)
            pos++;
        else
            pos = 0;
    }
    
    public void previousResponse() {
        if(!hasQuips()) {
            Log.d(TAG, "No quips in Respondent");
            return;
        }
        
        if(pos > 0)
            pos--;
        else
            pos = quips.length - 1;
    }
    
    public void setCurrentQuip(int pos) {
        this.pos = pos;
    }
    
    public Quip getQuipByPos(int pos) {
        return quips[pos];
    }
    
    public int getIndex() {
        return pos;
    }
    
    public String getCurrentTop() {
        return quips[pos].toString().substring(0, quips[pos].toString().indexOf("|"));
    }
    
    public String getCurrentBottom() {
        return quips[pos].toString().substring(quips[pos].toString().indexOf("|")+1);
    }
    
    public Quip getCurrentQuip() {
        if(pos == -1)
            return quips[0];
        return quips[pos];
    }
    
    public void setQuips(Quip[] quips) {
        this.quips = quips;
    }
    
    public boolean hasQuips() {
        return quips[0].getWebId() != -1;
    }
    
    public void clearIndex() {
        pos = 0;
    }
    
}
