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

    public static void input(){
        try{
            System.out.print("<<press any key to continue>>");
            System.in.read();
        }
        catch (Exception e){
            System.err.print(e.getMessage());
        }
    }

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


    public static void PackagesOptions(@NotNull PackagesManager packagesManager){
        try{
            boolean mainProceed = true;
            while(mainProceed){
                FslInterface.clear();
                Scanner optionReceiver = new Scanner(System.in);
                boolean proceed = true; // used for all the data scopes.
                System.out.println("Linux Packages tools:\n===============");
                System.out.println("= [1] Install a package");
                System.out.println("= [2] Add a new package");
                System.out.println("= [3] Delete a package");
                System.out.println("= [4] Change package name");
                System.out.println("= [5] Change package shell command");
                System.out.println("= [6] Get package data");
                System.out.println("= [7] Exit");
                System.out.print(">>> ");
                int opt = optionReceiver.nextInt();
                switch(opt){
                    case 1:
                        do{
                            Scanner packTo = new Scanner(System.in);
                            System.out.print("Type the package name to install: ");
                            String pack = packTo.nextLine();
                            System.out.println("Confirm package?\n[1] Yes\n[2] No\n[3] Cancel Operation\n");
                            int optP = packTo.nextInt();
                            if(optP == 1){
                                packagesManager.installPackage(pack);
                                System.out.println("Package installed successfully!");
                                FslInterface.input();
                                proceed = false;
                            }
                            else if(optP == 3) proceed = false;
                        }while(proceed);
                        break;
                    case 2:
                        do{
                            Scanner packDataReceiver = new Scanner(System.in);
                            System.out.print("Type the package name: ");
                            String packName = packDataReceiver.nextLine();
                            System.out.print("Type the package installation command shell: ");
                            String packShell = packDataReceiver.nextLine();
                            System.out.println("Confirm those data?\n[1] Yes\n[2] No\n[3] Cancel Operation\n");
                            System.out.print(">>> ");
                            int optProceed = packDataReceiver.nextInt();
                            if(optProceed == 1){
                                packagesManager.addPackage(packName, packShell);
                                System.out.print("Package added successfully!\n<<press any button to return>>");
                                FslInterface.input();
                                proceed = false;
                            }
                            else if(optProceed == 3) proceed = false;

                        }while(proceed);
                        break;
                    case 3:
                        do{
                            Scanner packTo = new Scanner(System.in);
                            System.out.print("Type the package name to delete: ");
                            String toDel = packTo.nextLine();
                            System.out.println("Confirm the package name?\n[1] Yes\n[2] No\n[3] Cancel Operation");
                            System.out.print(">>> ");
                            int optC = packTo.nextInt();
                            if(optC == 1){
                                packagesManager.delPackage(toDel);
                                System.out.println("Package deleted successfully!");
                                FslInterface.input();
                                proceed = false;
                            }
                            else if(optC == 3) proceed = false;
                        }while(proceed);
                        break;
                    case 4:
                        do{
                            Scanner packData = new Scanner(System.in);
                            System.out.print("Type the package to change the name: ");
                            String toChange = packData.nextLine();
                            System.out.println("Type the new name to the package: ");
                            String newName = packData.nextLine();
                            System.out.println("Confirm the package data?\n[1] Yes\n[2] No\n[3] Cancel Operation");
                            System.out.print(">>> ");
                            int optC = packData.nextInt();
                            if(optC == 1){
                                packagesManager.chPackName(toChange, newName);
                                System.out.println("Package name changed successfully");
                                FslInterface.input();
                                proceed = false;
                            }
                            else if(optC == 3) proceed = false;
                        }while(proceed);
                        break;
                    case 5:
                        do{
                            Scanner packData = new Scanner(System.in);
                            System.out.print("Type the package to change the shell command: ");
                            String toChange = packData.nextLine();
                            System.out.println("Type the new shell command: ");
                            String newShell = packData.nextLine();
                            System.out.println("Confirm the package data?\n[1] Yes\n[2] No\n[3] Cancel Operation");
                            System.out.print(">>> ");
                            int optC = packData.nextInt();
                            if(optC == 1){
                                packagesManager.chPackShell(toChange, newShell);
                                System.out.println("Package shell command changed successfully");
                                FslInterface.input();
                                proceed = false;
                            }
                            else if(optC == 3) proceed = false;
                        }while(proceed);
                        break;
                    case 6:
                        do{
                            Scanner packageT = new Scanner(System.in);
                            System.out.print("Type the package name: ");
                            String pack = packageT.nextLine();
                            System.out.println("Confirm the package name?\n[1] Yes\n[2] No\n[3] Cancel Operation");
                            System.out.print(">>> ");
                            int optC = packageT.nextInt();
                            if(optC == 1){
                                String[] data = packagesManager.getPackageData(pack);
                                System.out.println("Package Id: " + data[0]);
                                System.out.println("Package name: " + data[1]);
                                System.out.println("Package installation shell: " + data[2]);
                                System.out.print("<<press any button to return>>");
                                FslInterface.input();
                                proceed = false;
                            }
                            else if(optC == 3) proceed = false;
                        }while(proceed);
                        break;
                    case 7:
                        mainProceed = false;
                        break;
                }
                proceed = true;
            }
        }
        catch(Exception e){
            System.err.print(e.getMessage());
        }
    }


    public FslInterface(){
        // TODO
    }

    public static void main(String[] args){
        try{
            DatabaseManager dba = new DatabaseManager("packages.db");
            PackagesManager pkm = new PackagesManager("packages.db");
            FslInterface.PackagesOptions(pkm);
        }
        catch (Exception e){ System.out.println(e.getMessage());}
    }
}