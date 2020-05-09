package database;
import java.io.IOException;
import java.util.ArrayList;
import java.sql.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sqlite.*;
import database.ExtPackagesManager.InstallationError;


class WinPackages extends DatabaseManager{

    public static class WinPackageAlreadyExists extends Exception{

        public WinPackageAlreadyExists(String wpack){
            super("The windows package '" + wpack + "' already exists");
        }
    }
    public static class WinPackageNotFound extends Exception{

        public WinPackageNotFound(String wpack){
            super("The can't windows package '" + wpack +"'");
        }
    }

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

    public static void downloadPackage(String link) throws InstallationError{
        Runtime downloadCmd = Runtime.getRuntime();
        try {
            downloadCmd.exec("curl " + link);
        } catch (IOException e) {
            throw new InstallationError(e.getMessage());
        }
    }


    public void addWinP(String winpackage, String linkDownload) throws DatabaseNotLoadedYet, RuntimeDatabaseError, WinPackageAlreadyExists{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(this.checkWinExists(winpackage)) throw new WinPackageAlreadyExists(winpackage);
            PreparedStatement cursorAdd = this.databaseConnected.prepareStatement("INSERT INTO winpackages (nm_pckg, vl_link) VALUES (?, ?);");
            cursorAdd.setString(1, winpackage);
            cursorAdd.setString(2, linkDownload);
            cursorAdd.executeUpdate();
        }
        catch(SQLException re){ throw new RuntimeDatabaseError(re.getMessage());}
    }

    public void delWinP(String winpackage) throws DatabaseNotLoadedYet, RuntimeDatabaseError, WinPackageNotFound{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(!this.checkWinExists(winpackage)) throw new WinPackageNotFound(winpackage);
            PreparedStatement cursorDel = this.databaseConnected.prepareStatement("DELETE FROM winpackages WHERE nm_pckg = ?;");
            cursorDel.setString(1, winpackage);
            cursorDel.executeUpdate();
        }
        catch(SQLException re){ throw new RuntimeDatabaseError(re.getMessage());}
    }

    public void chWinPackName(String winpackage, String newName) throws DatabaseNotLoadedYet, RuntimeDatabaseError, WinPackageNotFound, WinPackageAlreadyExists{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(!this.checkWinExists(winpackage)) throw new WinPackageNotFound(winpackage);
            if(this.checkWinExists(newName)) throw new WinPackageAlreadyExists(newName);
            PreparedStatement cursorCh = this.databaseConnected.prepareStatement("UPDATE winpackages SET nm_pckg = ? WHERE nm_pckg = ?;");
            cursorCh.setString(1, newName);
            cursorCh.setString(2, winpackage);
            cursorCh.executeUpdate();
        }
        catch(SQLException re){ throw new RuntimeDatabaseError(re.getMessage());}
    }

    public void chWinPackLink(String winpackage, String new_link) throws DatabaseNotLoadedYet, RuntimeDatabaseError, WinPackageNotFound{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(!this.checkWinExists(winpackage)) throw new WinPackageNotFound(winpackage);
            PreparedStatement cursorCh = this.databaseConnected.prepareStatement("UPDATE winpackages SET vl_link = ? WHERE nm_pckg = ?;");
            cursorCh.setString(1, new_link);
            cursorCh.setString(2, winpackage);
            cursorCh.executeUpdate();
        }
        catch(SQLException re){ throw new RuntimeDatabaseError(re.getMessage());}
    }

    public ArrayList<String[]> qrWinPackByName(String nameNeedle) throws DatabaseNotLoadedYet, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            Statement cursorQr = this.databaseConnected.createStatement();
            ArrayList<String[]> results = new ArrayList<String[]>();
            ResultSet resultsDb = cursorQr.executeQuery("SELECT * FROM winpackages WHERE nm_pckg LIKE \"%" + nameNeedle + "%\";");
            while(resultsDb.next()){
                String[] resultsRow = new String[3];
                resultsRow[0] = "" + resultsDb.getInt("cd_extp");
                resultsRow[1] = resultsDb.getString("nm_pckg");
                resultsRow[2] = resultsDb.getString("vl_link");
                results.add(resultsRow);
            }
            return results;
        }
        catch(SQLException re){ throw new RuntimeDatabaseError(re.getMessage());}
    }

    public ArrayList<String[]> qrWinPackByLink(String linkNeedle) throws DatabaseNotLoadedYet, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            Statement cursorQr = this.databaseConnected.createStatement();
            ArrayList<String[]> results = new ArrayList<String[]>();
            ResultSet set = cursorQr.executeQuery("SELECT * FROM winpackages WHERE vl_link LIKE \"%"+ linkNeedle + "%\";");
            while(set.next()){
                String[] row = new String[3];
                row[0] = "" + set.getInt("cd_extp");
                row[1] = set.getString("nm_pckg");
                row[2] = set.getString("vl_link");
                results.add(row);
            }
            return results;
        }
        catch(SQLException re){ throw new RuntimeDatabaseError(re.getMessage());}
    }

    @Nullable
    public String[] getWinPData(@NotNull String winpackage) throws DatabaseNotLoadedYet, RuntimeDatabaseError{
        try{
            if(!this.gotDatabase) throw new DatabaseNotLoadedYet();
            if(!this.checkWinExists(winpackage)) return null;
            Statement cursorGet = this.databaseConnected.createStatement();
            cursorGet.setMaxRows(1);
            String[] data = new String[3];
            ResultSet results = cursorGet.executeQuery("SELECT * FROM winpackages WHERE nm_pckg = \"" + winpackage + "\";");
            data[0] = "" + results.getInt("cd_extp");
            data[1] = results.getString("nm_pckg");
            data[2] = results.getString("vl_link");
            return data;
        }
        catch(SQLException re){ throw new RuntimeDatabaseError(re.getMessage());}
    }
}