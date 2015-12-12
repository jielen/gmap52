package com.anyi.gp.print.direct;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.anyi.gp.print.exception.PrintingException;

public class PrintLoader {
  public PrintLoader() {
  }

  public static Object loadObject(String fileName) throws PrintingException {
    return loadObject(new File(fileName));
  }

  public static Object loadObject(File file) throws PrintingException {
    Object obj = null;
    FileInputStream fis = null;
    ObjectInputStream ois = null;
    if (!file.exists() || !file.isFile())
      throw new PrintingException(new FileNotFoundException(String.valueOf(file)));
    try {
      fis = new FileInputStream(file);
      ois = new ObjectInputStream(fis);
      obj = ois.readObject();
    } catch (IOException e) {
      throw new PrintingException("Error loading object from file : "
        + file.getName() + e.getMessage());
    } catch (ClassNotFoundException e) {
      throw new PrintingException("Class not found when loading object from file : "
        + file.getName() + e.getMessage());
    } finally {
      try {
        if (ois != null) {
          ois.close();
        }
        if (fis != null) {
          fis.close();
        }
      } catch (IOException e) {
        throw new PrintingException("Error close the inputstream : "
          + file.getName() + e.getMessage());
      }
    }
    return obj;
  }

}
