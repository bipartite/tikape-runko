package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.collector.KeskustelualueCollector;
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
        List<Keskustelualue> alueet = this.database.queryAndCollect("SELECT * FROM Keskustelualue WHERE id = ?", new KeskustelualueCollector(), key);
        if (alueet.isEmpty()) {
            return null;
        }

        return alueet.get(0);
    }

     @Override
    public void save(Keskustelualue alue) throws SQLException {
        System.out.println("luodun keskustelualueen id >> "+ alue.getId());
        if(alue.getId() < 0){
            this.database.update("INSERT INTO Keskustelualue(nimi) VALUES(?)", alue.getNimi());
        } else {
            this.database.update("INSERT INTO Keskustelualue (id, nimi) VALUES (?, ?)", alue.getId(), alue.getNimi());
        }
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
        return this.database.queryAndCollect("SELECT * FROM Keskustelualue", new KeskustelualueCollector());
    }

    @Override
    public void delete(Integer key) throws SQLException {
        this.database.update("DELETE FROM Keskustelualue WHERE id = ?", key);
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
