package net.gnomeffinway.sarcatstic;

import android.util.Log;
import android.widget.TextView;

public class Respondent {
    
    public static final String TAG = Respondent.class.getSimpleName();
    
    // Pastebin: http://pastebin.com/rtWtwhvx

    Quip[] quips = { new Quip("Hmm|Cat's got my tongue", -1) };
    int pos = -1;
    
    public void nextResponse(TextView top, TextView bottom) {
        if(!hasQuips()) {
            Log.d(TAG, "No quips in Respondent");
            return;
        }
        
        if(pos < quips.length-1)
            pos++;
        else
            pos = 0;
        
        top.setText(quips[pos].toString().substring(0, quips[pos].toString().indexOf("|")));
        bottom.setText(quips[pos].toString().substring(quips[pos].toString().indexOf("|")+1));

    }
    
    public Quip getCurrentQuip() {
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
