// $Id: AssertionException.java,v 1.1 2008/02/22 08:33:13 liubo Exp $

package com.anyi.gp.debug;

import org.apache.log4j.Logger;

public class AssertionException extends RuntimeException {
  private static Logger log = Logger.getLogger(AssertionException.class);

  /** Constructs a new runtime exception with null as its detail message. */
  public AssertionException() {
  }

  /** Constructs a new runtime exception with the specified detail message. */
  public AssertionException(String message) {
    super(message);
    log.info(message);
  }

}
