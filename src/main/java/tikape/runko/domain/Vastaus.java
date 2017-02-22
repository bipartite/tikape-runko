package tikape.runko.domain;

import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author VOL
 */
public class Vastaus {
    
    private int id;
    private int avaus;
    private String teksti;
    private String nimimerkki;
    private Timestamp julkaisuaika;
    
    public Vastaus(int id, int avaus, String teksti, String nimimerkki, Timestamp julkaisuaika){
        this.id = id;
        this.avaus = avaus;
        this.teksti = teksti;
        this.nimimerkki = nimimerkki;
        this.julkaisuaika = julkaisuaika;
    }
    
    public int getId(){
        return id;
    }
    
    public int getAvausId(){
        return avaus;
    }
    
    public String getTeksti(){
        return teksti;
    }
    
    public String getNimimerkki(){
        return nimimerkki;
    }
    
    public Timestamp getJulkaisuaika(){
        return julkaisuaika;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public void setAvaus(int avausId){
        avaus = avausId;
    }
    
    public void setTeksti(String teksti){
        this.teksti = teksti;
    }
    
    public void setNimimerkki(String nimimerkki){
        this.nimimerkki = nimimerkki;
    }
    
    public void setJulkaisuaika(Timestamp julkaisuaika){
        this.julkaisuaika = julkaisuaika;
    }
}