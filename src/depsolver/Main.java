package depsolver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;


class Package {
  private String name;
  private String version;
  private Integer size;
  private List<List<String>> depends = new ArrayList<>();
  private List<String> conflicts = new ArrayList<>();
  
  public String getName() { return name; }
  public String getVersion() { return version; }
  public Integer getSize() { return size; }
  public List<List<String>> getDepends() { return depends; }
  public List<String> getConflicts() { return conflicts; }
  public void setName(String name) { this.name = name; }
  public void setVersion(String version) { this.version = version; }
  public void setSize(Integer size) { this.size = size; }
  public void setDepends(List<List<String>> depends) { this.depends = depends; }
  public void setConflicts(List<String> conflicts) { this.conflicts = conflicts; }
}



public class Main {
  private static List<Package> repo;
  private static List<String> initial;
  private static Map<Package, Map<Package, Boolean>> packageConstraints; 
  private static ArrayList<String> jsonCommands;
  private static ArrayList<String> packageNames;
  public static void main(String[] args) throws IOException {
    TypeReference<List<Package>> repoType = new TypeReference<List<Package>>() {};
    repo = JSON.parseObject(readFile(args[0]), repoType);
    TypeReference<List<String>> strListType = new TypeReference<List<String>>() {};
    initial = JSON.parseObject(readFile(args[1]), strListType);
    List<String> constraints = JSON.parseObject(readFile(args[2]), strListType);

// *****************************************
System.out.println("repo contains" );
    for(Package p : repo){
      System.out.println(p.getName());
    }
    packageConstraints = new HashMap<Package, Map<Package, Boolean>>();
    packageNames = new ArrayList<String>();
       System.out.println("Started"); 
    for(Package p : repo){
        System.out.println("the package is " + p.getName() + " v: " + p.getVersion());
      ArrayList<String> con = (ArrayList)p.getConflicts();
        System.out.println("the conflicts for " + p.getName() + " v: " + p.getVersion() + " are " + con);
      ArrayList<ArrayList<String>> deps = (ArrayList)p.getDepends();
      Map<Package, Boolean> cons = new HashMap<Package, Boolean>();
      for(String s : con){
          //identify the package from the repo
          
          boolean packageFound = false;
          int counter = 0;
          int index = 0;
          while(!packageFound && counter < repo.size()){
            System.out.println("the name is " + repo.get(counter).getName());
            if(s.contains(repo.get(counter).getName())){
              System.out.println("looking for conflict" + s);
              String operator = s.substring(repo.get(counter).getName().length(), s.length());
              String version = "";
              System.out.println("operator is " + operator);
              if(!operator.equals("")){
                if(operator.charAt(1) == '=' || operator.charAt(1) == '>' || operator.charAt(1) == '<'){
                  version = operator.substring(2, operator.length());
                  operator = operator.substring(0, 2);
                } else {
                  version = operator.substring(1, operator.length());
                  operator = operator.substring(0, 1);
                }
                  System.out.println("version = " + version);
              } else {
                System.out.println("avoid all versions of " + s);
              }
              
              System.out.println("the operator is now " + operator);
              boolean operatorIdentified = false;
              float conflictingVersionNumber;
              if(!version.equals("")){
                conflictingVersionNumber = Float.parseFloat(version);
              } else {
                conflictingVersionNumber = 0.0f;
              }
                System.out.println("the conflicting version = " + conflictingVersionNumber);
                
              float reposVersionNumber = Float.parseFloat(repo.get(counter).getVersion());
              System.out.println("the repos version = " + reposVersionNumber);
              if(operator.equals("<") && reposVersionNumber < conflictingVersionNumber && conflictingVersionNumber != 0.0f){
                System.out.println("conflict with version: " + version);
              } else if (operator.equals("=") && conflictingVersionNumber == reposVersionNumber && conflictingVersionNumber != 0.0f){
                operatorIdentified = true;
                System.out.println("conflict with version : " + version);
              } else if (operator.equals('>') && reposVersionNumber > conflictingVersionNumber && conflictingVersionNumber != 0.0f){
                operatorIdentified = true;
                System.out.println("conflict with version: " + version);
              } else if (operator.equals(">=") && reposVersionNumber >= conflictingVersionNumber && conflictingVersionNumber != 0.0f){
                operatorIdentified = true;
                System.out.println("conflict with version: " + version);
              } else if (operator.equals("<=") && reposVersionNumber <= conflictingVersionNumber && conflictingVersionNumber != 0.0f){
                operatorIdentified = true;
                System.out.println("conflict with version: " + version);
              } else if (operator.equals("")){
                operatorIdentified = true;
                System.out.println("no version can be used");
              } else {
                System.out.println("no issue with this version");
              }
              if(operatorIdentified){
                packageFound = true;
                index = counter;
                cons.put(repo.get(index), false);
                System.out.println("there is a conflict in the repo, avoid using " + repo.get(counter).getName() + " version " + repo.get(counter).getVersion());
              }
              
            } 
            counter ++;
          }
          
      }
      System.out.println("the dependencies for " + p.getName() + " are " + deps);
      for(ArrayList<String> listOfDeps : deps){
        for(String s : listOfDeps){
          //identify the package from the repo
          System.out.println("looking for dep " + s);
          boolean packageFound = false;
          int counter = 0;
          int index = 0;
          while(!packageFound && counter < repo.size()){
            if(s.contains(repo.get(counter).getName())){
              packageFound = true;
              index = counter;
              cons.put(repo.get(index), true);
                System.out.println("found");
            } 
            counter ++;
          }
        }
      }
      //list of packages with a list of deps and conflicts
      packageConstraints.put(p,cons);
      packageNames.add(p.getName());
    }
    
      System.out.println("The Hashmap contains");
      System.out.println(packageConstraints);
        
    jsonCommands = new ArrayList<String>();
    for(String c : constraints){
      if(c.charAt(0) == '+'){
        install(c);
      } else if(c.charAt(0) == '-'){
        uninstall(c);
      } else {
        System.out.println("unknown sign, avoiding " + c + "as unknown constraint"); 
      }
    }
    String output = JSON.toJSONString(jsonCommands);
    System.out.println(output);
    PrintWriter writer = new PrintWriter("commands.json", "UTF-8");
    writer.println(output);
    writer.close();
  }

  private static void uninstall(String packageToRemove) {
    if(isInstalled(packageToRemove)){
      System.out.println("package "+ packageToRemove +" is being uninstalled");
      jsonCommands.add(packageToRemove);
    } else {
      System.out.println("package "+ packageToRemove +" is not installed");
    }
  }

  private static void install(String packageToInstall) {
    if(!isInstalled(packageToInstall)){
      // check package is in repo
      String[] packageParts= trimAndSplit(packageToInstall);
      String pName = packageParts[0].substring(1, packageParts[0].length());
      String version = "0.0";
      if(packageParts.length > 1){
        version = packageParts[1];
        System.out.println("was greater than 1");
      }
      
      if(packageNames.contains(pName)){
        for(Package p : repo){
          if(p.getName().equals(pName)){
            System.out.println("Package " + pName +" version "+ version +" is in position " + packageNames.indexOf(pName));
            //find all dependecies
            List<List<String>> deps = p.getDepends();
            for(List l : deps){
              for(Object d : l){
                System.out.println("found dependency " + d);
              }
            
            }
            // find possible instalation options
            //foreach possible option
            //find all sub dependencies
          }
        }
        
      
      System.out.println("package " + packageToInstall +" needs to be installed");
      jsonCommands.add(packageToInstall);
      } else {System.out.println("Could not find requested package in current repository");}
      
    } else {
      System.out.println("package "+ packageToInstall +" is already installed");
    }
  }
  
  private static String[] trimAndSplit(String packageToInstall){
    String[] parts = packageToInstall.split("=");
    return parts;
  }

  private static boolean isInstalled(String pName){
    String name = pName.substring(1, pName.length());
    String[] details = name.split("=");
    for(String s : initial){
      if(s.contains(details[0])){
        String[] v = s.split("=");
        String installedVersion = v[1];
        String requestedVersion = details[1];
        if(installedVersion.equals(requestedVersion)){
          return true;
        }
      }
    }
    return false;
  }

// *****************************************
  static String readFile(String filename) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(filename));
    StringBuilder sb = new StringBuilder();
    br.lines().forEach(line -> sb.append(line));
    return sb.toString();
  }
}
