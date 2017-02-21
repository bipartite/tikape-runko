package tikape.runko.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Yuuhaa
 * 
 */
public interface Collector<T> {
    
    T collect(ResultSet rs) throws SQLException;
    
}
