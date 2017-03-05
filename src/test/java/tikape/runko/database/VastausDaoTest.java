/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.io.File;
import java.util.UUID;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Yuuhaa
 */
public class VastausDaoTest {
    private Database database;
    private VastausDao vastausDao;
    private String databaseName;

    @Before
    public void setUp() throws Exception {
        this.databaseName = "tmp" + UUID.randomUUID().toString().substring(0, 6)+".db";
        this.database = new Database("jdbc:sqlite:./" + this.databaseName);
        this.vastausDao = new VastausDao(this.database);
        
        
    }
    @After
    public void tearDown() throws Exception {
        if (new File(databaseName + ".mv.db").exists()) {
            new File(databaseName + ".mv.db").delete();
        }

        if (new File(databaseName + ".trace.db").exists()) {
            new File(databaseName + ".trace.db").delete();
        }
    }
    
    @Test
    public void findTheAmountOfMessagesUnderTest() throws Exception {

        int key = 1; //musiikki
        int oikea_vastaukset = 1;  
        int vastaukset = vastausDao.findTheAmountOfMessagesUnder(key);
        assertEquals(oikea_vastaukset, vastaukset);        
        
    }
    
}
