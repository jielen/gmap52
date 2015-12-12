package com.anyi.gp.taglib.components;

import java.io.IOException;
import java.io.Writer;

public interface BoxScriptHandler {
  void handle(EditBox box,Writer out) throws IOException;
}
