package database;

import java.util.ArrayList;
import java.sql.*;
import org.sqlite.*;
import database.ExtPackagesManager.InstallationError;


class WinPackages extends DatabaseManager{

    public static class WinPackageAlreadyExists extends Exception{}
    public static class WinPackageNotFound extends Exception{}

    public WinPackages(String database) throws DatabaseAlreadyConnected, RuntimeDatabaseError, InvalidDatabaseError{ super(database);}

    private boolean checkWinExists(String winpack) throws DatabaseNotLoadedYet, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            Statement cursorCk = this.databaseConnected.createStatement();
            cursorCk.setMaxRows(1);
            ResultSet results = cursorCk.executeQuery("SELECT COUNT(cd_extp) FROM winpackages WHERE nm_pckg = \"" + winpack + "\";");
            return results.getInt(1) > 0;
        }
        catch (SQLException re){ throw new RuntimeDatabaseError(re.getMessage());}
    }

    public static void downloadPackage(String link) throws java.io.IOException{
        Runtime downloadCmd = Runtime.getRuntime();
        downloadCmd.exec("curl " + link);
    }


}