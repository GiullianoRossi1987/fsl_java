package database;
import java.sql.*;
import org.sqlite.*;
import java.util.ArrayList;
import java.lang.Exception;
import java.lang.Runtime;

class PackagesManager extends DatabaseManager{

    public static class PackageNotFound extends Exception{}
    public static class PackageAlreadyExists extends Exception{}
    public static class InvalidCommandError extends Exception{}

    private boolean checkPackageExists(String pack) throws DatabaseNotLoadedYet, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            Statement cursorSelect = this.databaseConnected.createStatement();
            cursorSelect.setMaxRows(1);
            ResultSet queryCheck = cursorSelect.executeQuery("SELECT count(cd_pack) AS tot FROM packages WHERE nm_pack = \"" + pack +"\";");
            return queryCheck.getInt("tot") > 0;
        }
        catch(SQLException re){
            throw new RuntimeDatabaseError(re.getMessage());
        }
    }

    public void addPackage(String pack, String command) throws DatabaseNotLoadedYet, RuntimeDatabaseError, PackageAlreadyExists, InvalidCommandError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(this.checkPackageExists(pack)) throw new PackageAlreadyExists();
            if(command.length() == 0) throw new InvalidCommandError();
            PreparedStatement cursorAdd = this.databaseConnected.prepareStatement("INSERT INTO packages (nm_pack, vl_shell) VALUES (?, ?);");
            cursorAdd.setString(1, pack);
            cursorAdd.setString(2, command);
            cursorAdd.executeUpdate();
            // System.gc();
        }
        catch(SQLException re){
            throw new RuntimeDatabaseError(re.getMessage());
        }
    }
}