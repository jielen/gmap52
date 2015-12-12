package com.anyi.gp.domain;

import java.util.Set;

public class SecurityRandom {

  private static String generateTicket() {
    double aNum = Math.random() * 1000000000;
    return "" + (int) Math.floor(aNum);
  }

  public static String getToken(Set tokens) {
    String token = null;
    while (true) {
      token = generateTicket();
      if (!tokens.contains(token)) {
        break;
      }
    }
    return token;
  }
}
