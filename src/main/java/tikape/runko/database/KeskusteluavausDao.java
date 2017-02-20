package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Keskusteluavaus;

/**
 *
 * @author VOL
 */
public class KeskusteluavausDao implements Dao<Keskusteluavaus, Integer>{
    
    private Database database;
    
    public KeskusteluavausDao(Database database){
        this.database = database;
    }

    /**
     * Returns the Keskusteluavaus-object from the database
     * with the given id
     * 
     * @param key the id of the wanted object
     * @return one Keskusteluavaus-object from the database
     * @throws SQLException 
     */
    @Override
    public Keskusteluavaus findOne(Integer key) throws SQLException {
        Connection con = database.getConnection();
        
        PreparedStatement stmnt = con.prepareStatement("SELECT * FROM Keskusteluavaus WHERE id=?", key);
        
        //Finds all the objects from the database with the given id(returns a list with one or none objects in it)
        ResultSet rs = stmnt.executeQuery();
        
        //Get the needed information from the object
        int id = rs.getInt("id");
        int alue = rs.getInt("alue");
        String otsikko = rs.getString("otsikko");
        
        return new Keskusteluavaus(id, alue, otsikko);
    }

    /**
     * Returns every Keskusteluavaus-object from the Keskusteluavaus-table in the database
     * 
     * @return every Keskusteluavaus-object from the database
     * @throws SQLException 
     */
    @Override
    public List<Keskusteluavaus> findAll() throws SQLException {
        //Find all the rows in the Keskusteluavaus-table
        ResultSet rs = database.getConnection()
                .createStatement()
                .executeQuery("SELECT * FROM Keskusteluavaus");
        
        List<Keskusteluavaus> avaukset = new ArrayList<Keskusteluavaus>();
        
        while(rs.next()){
            //Get the needed information from the object
            int id = rs.getInt("id");
            int alue = rs.getInt("alue");
            String otsikko = rs.getString("otsikko");
            
            avaukset.add(new Keskusteluavaus(id, alue, otsikko));
        }
        
        return avaukset;
    }

    /**
     * Delete an Keskusteluavaus-object with the given id from the database
     * 
     * @param key the id of the object to be deleted
     * @throws SQLException 
     */
    @Override
    public void delete(Integer key) throws SQLException {
        database.getConnection().createStatement().execute("DELETE FROM Keskusteluavaus WHERE id='" + key + "'");
    }
    
    /**
     * Returns every Keskusteluavaus-object that are in the given Keskustelualue from the database
     * 
     * @param key the id of the Keskustelualue where the Keskusteluavaus-objects are wanted from
     * @return A list of Keskusteluavaus-objects that are under the specified Keskustelualue
     */
    public List<Keskusteluavaus> findAllFromAlue(int key) throws SQLException{
        Connection con = database.getConnection();
        
        PreparedStatement ps = con.prepareStatement("SELECT * FROM Keskusteluavaus WHERE alue = '" + key +"'");
        
        //Find all Keskusteluavaus-objects where the alue is the same as the given key
        ResultSet rs = ps.executeQuery();
        
        List<Keskusteluavaus> avaukset = new ArrayList<>();
        
        while(rs.next()){
            //Get the needed information from the object
            int id = rs.getInt("id");
            int alue = rs.getInt("alue");
            String otsikko = rs.getString("otsikko");
            
            avaukset.add(new Keskusteluavaus(id, alue, otsikko));
        }
        
        return avaukset;
    }
    
    
}
