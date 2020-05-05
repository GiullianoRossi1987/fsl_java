package database;
import org.sqlite.*;
import java.sql.*;

public class DatabaseManager {
    private static String MAIN_DATABASE = "packages.db";
    protected Connection databaseConnected;
    protected boolean gotDatabase;

    protected static class DatabaseAlreadyConnected extends Exception{}

    protected static class DatabaseNotLoadedYet extends Exception{}

    protected static class InvalidDatabaseError extends Exception{}

    protected static class RuntimeDatabaseError extends Exception{}

    public DatabaseManager(){
        this.databaseConnected = null;
        this.gotDatabase = false;
    }

    private static boolean checkDatabase(String database){
        Connection tmpConnection = null;
        try{
            tmpConnection = DriverManager.getConnection("jdbc:sqlite3:" + database);
            String structureLs = "SELECT name FROM sqlite_master WHERE type = \"table\";";
            Statement cursor = tmpConnection.createStatement();
            ResultSet struct = cursor.executeQuery(structureLs);
            while(struct.next()) {
                String table = struct.getString("name");
                String[] tables = {"packages", "extpackages", "winpackages"};
                for(int i = 0; i < 3; i++){
                    if(!table.equals(tables[i])) return false;
                }
            }
            return true;
        }
        catch(SQLException e){ return false;}
    }

    public DatabaseManager(String database) throws InvalidDatabaseError, DatabaseAlreadyConnected, RuntimeDatabaseError{
        if(!checkDatabase(database)) throw new InvalidDatabaseError();
        if(this.gotDatabase) throw new DatabaseAlreadyConnected();
        try{
            this.databaseConnected = DriverManager.getConnection("jdbc:sqlite3:" + database);
            this.gotDatabase = true;
        }
        catch(SQLException ex){
            throw new RuntimeDatabaseError();
        }
    }

    public Connection getDatabaseConnected(){ return this.databaseConnected}

    public boolean getGotDatabase(){ return this.gotDatabase;}

    public void setDatabaseConnected(Connection value){
        this.databaseConnected = value;
        this.gotDatabase = true;
    }

    public void setGotDatabase(boolean value){
        this.gotDatabase = value;
    }
}
