//Producer.java (class D)
package org.example;

public class Producer implements Person {
  private String name;

  public Producer(String name) {
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
