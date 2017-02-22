package tikape.runko.domain;

import java.sql.Timestamp;
import java.util.List;

/**
 *
 * @author VOL
 */
public class Keskusteluavaus {
    
    private int id;
    private int alue;
    private String otsikko;
    private List<Vastaus> vastaukset;
    private int viestimaara;
    
    private Timestamp viimeisinViesti;
    
    public Keskusteluavaus(int id, int alue, String otsikko){
        this.id = id;
        this.alue = alue;
        this.otsikko = otsikko;
        
        viestimaara = 0;
        viimeisinViesti = null;
    }
    
    public int getId(){
        return id;
    }
    
    public int getAlueId(){
        return alue;
    }

    public String getOtsikko(){
        return otsikko;
    }
    
    public int getViestimaara(){
        return viestimaara;
    }
    
    /**
     * Returns the timestamp of the latest message in the Keskusteluavaus
     * 
     * @return The timestamp of the latest message in the Keskusteluavaus
     */
    public Timestamp getViimeisinViesti(){
        return viimeisinViesti;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public void setAlue(int alueid){
        alue = alueid;
    }
    
    public void setOtsikko(String otsikko){
        this.otsikko = otsikko;
    }
    
    public void setViestimaara(int maara){
        viestimaara = maara;
    }
 
    public void setViimeisinViesti(Timestamp julkaisuaika){
        viimeisinViesti = julkaisuaika;
    }
}
