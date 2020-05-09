package database;

import java.io.IOException;
import java.sql.*;
import org.sqlite.*;
import java.util.ArrayList;
import java.lang.Exception;

public class PackagesManager extends DatabaseManager{

    public static class PackageNotFound extends Exception{

        public PackageNotFound(String pack){
            super("Error: couldn't find package \"" + pack + "\"");
        }
    }
    public static class PackageAlreadyExists extends Exception{

        public PackageAlreadyExists(String pack){
            super("Error: The package reference \"" + pack + "\" is already in use");
        }
    }
    public static class InvalidCommandError extends Exception{

        public InvalidCommandError(){
            super("Error: invalid command value!");
        }
    }

    public PackagesManager(String database) throws DatabaseAlreadyConnected, InvalidDatabaseError, RuntimeDatabaseError{
        super(database);
    }

    public static class InstallationError extends Exception{

        public InstallationError(String message){ super(message); }
    }

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
            if(this.checkPackageExists(pack)) throw new PackageAlreadyExists(pack);
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

    public void delPackage(String pack) throws DatabaseNotLoadedYet, RuntimeDatabaseError, PackageNotFound{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(!this.checkPackageExists(pack)) throw new PackageNotFound(pack);
            PreparedStatement cursorDel = this.databaseConnected.prepareStatement("DELETE FROM pacakges WHERE nm_pack = ?;");
            cursorDel.setString(1, pack);
            cursorDel.executeUpdate();
        }
        catch(SQLException re){
            throw new RuntimeDatabaseError(re.getMessage());
        }
    }

    public void chPackName(String pack, String newName) throws DatabaseNotLoadedYet, RuntimeDatabaseError, PackageNotFound, PackageAlreadyExists{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(!this.checkPackageExists(pack)) throw new PackageNotFound(pack);
            if(this.checkPackageExists(newName)) throw new PackageAlreadyExists(newName);
            PreparedStatement cursorCh = this.databaseConnected.prepareStatement("UPDATE packages SET nm_pack = ? WHERE nm_pack = ?;");
            cursorCh.setString(1, newName);
            cursorCh.setString(2, pack);
            cursorCh.executeUpdate();
        }
        catch(SQLException re){
            throw new RuntimeDatabaseError(re.getMessage());
        }
    }

    public void chPackShell(String pack, String newShell) throws DatabaseNotLoadedYet, RuntimeDatabaseError, PackageNotFound, InvalidCommandError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(!this.checkPackageExists(pack)) throw new PackageNotFound(pack);
            if(newShell.length() == 0) throw new InvalidCommandError();
            PreparedStatement cursorCh = this.databaseConnected.prepareStatement("UPDATE packages SET vl_shell = ? WHERE nm_pack = ?;");
            cursorCh.setString(1, newShell);
            cursorCh.setString(2, pack);
            cursorCh.executeUpdate();
        }
        catch(SQLException re){
            throw new RuntimeDatabaseError(re.getMessage());
        }
    }

    public static void executeShell(String shell) throws IOException, java.lang.InterruptedException, InstallationError {
        ProcessBuilder parentProcess = new ProcessBuilder("bash", "-c", shell).redirectErrorStream(true);
        Process process = parentProcess.start();
        int response = process.waitFor();
        if(response != 0){
            throw new InstallationError("Couldn't execute installation: " + shell);
        }
    }

    public String[] getPackageData(String pack) throws DatabaseNotLoadedYet, RuntimeDatabaseError, PackageNotFound{
        try {
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(!this.checkPackageExists(pack)) throw new PackageNotFound(pack);
            String[] packData = new String[3];
            Statement cursorSelect = this.databaseConnected.createStatement();
            cursorSelect.setMaxRows(1);
            ResultSet data = cursorSelect.executeQuery("SELECT * FROM packages WHERE nm_pack = \"" + pack + "\";");
            packData[0] = "" + data.getInt("cd_pack"); // package code
            packData[1] = data.getString("nm_pack");
            packData[2] = data.getString("vl_shell");
            return packData;

        } catch (SQLException e) {
            throw new RuntimeDatabaseError(e.getMessage());
        }
    }


    public ArrayList<String[]> qrPackageByName(String nameNeedle) throws DatabaseNotLoadedYet, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(nameNeedle.length() == 0) return null;
            Statement cursorQr = this.databaseConnected.createStatement();
            ResultSet resultsPure = cursorQr.executeQuery("SELECT * FROM packages WHERE nm_pack LIKE \"%" + nameNeedle + "%\"");
            ArrayList<String[]> results = new ArrayList<String[]>();
            int counterRow = 0;
            while(resultsPure.next()){
                String[] packageData = new String[3];
                packageData[0] = "" + resultsPure.getInt("cd_pack");
                packageData[1] = resultsPure.getString("nm_pack");
                packageData[2] = resultsPure.getString("vl_shell");
                results.add(packageData);
                counterRow++;
            }
            return results;
        }
        catch(SQLException re){ throw new RuntimeDatabaseError(re.getMessage()); }
    }

    public ArrayList<String[]> qrPackageByShell(String shellNeedle) throws DatabaseNotLoadedYet, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(shellNeedle.length() == 0) return null;
            Statement cursorQr = this.databaseConnected.createStatement();
            ResultSet resultsSet = cursorQr.executeQuery("SELECT * FROM packages WHERE vl_shell LIKE \""+ shellNeedle +"\";");
            ArrayList<String[]> results = new ArrayList<String[]>();
            while(resultsSet.next()){
                String[] row = new String[3];
                row[0] = "" + resultsSet.getInt("cd_pack");
                row[1] = resultsSet.getString("nm_pack");
                row[2] = resultsSet.getString("vl_shell");
                results.add(row);
            }
            return results;
        }
        catch (SQLException re){
            throw new RuntimeDatabaseError(re.getMessage());
        }
    }

    private boolean checkPackageExists(int cd_pack) throws DatabaseNotLoadedYet, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            Statement cursorSelect = this.databaseConnected.createStatement();
            cursorSelect.setMaxRows(1);
            ResultSet results = cursorSelect.executeQuery("SELECT COUNT(cd_pack) FROM packages WHERE cd_pack = " + cd_pack + ";");
            return results.getInt(1) == 1;
        }
        catch(SQLException re){
            throw new RuntimeDatabaseError(re.getMessage());
        }
    }

    public void installPackage(String pack) throws DatabaseNotLoadedYet, RuntimeDatabaseError, PackageNotFound, InstallationError, java.lang.InterruptedException, IOException{
        if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
        if(!this.checkPackageExists(pack)) throw new PackageNotFound(pack);
        String[] data = this.getPackageData(pack);
        PackagesManager.executeShell(data[2]);
    }
}