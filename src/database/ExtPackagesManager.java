package database;
import java.io.IOException;
import java.lang.Exception;
import java.sql.*;
import java.util.ArrayList;

public class ExtPackagesManager extends DatabaseManager{

    private static int WEB_INTERFACE_LINUX = 1;

    private static int WEB_INTERFACE_WGET = 1;
    private static int WEB_INTERFACE_CURL = 2;

    public static class ExtPackNotFound extends Exception{}

    public static class ExtPackExistsErr extends Exception{}

    public static class InvalidLink extends Exception{}

    public static class InvalidWebInterface extends Exception{}

    public static class LinkAlreadyInUse extends Exception{}

    public static class InstallationError extends Exception{}

    private boolean checkExtPackEx(String extpack) throws DatabaseNotLoadedYet, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            Statement checkCursor = this.databaseConnected.createStatement();
            ResultSet allExtP = checkCursor.executeQuery("SELECT COUNT(cd_pack) as tot from extpackages where nm_pack = \"" + extpack + "\";");
            return allExtP.getInt("tot") > 0;
        }
        catch(SQLException re){ throw new RuntimeDatabaseError();}
    }

    public void addExtPack(String extpack, String link) throws DatabaseNotLoadedYet, RuntimeDatabaseError, ExtPackExistsErr{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(this.checkExtPackEx(extpack)) throw new ExtPackExistsErr();
            PreparedStatement cursorAdd = this.databaseConnected.prepareStatement("INSERT INTO extpackages (nm_pack, vl_link) VALUES (?, ?);");
            cursorAdd.setString(1, extpack);
            cursorAdd.setString(2, link);
            cursorAdd.executeUpdate();
        }
        catch(SQLException re){
            throw new RuntimeDatabaseError();
        }
    }

    public void delExtPack(String extpack) throws DatabaseNotLoadedYet, ExtPackNotFound, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(!this.checkExtPackEx(extpack)) throw new ExtPackNotFound();
            PreparedStatement cursorDel = this.databaseConnected.prepareStatement("DELETE FROM extpackages WHERE nm_pack = ?;");
            cursorDel.setString(1, extpack);
            cursorDel.executeUpdate();

        }
        catch(SQLException re){
            throw new RuntimeDatabaseError();
        }
    }

    public void chExtPackName(String extpack, String newName) throws DatabaseNotLoadedYet, ExtPackNotFound, ExtPackExistsErr, RuntimeDatabaseError{
        try{
           if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
           if(!this.checkExtPackEx(extpack)) throw new ExtPackNotFound();
           if(this.checkExtPackEx(newName)) throw new ExtPackExistsErr();
           PreparedStatement cursorCh = this.databaseConnected.prepareStatement("UPDATE extpackages SET nm_package = ? WHERE nm_pack = ?;");
           cursorCh.setString(1, newName);
           cursorCh.setString(2, extpack);
           cursorCh.executeUpdate();
        }
        catch(SQLException re){
            throw new RuntimeDatabaseError();
        }
    }

    public void chExtPackLink(String extpack, String newLink) throws DatabaseNotLoadedYet, ExtPackNotFound, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(!this.checkExtPackEx(extpack)) throw new ExtPackNotFound();
            PreparedStatement cursorCh = this.databaseConnected.prepareStatement("UPDATE extpackages SET vl_link = ? WHERE nm_pack = ?;");
            cursorCh.setString(1, newLink);
            cursorCh.setString(2, extpack);
            cursorCh.executeUpdate();
        }
        catch(SQLException re){
            throw new RuntimeDatabaseError();
        }
    }

    public String[] getPackData(String extpack) throws DatabaseNotLoadedYet, ExtPackNotFound, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(!this.checkExtPackEx(extpack)) throw new ExtPackNotFound();
            Statement cursorSel = this.databaseConnected.createStatement();
            cursorSel.setMaxRows(1);
            ResultSet packageData = cursorSel.executeQuery("SELECT * FROM extpackages WHERE nm_pack = \"" + extpack + "\";");
            String[] packData = new String[4];
            packData[0] = "" + packageData.getInt("cd_pack");  // package code
            packData[1] = packageData.getString("nm_pack");    // package name
            packData[2] = packageData.getString("vl_link");    // package download link
            packData[3] = packageData.getInt("vl_works") == 0 ? "FALSE" : "TRUE";  // if the link works;
            return packData;
        }
        catch(SQLException re) {
            throw new RuntimeDatabaseError();
        }
    }

    protected static void downloadLink(String link, String path) throws InstallationError{
        try{
            Runtime goToPath = Runtime.getRuntime();
            Runtime download = Runtime.getRuntime();
            goToPath.exec("cd " + path);
            download.exec("wget " + link);
        }
        catch(IOException ie){
            throw new InstallationError();
        }
    }

    public void downloadPackage(String extpack) throws DatabaseNotLoadedYet, ExtPackNotFound, RuntimeDatabaseError, InstallationError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(!this.checkExtPackEx(extpack)) throw new ExtPackNotFound();
            Statement cursorSel = this.databaseConnected.createStatement();
            cursorSel.setMaxRows(1);
            ResultSet packLinkP = cursorSel.executeQuery("SELECT vl_link FROM extpackages WHERE nm_pack = \"" + extpack +"\"");
            String packLink = packLinkP.getString("vl_link");
        }
        catch(SQLException re){
            throw new RuntimeException();
        }
    }

    public ArrayList<String[]> getAllEPacks() throws DatabaseNotLoadedYet, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            ArrayList<String[]> tmpallEPacks = new ArrayList<String[]>();
            Statement cursorSel = this.databaseConnected.createStatement();
            ResultSet allPacks = cursorSel.executeQuery("SELECT * FROM extpackages;");
            while(allPacks.next()){
                String[] packData = new String[3];
                packData[0] = "" + allPacks.getInt("cd_pack");
                packData[1] = allPacks.getString("nm_pack");
                packData[2] = allPacks.getString("vl_link");
                tmpallEPacks.add(packData);
            }
            return tmpallEPacks;
        }
        catch (SQLException re){
            throw new RuntimeDatabaseError();
        }
    }

    public String getPackageByLink(String link) throws DatabaseNotLoadedYet, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            
        }
        catch (SQLException re){
            throw new RuntimeDatabaseError();
        }
    }
}