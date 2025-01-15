package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProducerTest {

  @Test
  void testProducerConstructor() {
    Producer producer = new Producer("Producer Name");

    assertEquals("Producer Name", producer.getName());
  }

  @Test
  void testSetName() {
    Producer producer = new Producer("Old Name");
    producer.setName("New Name");

    assertEquals("New Name", producer.getName());
  }
}
