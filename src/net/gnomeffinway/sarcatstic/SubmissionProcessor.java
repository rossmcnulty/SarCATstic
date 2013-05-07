package net.gnomeffinway.sarcatstic;

public class SubmissionProcessor {

    final int MAX_TOP = 40;
    final int MAX_BOTTOM = 40;
    final int MAX_CHARACTERS = 75;
    
    public boolean validateText(String top, String bottom) {

        if(top.indexOf("|") != -1 || bottom.indexOf("|") != -1)
            return false;
        if(top.length() > MAX_TOP || bottom.length() > MAX_BOTTOM)
            return false;
        String s = top + bottom;
        if(s.length() > MAX_CHARACTERS)
            return false;
        
        return true;
    }
    
    public String conjoin(String top, String bottom) {
        return top + "|" + bottom;
    }
    
    //TODO: GSON/toJSON formatting
    
}
