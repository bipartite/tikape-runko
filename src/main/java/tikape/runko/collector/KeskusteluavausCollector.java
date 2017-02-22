/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.collector;

import java.sql.ResultSet;
import java.sql.SQLException;
import tikape.runko.database.Collector;
import tikape.runko.domain.Keskusteluavaus;

/**
 *
 * @author Yuuhaa
 */
public class KeskusteluavausCollector implements Collector<Keskusteluavaus> {
    
    @Override
    public Keskusteluavaus collect(ResultSet rs) throws SQLException {
        return new Keskusteluavaus(rs.getInt("id"), rs.getInt("alue"), rs.getString("otsikko"));
    }
    
}
