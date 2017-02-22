package tikape.runko.domain;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author VOL
 */
public class Keskustelualue {
    
    private int id;
    private String nimi;
    
    private int viesteja;
    
    private Date viimeisinViesti;
    
    private List<Keskusteluavaus> avaukset; 
    
    public Keskustelualue(int id, String nimi){
        this.id = id;
        this.nimi = nimi;
        viesteja = 0;
        avaukset = new ArrayList<>();
        viimeisinViesti = null;
    }
    
    public Keskustelualue(String nimi){
        System.out.println(nimi);
        this.id = -1;
        this.nimi = nimi;
        viesteja = 0;
        avaukset = new ArrayList<>();
        viimeisinViesti = null;
    }
    
    public int getId(){
        return id;
    }
    
    public String getNimi(){
        return nimi;
    }
    
    public int getViestimaara(){
        return viesteja;
    }
    
    /**
     * Returns the timestamp of the latest message in the Keskustelualue
     * 
     * @return The timestamp of the latest message in the Keskustelualue
     */
    public Date getViimeisinViesti(){
        return viimeisinViesti;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public void setNimi(String nimi){
        this.nimi = nimi;
    }
    
    public void setViestimaara(int viestimaara){
        this.viesteja = viestimaara;
    }
    
    public void setViimeisinViesti(Date julkaisuaika){
        viimeisinViesti = julkaisuaika;
    }
    
    public void addKeskusteluAvaus(Keskusteluavaus avaus){
        avaukset.add(avaus);
    }
}
