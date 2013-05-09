package net.gnomeffinway.sarcatstic;

public class Quip {
    
    private String quip;
    private long id;
    private long webId;
    
    public Quip(String quip, long webId) {
        this.quip = quip;
        this.webId = webId;
    }
    
    public Quip() {
        // TODO Auto-generated constructor stub
    }

    public long getId(){
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public long getWebId(){
        return webId;
    }
    
    public void setWebId(long webId) {
        this.webId = webId;
    }
    
    public String getQuip() {
        return quip;
    }
    
    public void setQuip(String quip) {
        this.quip = quip;
    }
    
    @Override
    public String toString() {
        return quip;
    }

}
