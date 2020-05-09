package interface_cli;

import database.DatabaseManager;
import database.PackagesManager;
import database.ExtPackagesManager;
import database.WinPackages;
import java.util.Scanner;
import org.jetbrains.annotations.*;
import java.io.IOException;

public class FslInterface{

    public static String DEFAULT_PACKAGES_DATABASE = "packages.db";
    public static String FSL_VERSION = "ALPHA";


    public static void clear(){  for(int i = 0; i < 128; i++) System.out.println(); }

    public static void DatabaseDataScreen(@NotNull  DatabaseManager databaseUsing){
        try{
            Scanner nullableScanner = new Scanner(System.in);
            String dbPath = databaseUsing.getDatabaseLoaded();
            String sqliteV = databaseUsing.getDriverVersion();
            String sqliteD = databaseUsing.getDriverUsing();
            System.out.println("Database using: " + dbPath);
            System.out.println("SQLite Version: " + sqliteV);
            System.out.println("SQLite Driver: " + sqliteD);
            System.out.println("FSL VERSION: " + FslInterface.FSL_VERSION);
            System.out.print("<<press any button to return>");
            Object data = nullableScanner.nextLine();
            FslInterface.clear();
        }
        catch(Exception e){
            // TODO: add the colors to the CLI
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }


    public FslInterface(){
        // TODO
    }

    public static void main(String[] args){
        try{
            DatabaseManager dba = new DatabaseManager("packages.db");
            FslInterface.DatabaseDataScreen(dba);
        }
        catch (Exception e){ System.out.println(e.getMessage());}
    }
}