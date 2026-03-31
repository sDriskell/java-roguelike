package test.roguelike;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class HealthCheck {

  @Test
  void testDefaultIsTrue() {
    assertTrue(true);
  }

  @Test
  void testHealthCheck() {
    assertEquals(2 + 2, 4);
    assertNotEquals(1 + 1, 3);
    assertFalse(false);
    assertNotSame("this", "different");
    assertEquals("a", "a");
  }
}
