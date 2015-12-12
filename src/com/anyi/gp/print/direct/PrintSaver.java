package com.anyi.gp.print.direct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import com.anyi.gp.print.exception.PrintingException;

public class PrintSaver {

  public PrintSaver() {
  }

  public static void saveObject(Object obj, String fileName) throws PrintingException{
    saveObject(obj, new File(fileName));
  }

  public static void saveObject(Object obj, File file) throws PrintingException{
    FileOutputStream fos = null;
    ObjectOutputStream oos = null;
    try {
      fos = new FileOutputStream(file);
      oos = new ObjectOutputStream(fos);
      oos.writeObject(obj);
      oos.flush();
      fos.flush();
    } catch (IOException e) {
      throw new PrintingException("Error saving file : " + file.getName() + e.getMessage());
    } finally {
      try {
        if (oos != null) {
          oos.close();
        }
        if (fos != null) {
          fos.close();
        }
      } catch (IOException e) {
        throw new PrintingException("Error saving file : " + file.getName() + e.getMessage());
      }
    }
  }

}
