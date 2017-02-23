package tikape.runko.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private String databaseAddress;
    private boolean debug;
    private Connection connection;

    public Database(String driver, String databaseAddress) throws Exception {
        Class.forName(driver);
        this.connection = DriverManager.getConnection(databaseAddress);
        this.databaseAddress = databaseAddress;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseAddress);
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
