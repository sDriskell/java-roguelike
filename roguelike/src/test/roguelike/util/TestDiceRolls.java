package test.roguelike.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import roguelike.util.DiceRolls;

class TestDiceRolls {
  private static final String ZERO_POOL_MSG = "Rolling zero dice yields zero success.";

  @Test
  void zeroPoolReturnsZero() {
    assertEquals(0, DiceRolls.roll(0), ZERO_POOL_MSG);
  }

}