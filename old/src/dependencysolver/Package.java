/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dependencysolver;

import java.util.ArrayList;

/**
 *
 * @author Mup
 */
public class Package {
  String name;
  String version;
  int size;
  ArrayList<String> depends;
  ArrayList<String> conflicts;
  
  public Package(String name, String version, int size, ArrayList<String> depends, ArrayList<String> conflicts){
    this.name = name;
    this.version = version;
    this.size = size;
    this.depends = depends;
    this.conflicts = conflicts;
  }
  
  public Package(String name, String version){
    this.name = name;
    this.version = version;
    size = 0;
    depends = null;
    conflicts = null;
  }
  
  public String getName(){
    return name;
  }
  
  public String getVersion(){
    return version;
  }
  
  public int getSize(){
    return size;
  }
  
  public ArrayList<String> getDependancies(){
    return depends;
  }
  
  public ArrayList<String> getConflicts(){
    return conflicts;
  }
}
