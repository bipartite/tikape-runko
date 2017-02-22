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
    
    /**
     * Returns one Vastaus-object from the database
     * found with the given key
     *
     * @param   key Objects key in the database
     * @return one Vastaus-object from the database
     * 
     * @throws SQLException
     */
    @Override
    public Vastaus findOne(Integer key) throws SQLException {
        Connection con = database.getConnection();
        
        PreparedStatement stmnt = con.prepareStatement("SELECT * FROM Vastaus WHERE id=?", key);
        
        //Find all the objects from the database with the given id (returns a list with only one or none objects in it)
        ResultSet rs = stmnt.executeQuery();
        
        int id = rs.getInt("id");
        int avaus = rs.getInt("avaus");
        String teksti = rs.getString("teksti");
        String nimimerkki = rs.getString("nimimerkki");
        Timestamp julkaisuaika = rs.getTimestamp("julkaisuaika");
        
        return new Vastaus(id, 0, teksti, nimimerkki, julkaisuaika);
    }

    /**
     * Returns all the Vastaus-objects from the database
     * 
     * @return Every Vastaus-object from the Vastaus-table in the database
     * @throws SQLException 
     */
    @Override
    public List<Vastaus> findAll() throws SQLException {
        Connection con = database.getConnection();
        
        //Get everything from the Vastaus-table        
        ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Vastaus");
        
        List<Vastaus> vastaukset = new ArrayList<Vastaus>();
        
        while(rs.next()){
            //Find the needed values from the current row in the table
            int id = rs.getInt("id");
            int avaus = rs.getInt("avaus");
            String teksti = rs.getString("teksti");
            String nimimerkki = rs.getString("nimimerkki");
            Timestamp julkaisuaika = rs.getTimestamp("julkaisuaika");
            
            vastaukset.add(new Vastaus(id, 0, teksti, nimimerkki, julkaisuaika));
        }
        
        return vastaukset;
    }

    /**
     * Deletes the object with the given id from the database
     * 
     * @param key th id of the object to be deleted
     * @throws SQLException 
     */
    @Override
    public void delete(Integer key) throws SQLException {
        database.getConnection().createStatement().execute("DELETE FROM Vastaus WHERE id='" + key + "'");
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
    public Timestamp findLatestMessageTimestampFromAvaus(int key) throws SQLException, ParseException{
        Connection con = database.getConnection();
        
        ResultSet rs = con.createStatement().executeQuery("SELECT julkaisuaika FROM Vastaus WHERE avaus='"+ key +"' ORDER BY julkaisuaika DESC");
        
        //Set viimeisin's value to the value of the julkaisuaika of the first
        
        //Ei jostain syystä toimi. Antaa tästä kohdasta parseExceptionin ja SQLExcpetionin. Pitää korjata.
        Timestamp viimeisin = rs.getTimestamp("julkaisuaika");
        
        return viimeisin;
    }
}
