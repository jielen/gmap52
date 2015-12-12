package com.anyi.gp.taglib.components;

import java.io.IOException;
import java.io.Writer;

public interface Component {
  
  void writeInitScript(Writer out) throws IOException;

}
