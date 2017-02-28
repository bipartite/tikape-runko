package tikape.runko.database;

import java.sql.*;
import java.util.*;
import java.net.*;


public class Database {

    private String databaseAddress;
    private boolean debug;
    private Connection connection;

    public Database(String databaseAddress) throws Exception {
     //   Class.forName(driver);
    //    this.connection = DriverManager.getConnection(databaseAddress);
        this.databaseAddress = databaseAddress;
        init();
    }
    /**
     * Luodaan database jos ei ole vielä olemassa. Postgrelauseet herokua varten
     */
    public void init(){
        List<String> lauseet = null;
        if (this.databaseAddress.contains("postgres")) {
            lauseet = postgreLauseet();
        } else {
            lauseet = sqliteLauseet();
        }
        // "try with resources" sulkee resurssin automaattisesti lopuksi
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();

            // suoritetaan komennot
            for (String lause : lauseet) {
                System.out.println("Running command >> " + lause);
                st.executeUpdate(lause);
            }

        } catch (Throwable t) {
            // jos tietokantataulu on jo olemassa, ei komentoja suoriteta
            System.out.println("Error >> " + t.getMessage());
        }
        
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

        return DriverManager.getConnection(databaseAddress);
    }
    
    private List<String> postgreLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
      
        // heroku käyttää SERIAL-avainsanaa uuden tunnuksen automaattiseen luomiseen
        lista.add("CREATE TABLE Keskusteluavaus (id SERIAL PRIMARY KEY NOT NULL, alue INTEGER, otsikko VARCHAR(50) NOT NULL, FOREIGN KEY(alue) REFERENCES Keskustelualue(id));");
        lista.add("CREATE TABLE Keskustelualue(id SERIAL PRIMARY KEY NOT NULL, nimi VARCHAR(20) NOT NULL);");
        lista.add("CREATE TABLE Vastaus(id SERIAL PRIMARY KEY NOT NULL, avaus INTEGER, teksti VARCHAR(1000) NOT NULL, nimimerkki VARCHAR(20) NOT NULL, julkaisuaika TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(avaus) REFERENCES Keskusteluavaus(id));");
        
        lista.add("INSERT INTO Keskustelualue (nimi) VALUES ('Musiikki')");
        lista.add("INSERT INTO Keskustelualue (nimi) VALUES ('Tietokoneet')");
        return lista;
    }

    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        lista.add("CREATE TABLE Keskusteluavaus(id INTEGER PRIMARY KEY NOT NULL, alue INTEGER, otsikko VARCHAR(50) NOT NULL, FOREIGN KEY(alue) REFERENCES Keskustelualue(id));");
        lista.add("CREATE TABLE Keskustelualue(id INTEGER PRIMARY KEY NOT NULL, nimi VARCHAR(20) NOT NULL);");
        lista.add("CREATE TABLE Vastaus(id INTEGER PRIMARY KEY NOT NULL, avaus INTEGER, teksti VARCHAR(1000) NOT NULL, nimimerkki VARCHAR(20) NOT NULL, julkaisuaika TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY(avaus) REFERENCES Keskusteluavaus(id));");

        lista.add("INSERT INTO Keskustelualue (nimi) VALUES ('Musiikki')");
        lista.add("INSERT INTO Keskustelualue (nimi) VALUES ('Tietokoneet')");
        
        lista.add("INSERT INTO Keskusteluavaus (alue, otsikko) VALUES (1, 'pop on jees!');");
        
        return lista;
    }
    

    public void setDebugMode(boolean b) {
        debug = b;
    }
    
    public <T> List<T> queryAndCollect(String query, Collector<T> col, Object... params) throws SQLException {
        connection = getConnection();
        if (debug) {
            System.out.println("---");
            System.out.println("Executing: " + query);
            System.out.println("---");
        }
        List<T> rows = new ArrayList<>();
        PreparedStatement stmt = connection.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        
        ResultSet rs = stmt.executeQuery();

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
        
        return rows;
    }
    
    public int update(String updateQuery, Object... params) throws SQLException {
        connection = getConnection();
        
        PreparedStatement stmt = connection.prepareStatement(updateQuery);

        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }

        int changes = stmt.executeUpdate();

        if (debug) {
            System.out.println("---");
            System.out.println(updateQuery);
            System.out.println("Changed rows: " + changes);
            System.out.println("---");
        }
        stmt.close();
        
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
