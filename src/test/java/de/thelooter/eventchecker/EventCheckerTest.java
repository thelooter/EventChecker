package de.thelooter.eventchecker;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import de.thelooter.eventchecker.EventChecker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventCheckerTest {

  ServerMock server;
  EventChecker eventChecker;

  @BeforeEach
  void setUp() {
    server = MockBukkit.mock();
    eventChecker = MockBukkit.load(EventChecker.class);
  }

  @AfterEach
  void tearDown() {
    MockBukkit.unmock();
  }

  @Test
  void test() {
    assertNotNull(eventChecker);
    assertTrue(eventChecker.isEnabled());
    assertInstanceOf(EventChecker.class, eventChecker);
  }
}