package org.example.utils;

public class Utils {
  public static Long parseLongOrNull(String value) {
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException ex) {
      return null;
    }
  }
}
