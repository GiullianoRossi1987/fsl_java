package database;
import java.io.IOException;
import java.lang.Exception;
import java.sql.*;
import java.util.ArrayList;
import database.*;

public class ExtPackagesManager extends DatabaseManager{

    private static int WEB_INTERFACE_LINUX = 1;

    private static int WEB_INTERFACE_WGET = 1;
    private static int WEB_INTERFACE_CURL = 2;

    public static class ExtPackNotFound extends Exception{

        public ExtPackNotFound(String extp){
            super("Couldn't found external linux package ' "+ extp + "';");
        }
    }

    public static class ExtPackExistsErr extends Exception{

        public ExtPackExistsErr(String pack){
            super("Package reference \"" + pack + "\" already in use");
        }
    }

    public static class InvalidWebInterface extends Exception{

        public InvalidWebInterface(int interfacew){
            super("Invalid web interface: " + interfacew);
        }
    }

    public static class InstallationError extends Exception{

        public InstallationError(String ioeMessage){
            super("ERROR INSTALLING: " + ioeMessage);
        }
    }

    private boolean checkExtPackEx(String extpack) throws DatabaseNotLoadedYet, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            Statement checkCursor = this.databaseConnected.createStatement();
            ResultSet allExtP = checkCursor.executeQuery("SELECT COUNT(cd_extp) as tot from extpackages where nm_pack = \"" + extpack + "\";");
            return allExtP.getInt("tot") > 0;
        }
        catch(SQLException re){ throw new RuntimeDatabaseError(re.getMessage());}
    }

    public void addExtPack(String extpack, String link) throws DatabaseNotLoadedYet, RuntimeDatabaseError, ExtPackExistsErr{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(this.checkExtPackEx(extpack)) throw new ExtPackExistsErr(extpack);
            PreparedStatement cursorAdd = this.databaseConnected.prepareStatement("INSERT INTO extpackages (nm_pack, vl_link) VALUES (?, ?);");
            cursorAdd.setString(1, extpack);
            cursorAdd.setString(2, link);
            cursorAdd.executeUpdate();
        }
        catch(SQLException re){
            throw new RuntimeDatabaseError(re.getMessage());
        }
    }

    public void delExtPack(String extpack) throws DatabaseNotLoadedYet, ExtPackNotFound, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(!this.checkExtPackEx(extpack)) throw new ExtPackNotFound(extpack);
            PreparedStatement cursorDel = this.databaseConnected.prepareStatement("DELETE FROM extpackages WHERE nm_pack = ?;");
            cursorDel.setString(1, extpack);
            cursorDel.executeUpdate();

        }
        catch(SQLException re){
            throw new RuntimeDatabaseError(re.getMessage());
        }
    }

    public void chExtPackName(String extpack, String newName) throws DatabaseNotLoadedYet, ExtPackNotFound, ExtPackExistsErr, RuntimeDatabaseError{
        try{
           if(!this.gotDatabase) throw new ExtPackagesManager.DatabaseNotLoadedYet();
           if(!this.checkExtPackEx(extpack)) throw new ExtPackagesManager.ExtPackNotFound(extpack);
           if(this.checkExtPackEx(newName)) throw new ExtPackagesManager.ExtPackExistsErr(newName);
           PreparedStatement cursorCh = this.databaseConnected.prepareStatement("UPDATE extpackages SET nm_package = ? WHERE nm_pack = ?;");
           cursorCh.setString(1, newName);
           cursorCh.setString(2, extpack);
           cursorCh.executeUpdate();
        }
        catch(SQLException re){
            throw new ExtPackagesManager.RuntimeDatabaseError(re.getMessage());
        }
    }

    public void chExtPackLink(String extpack, String newLink) throws DatabaseNotLoadedYet, ExtPackNotFound, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(!this.checkExtPackEx(extpack)) throw new ExtPackNotFound(extpack);
            PreparedStatement cursorCh = this.databaseConnected.prepareStatement("UPDATE extpackages SET vl_link = ? WHERE nm_pack = ?;");
            cursorCh.setString(1, newLink);
            cursorCh.setString(2, extpack);
            cursorCh.executeUpdate();
        }
        catch(SQLException re){
            throw new RuntimeDatabaseError(re.getMessage());
        }
    }

    public String[] getPackData(String extpack) throws DatabaseNotLoadedYet, ExtPackNotFound, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(!this.checkExtPackEx(extpack)) throw new ExtPackNotFound(extpack);
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
            throw new RuntimeDatabaseError(re.getMessage());
        }
    }

    protected static void downloadLink(String link, String path, int webInterface) throws InstallationError, InvalidWebInterface, java.lang.InterruptedException{
        try{
            Runtime goToPath = Runtime.getRuntime();
            Runtime download = Runtime.getRuntime();
            int response;
            if(webInterface == ExtPackagesManager.WEB_INTERFACE_WGET) {
                ProcessBuilder procB = new ProcessBuilder("bash", "-c", "cd " + path + ";wget " + link).redirectErrorStream(true);
                Process proc = procB.start();
                response = proc.waitFor();
            }
            else if(webInterface == ExtPackagesManager.WEB_INTERFACE_CURL) {
                ProcessBuilder procB = new ProcessBuilder("bash", "-c", "cd " + path + ";curl " + link).redirectErrorStream(true);
                Process proc = procB.start();
                response = proc.waitFor();
            }
            else throw new InvalidWebInterface(webInterface);
            if(response != 0) throw new InstallationError("Couldn't install package from link: " + link);
        }
        catch(IOException ie){
            throw new InstallationError(ie.getMessage());
        }
    }

    public void downloadPackage(String extpack, String path) throws DatabaseNotLoadedYet, ExtPackNotFound, RuntimeDatabaseError, InstallationError, java.lang.InterruptedException{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(!this.checkExtPackEx(extpack)) throw new ExtPackNotFound(extpack);
            Statement cursorSel = this.databaseConnected.createStatement();
            cursorSel.setMaxRows(1);
            ResultSet packLinkP = cursorSel.executeQuery("SELECT vl_link FROM extpackages WHERE nm_pack = \"" + extpack +"\"");
            String packLink = packLinkP.getString("vl_link");
            downloadLink(packLink, path, ExtPackagesManager.WEB_INTERFACE_LINUX);
        }
        catch(SQLException re){
            throw new RuntimeException(re.getMessage());
        }
        catch (InvalidWebInterface iwe){
            throw new RuntimeDatabaseError(iwe.getMessage());
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
            throw new RuntimeDatabaseError(re.getMessage());
        }
    }

    public String getPackageByLink(String link) throws DatabaseNotLoadedYet, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            Statement cursorSelect = this.databaseConnected.createStatement();
            cursorSelect.setMaxRows(1);
            ResultSet result = cursorSelect.executeQuery("SELECT nm_pack FROM extpackages WHERE vl_link = \"" + link +"\";");
            return result.getString("nm_pack");
        }
        catch (SQLException re){
            throw new RuntimeDatabaseError(re.getMessage());
        }
    }

    public ArrayList<String> queryPackageByName(String name) throws DatabaseNotLoadedYet, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            Statement cursorQuery = this.databaseConnected.createStatement();
            ResultSet allQuery = cursorQuery.executeQuery("SELECT nm_pack FROM extpackages WHERE nm_pack LIKE \"%" + name + "%\";");
            ArrayList<String> results = new ArrayList<String>();
            while(allQuery.next()){ results.add(allQuery.getString("nm_pack")); }
            return results.size() > 0 ? results : null;
        }
        catch (SQLException re){
            throw new RuntimeDatabaseError(re.getMessage());
        }
    }

    public ExtPackagesManager(String path) throws DatabaseAlreadyConnected, RuntimeDatabaseError, InvalidDatabaseError{
        super(path);
    }

}