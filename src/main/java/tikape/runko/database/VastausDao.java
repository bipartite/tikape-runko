package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import tikape.runko.collector.VastausCollector;
import tikape.runko.domain.Keskustelualue;
import tikape.runko.domain.Vastaus;

/**
 *
 * @author VOL
 */
public class VastausDao implements Dao<Vastaus, Integer>{

    private Database database;
    
    public VastausDao(Database db){
        database = db;
    }
    
    @Override
    public Vastaus findOne(Integer key) throws SQLException {
        List<Vastaus> vastaus = this.database.queryAndCollect("SELECT * FROM Vastaus WHERE id = ?", new VastausCollector(), key);
        if (vastaus.isEmpty()) {
            return null;
        }

        return vastaus.get(0);
    }

    @Override
    public void save(Vastaus vastaus) throws SQLException {
        if(vastaus.getId() >= 0 && vastaus.getJulkaisuaika() != null){
            this.database.update("INSERT INTO Vastaus (id, avaus, teksti, nimimerkki, julkaisuaika) VALUES (?, ?, ?, ?, ?)", vastaus.getId(), vastaus.getAvausId(), vastaus.getTeksti(),
                vastaus.getNimimerkki(), vastaus.getJulkaisuaika());
        } else {
            this.database.update("INSERT INTO Vastaus (avaus, teksti, nimimerkki) VALUES (?, ?, ?)", vastaus.getAvausId(), vastaus.getTeksti(),
                vastaus.getNimimerkki());
        }
        
    }

    @Override
    public List<Vastaus> findAll() throws SQLException {
        return this.database.queryAndCollect("SELECT * FROM Vastaus", new VastausCollector());
    }

    @Override
    public void delete(Integer key) throws SQLException {
        this.database.update("DELETE FROM Vastaus WHERE id = ?", key);
    }
    
    /**
     * Returns the amount of messages that are in the specified Keskuseluavaus
     * 
     * @param key the id of the Keskusteluavaus
     * @return  The number of messages in the Keskusteluavaus
     */
    public int findTheAmountOfMessagesUnder(int key) throws SQLException{
        Connection con = database.getConnection();
        
        PreparedStatement ps = con.prepareStatement("SELECT count(id) AS viesteja FROM Vastaus WHERE avaus='" + key + "'");
        
        //Find the number of messages under the specified Keskusteluavaus
        ResultSet rs = ps.executeQuery();
        
        int viesteja = rs.getInt("viesteja");
        
        return viesteja;
    }
    
    /**
     * Returns the timestamp of the latest message in the specified Keskusteluavaus
     * 
     * @param key The id of the Keskusteluavaus
     * @return  The timestamp of the latest message in the given Keskusteluavaus
     * 
     * @throws SQLException
     */
    public Date findLatestMessageTimestampFromAvaus(int key) throws SQLException, ParseException{
        Connection con = database.getConnection();
        
        ResultSet rs = con.createStatement().executeQuery("SELECT datetime(julkaisuaika, 'localtime') AS julkaisuaika FROM Vastaus WHERE avaus='"+ key +"' ORDER BY julkaisuaika DESC");
        
        if(!rs.isClosed()){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
            //Set viimeisin's value to the value of the julkaisuaika of the first
            Date viimeisin = formatter.parse(rs.getString("julkaisuaika"));

            return viimeisin;
        }
        
        return null;
    }
    
    /**
     * Returns all the messages from the specified Keskusteluavaus
     * 
     * @param key The id of the Keskusteluavaus
     * @return A list of Vastaus-objects
     * 
     * @throws SQLException
     */
    public List<Vastaus> findAllInAvaus(int key) throws SQLException{        
        return database.queryAndCollect("SELECT id, avaus, teksti, nimimerkki, datetime(julkaisuaika, 'localtime') AS julkaisuaika FROM Vastaus WHERE avaus=? ORDER BY id DESC", new VastausCollector(), key);
    }
}
