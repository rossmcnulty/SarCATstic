package net.gnomeffinway.sarcatstic;

import android.util.Log;
import android.widget.TextView;

public class Respondent {
    
    // Pastebin: http://pastebin.com/rtWtwhvx

    Quip[] quips = {};
    Quip[] backup = { new Quip("Hmm|Cat's got my tongue", -1) };
    int pos = 0;
    
    public void nextResponse(TextView top, TextView bottom) {
        if(!hasQuips()) {
            Log.d(MainActivity.TAG, "No quips in Respondent");
            quips = backup;
        }
        top.setText(quips[pos].toString().substring(0, quips[pos].toString().indexOf("|")));
        bottom.setText(quips[pos].toString().substring(quips[pos].toString().indexOf("|")+1));
        if(pos < quips.length-1)
            pos++;
        else
            pos = 0;
    }
    
    public Quip getCurrentQuip() {
        return quips[pos];
    }
    
    public void setQuips(Quip[] quips) {
        this.quips = quips;
    }
    
    public boolean hasQuips() {
        return quips.length > 0;
    }
    
    public void clearIndex() {
        pos = 0;
    }
    
}
