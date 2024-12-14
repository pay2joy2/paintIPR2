package com.paintipr1.paintipr1;

import myjar.util.Drawer;
// com.paintipr1.paintipr1.drawers.Drawer;
import java.util.LinkedHashMap;

public class DrawersMap {

  private static LinkedHashMap<String, Drawer> figuresList = null;

  public static LinkedHashMap<String, Drawer> getInstance(){
    if(figuresList == null){
      figuresList = new LinkedHashMap<>();
    }
    return figuresList;
  }

}
