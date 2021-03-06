package tikape.runko.database;

import tikape.runko.collector.Collector;
import java.sql.*;
import java.util.*;
import java.net.*;

public class Database {

    private String databaseAddress;
    private boolean debug;
    private Connection connection;

    public Database(String databaseAddress) throws Exception {
        //   Class.forName(driver);
        this.connection = DriverManager.getConnection(databaseAddress);
        this.databaseAddress = databaseAddress;
        init();
    }

    /**
     * Luodaan database jos ei ole vielÃ¤ olemassa. Postgrelauseet herokua varten
     */
    private void init() throws SQLException {
        connection = getConnection();
        
        List<String> lauseet = null;
        if (this.databaseAddress.contains("postgres")) {
            lauseet = postgreLauseet();
        } else {
            lauseet = sqliteLauseet();
        }
        // "try with resources" sulkee resurssin automaattisesti lopuksi
            Statement st = connection.createStatement();

            // suoritetaan komennot
            for (String lause : lauseet) {
                System.out.println("Running command >> " + lause);
                st.executeUpdate(lause);
            }
            st.close();
        

    }

    public Connection getConnection() throws SQLException {
        if (this.databaseAddress.contains("postgres")) {
            try {
                URI dbUri = new URI(databaseAddress);

                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

                return DriverManager.getConnection(dbUrl, username, password);
            } catch (Throwable t) {
                System.out.println("Error: " + t.getMessage());
                t.printStackTrace();
            }
        }

        return connection;
    }

    private List<String> postgreLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjÃ¤rjestyksessÃ¤
        // heroku kÃ¤yttÃ¤Ã¤ SERIAL-avainsanaa uuden tunnuksen automaattiseen luomiseen
        lista.add("CREATE TABLE Keskusteluavaus (id SERIAL PRIMARY KEY NOT NULL, alue INTEGER, otsikko VARCHAR(50) NOT NULL, FOREIGN KEY(alue) REFERENCES Keskustelualue(id));");
        lista.add("CREATE TABLE Keskustelualue(id SERIAL PRIMARY KEY NOT NULL, nimi VARCHAR(20) NOT NULL);");
        lista.add("CREATE TABLE Vastaus(id SERIAL PRIMARY KEY NOT NULL, avaus INTEGER, teksti VARCHAR(1000) NOT NULL, nimimerkki VARCHAR(20) NOT NULL, julkaisuaika TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(avaus) REFERENCES Keskusteluavaus(id));");

        lista.add("INSERT INTO Keskustelualue (nimi) VALUES ('Musiikki');");
        lista.add("INSERT INTO Keskustelualue (nimi) VALUES ('Tietokoneet');");
        lista.add("INSERT INTO Keskusteluavaus (alue, otsikko) VALUES (1, 'pop on jees!');");
        lista.add("INSERT INTO Vastaus (avaus, teksti, nimimerkki, julkaisuaika) VALUES (1, 'nii on klasariki', 'kalle', '01.07.1970 01:00:00');");
        return lista;
    }

    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjÃ¤rjestyksessÃ¤
        /*lista.add("CREATE TABLE Keskustelualue(id INTEGER PRIMARY KEY NOT NULL, nimi VARCHAR(20) NOT NULL);");

        lista.add("CREATE TABLE Keskusteluavaus(id INTEGER PRIMARY KEY NOT NULL, alue INTEGER, otsikko VARCHAR(50) NOT NULL, FOREIGN KEY(alue) REFERENCES Keskustelualue(id));");
        lista.add("CREATE TABLE Vastaus(id INTEGER PRIMARY KEY NOT NULL, avaus INTEGER, teksti VARCHAR(1000) NOT NULL, nimimerkki VARCHAR(20) NOT NULL, julkaisuaika TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(avaus) REFERENCES Keskusteluavaus(id));");

        lista.add("INSERT INTO Keskustelualue (nimi) VALUES ('Musiikki');");
        lista.add("INSERT INTO Keskustelualue (nimi) VALUES ('Tietokoneet');");

       lista.add("INSERT INTO Keskusteluavaus (alue, otsikko) VALUES (1, 'pop on jees!');");
       lista.add("INSERT INTO Vastaus (avaus, teksti, nimimerkki) VALUES (1, 'nii on klasariki', 'kalle');");*/

        return lista;
    }

    public void setDebugMode(boolean b) {
        debug = b;
    }
    /**
     * 
     * @param <T>
     * @param query
     * @param col
     * @param params
     * @return List<T> rows or null 
     * @throws SQLException 
     */
    public <T> List<T> queryAndCollect(String query, Collector<T> col, Object... params) throws SQLException {
            connection = getConnection();
            List<T> rows;
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                if (debug) {
                    System.out.println("---");
                    System.out.println("Executing: " + query);
                    System.out.println("---");
                }
                rows = new ArrayList<>();
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        if (debug) {
                            System.out.println("---");
                            System.out.println(query);
                            debug(rs);
                            System.out.println("---");
                        }
                        rows.add(col.collect(rs));

                    }
                    rs.close();
                    stmt.close();
                }
                stmt.close();
            }
            
            return rows;
    }
    /**
     * 
     * @param updateQuery
     * @param params
     * @return 0 if none changed or # changed rows
     * @throws SQLException 
     */
    public int update(String updateQuery, Object... params) throws SQLException {
        int changes = 0;
            try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }

                changes = stmt.executeUpdate();

                stmt.close();
                
                if (debug) {
                    System.out.println("---");
                    System.out.println(updateQuery);
                    System.out.println("Changed rows: " + changes);
                    System.out.println("---");
                }
            }

            return changes;
    }

    private void debug(ResultSet rs) throws SQLException {
        int columns = rs.getMetaData().getColumnCount();
        for (int i = 0; i < columns; i++) {
            System.out.print(
                    rs.getObject(i + 1) + ":"
                    + rs.getMetaData().getColumnName(i + 1) + "  ");
        }

        System.out.println();
    }
}
