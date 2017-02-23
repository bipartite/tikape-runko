package tikape.runko.domain;

import com.sun.org.apache.bcel.internal.generic.AALOAD;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.print.attribute.standard.DateTimeAtCompleted;

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
    public String getViimeisinViesti(){
        if(viimeisinViesti == null){
            return "";
        }
        
        DateParser parser = new DateParser();
        
        String aika = parser.parseDate(viimeisinViesti);
        
        return aika;
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
