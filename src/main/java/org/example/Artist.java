//Artist.java (class A)
package org.example;

public class Artist implements Person {
  private String name;

  public Artist(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }
}
