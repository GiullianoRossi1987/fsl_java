package database;
import org.sqlite.*;
import java.sql.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DatabaseManager {

    protected Connection databaseConnected;
    protected boolean gotDatabase;
    protected String[] connectionData = {null, null, null};

    public static class DatabaseAlreadyConnected extends Exception{

        public DatabaseAlreadyConnected(){
            super("ERROR: DATABASE ALREADY CONNECTED AND UNREACHABLE FOR CONNECT");
        }
    }

    public static class DatabaseNotLoadedYet extends Exception{

        public DatabaseNotLoadedYet(){ super("ERROR: DATABASE NOT CONNECTED"); }
    }

    public static class InvalidDatabaseError extends Exception{

        public InvalidDatabaseError(String message){
            super("ERROR: INVALID DATABASE {" + message + "}");
        }
    }

    public static class RuntimeDatabaseError extends Exception{

        public RuntimeDatabaseError(String message){
            super("DATABASE ACTION ERROR: " + message);
        }
    }

    public DatabaseManager(){
        this.databaseConnected = null;
        this.gotDatabase = false;
        this.connectionData[0] = null;
        this.connectionData[1] = null;
        this.connectionData[2] = null;
    }

    public DatabaseManager(String database) throws DatabaseAlreadyConnected, RuntimeDatabaseError{
        if(this.gotDatabase) throw new DatabaseAlreadyConnected();
        try{
            Class.forName("org.sqlite.JDBC");
            this.databaseConnected = DriverManager.getConnection("jdbc:sqlite:" + database);
            this.gotDatabase = true;
            this.connectionData[0] = database;
            this.connectionData[1] = "v.3";
            this.connectionData[2] = "org.sqlite.JDBC";
        }
        catch(Exception ex){
            throw new RuntimeDatabaseError(ex.getMessage());
        }
    }

    @Nullable public Connection getDatabaseConnected(){ return this.databaseConnected; }

    public boolean getGotDatabase(){ return this.gotDatabase;}

    public void setDatabaseConnected(Connection value){
        this.databaseConnected = value;
        this.gotDatabase = true;
    }

    public void setGotDatabase(boolean value){
        this.gotDatabase = value;
    }
    @NotNull public String[] getConnectionData(){ return this.connectionData;}
    @Nullable public String getDatabaseLoaded(){ return this.connectionData[0];}
    @Nullable public String getDriverVersion(){ return this.connectionData[1];}
    @Nullable public String getDriverUsing(){ return this.connectionData[2];}
}
