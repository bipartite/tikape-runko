/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.collector;

import java.sql.ResultSet;
import java.sql.SQLException;
import tikape.runko.database.Collector;
import tikape.runko.domain.Keskustelualue;

/**
 *
 * @author Yuuhaa
 */
public class KeskustelualueCollector  implements Collector<Keskustelualue>{
    
    @Override
    public Keskustelualue collect(ResultSet rs) throws SQLException {
        return new Keskustelualue(rs.getInt("id"), rs.getString("nimi"));
    }
    
}
