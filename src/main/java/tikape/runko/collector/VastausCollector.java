/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.collector;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import tikape.runko.database.Collector;
import tikape.runko.domain.Vastaus;

/**
 *
 * @author Yuuhaa
 */
public class VastausCollector implements Collector<Vastaus> {
    
    @Override
    public Vastaus collect(ResultSet rs) throws SQLException {
            return new Vastaus(rs.getInt("id"), rs.getInt("avaus") , rs.getString("teksti"), rs.getString("nimimerkki"), new Timestamp(0));
    }
}
