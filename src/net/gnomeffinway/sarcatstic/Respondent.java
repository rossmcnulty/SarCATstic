package net.gnomeffinway.sarcatstic;

import android.widget.TextView;

public class Respondent {
    
    // Pastebin: http://pastebin.com/rtWtwhvx

    String[] quips = {};
    String[] backup = {"Hmm|Cat's got my tongue"};
    int pos = 0;
    
    public void nextResponse(TextView top, TextView bottom) {
        if(!hasQuips())
            quips = backup;
        top.setText(quips[pos].substring(0, quips[pos].indexOf("|")));
        bottom.setText(quips[pos].substring(quips[pos].indexOf("|")+1));
        if(pos < quips.length-1)
            pos++;
        else
            pos = 0;
    }
    
    public void setQuips(String[] quips) {
        this.quips = quips;
    }
    
    public boolean hasQuips() {
        return quips.length > 0;
    }
    
    public void clearIndex() {
        pos = 0;
    }
    
}
