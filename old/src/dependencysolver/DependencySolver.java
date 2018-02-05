/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dependencysolver;

/**
 *
 * @author Mup
 */

import java.util.ArrayList;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class DependencySolver {
  ArrayList<Package> currentlyInstalled;
  ArrayList<Package> packages;
  public DependencySolver(String[] args) throws JSONException, Exception{
    packages = new ArrayList();
    JSONArray repoJson = new JSONArray(args[0]);
    for (int i = 0; i < repoJson.length(); i++){
      JSONObject obj = repoJson.getJSONObject(i);
      ArrayList<String> dependancies = dependants(obj);
      ArrayList<String> conflicts = conflicts(obj);
      int packageSize = obj.getInt("size");
      Package p = new Package(obj.getString("name"), obj.getString("version"), packageSize, dependancies, conflicts);
      packages.add(p);
    }
    for(Package p : packages){
      System.out.println(p.getName() + " version " + p.getVersion());
      System.out.println("depends on " + p.getDependancies().toString());
      System.out.println("conflicts with " + p.getConflicts().toString());
    }
    
    JSONArray initialJson = new JSONArray(args[1]);
    System.out.println("**** **** initial state **** ****");
    System.out.println(initialJson);
    for (int i = 0; i < initialJson.length(); i++){
      JSONObject obj = repoJson.getJSONObject(i);
      System.out.println(obj.toString());
//      Package p = new Package(obj.getString("name"), obj.getString("version"));
//      currentlyInstalled.add(p);
    }
  }
  
  private ArrayList<String> conflicts(JSONObject obj) throws JSONException {
    ArrayList<String> packageConflicts = new ArrayList();
    if(obj.has("conflicts")){
      JSONArray conflictsArray = obj.getJSONArray("conflicts");
      if(conflictsArray.length() > 0){
        for(int i = 0; i < conflictsArray.length(); i++){
          packageConflicts.add(conflictsArray.get(i).toString());
        }
      }
    }
    return packageConflicts;
  }

  private ArrayList<String> dependants(JSONObject obj) throws JSONException {
    ArrayList<String> dependants = new ArrayList();
    if(obj.has("depends")){
      JSONArray depsArray = obj.getJSONArray("depends");
      if(depsArray.length() > 0){
        for(int i = 0; i < depsArray.length(); i++){
          JSONArray depArray = depsArray.getJSONArray(i);
          for(int j =0; j < depArray.length(); j++){
            dependants.add(depArray.get(j).toString());
          }
        }
      }
    }
    return dependants;
  }
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws JSONException, Exception {
    DependencySolver solver = new DependencySolver(args);
  }
}
