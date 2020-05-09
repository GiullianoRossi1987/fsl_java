package interface_cli;

import database.DatabaseManager;
import database.PackagesManager;
import database.ExtPackagesManager;
import database.WinPackages;
import java.util.Scanner;
import org.jetbrains.annotations.*;

public class FslInterface{

    public static String DEFAULT_PACKAGES_DATABASE = "packages.db";

    private static void DatabaseDataScreen(@NotNull  DatabaseManager databaseUsing){
        try{

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
}