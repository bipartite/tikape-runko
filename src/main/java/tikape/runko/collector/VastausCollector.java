/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.collector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import tikape.runko.domain.Vastaus;

/**
 *
 * @author Yuuhaa
 */
public class VastausCollector implements Collector<Vastaus> {
    
    @Override
    public Vastaus collect(ResultSet rs) throws SQLException {
        if(!rs.isClosed()){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            Date d = null;
            try {
                //Set viimeisin's value to the value of the julkaisuaika of the first
                d = formatter.parse(rs.getString("julkaisuaika"));
            } catch (ParseException ex) {
                Logger.getLogger(VastausCollector.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            if(d == null){
                return new Vastaus(rs.getInt("id"), rs.getInt("avaus") , rs.getString("teksti"), rs.getString("nimimerkki"), new Date());
            } else {
                return new Vastaus(rs.getInt("id"), rs.getInt("avaus") , rs.getString("teksti"), rs.getString("nimimerkki"), d);
            }
        }
        return null;
    }
}
