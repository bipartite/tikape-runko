/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.collector;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Yuuhaa
 */
public class IntegerCollector implements Collector<Integer>{
    
    @Override
    public Integer collect(ResultSet rs) throws SQLException {
        
        int viesteja = rs.getInt("viesteja");
        
        
        return viesteja;
    }
    
}
