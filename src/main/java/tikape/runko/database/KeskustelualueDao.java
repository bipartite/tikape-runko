package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Keskustelualue;

/**
 *
 * @author VOL
 */
public class KeskustelualueDao implements Dao<Keskustelualue, Integer>{
    
    private Database database;
    
    public KeskustelualueDao(Database database){
        this.database = database;
    }

    /**
     * Returns one Keskustelualue-object from the database
     * found with the given key
     *
     * @param   key Objects key in the database
     * @return one Keskustelualue-object from the database
     * 
     * @throws SQLException
     */
    @Override
    public Keskustelualue findOne(Integer key) throws SQLException {
        Connection con = database.getConnection();
        
        PreparedStatement stmnt = con.prepareStatement("SELECT * FROM Keskutelualue WHERE id=?", key);
        
        //Find all the objects from the database with the given id (returns a list with only one or none objects in it)
        ResultSet rs = stmnt.executeQuery();
        
        int id = rs.getInt("id");
        String otsikko = rs.getString("nimi");
        
        return new Keskustelualue(id, otsikko);
    }

    /**
     * Returns a list of every Keskustelualue-object in the database
     * 
     * 
     * @return a list of every Keskustelualue-object in the database
     * @throws SQLException 
     */
    @Override
    public List<Keskustelualue> findAll() throws SQLException {
        Connection con = database.getConnection();
        
        //Get everything from the Keskustelualue-table        
        ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Keskustelualue");
        
        List<Keskustelualue> alueet = new ArrayList<Keskustelualue>();
        
        while(rs.next()){
            //Find the needed values from the current row in the table
            int id = rs.getInt("id");
            String otsikko = rs.getString("nimi");
            
            alueet.add(new Keskustelualue(id, otsikko));
        }
        
        return alueet;
    }

    /**
     * Deletes an object with the given id
     * from the Keskustelualue-table 
     * 
     * @param key the id of the object to delete
     * @throws SQLException 
     */
    @Override
    public void delete(Integer key) throws SQLException {
        database.getConnection().createStatement().execute("DELETE FROM Keskustelualue WHERE id='" + key + "';");
    }
    
    /**
     * Returns the amount of messages posted under the Keskustelualue
     * with the given key
     * 
     * @param key the id of the Keskustelualue in the database
     * @return Amount of messages under the Keskustelualue
     * 
     * @throws SQLException
     */
    public int getMessageAmount(Integer key) throws SQLException{
        // TODO: implement
        Connection con = database.getConnection();
        
        return 0;
    }
}
