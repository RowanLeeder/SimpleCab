package com.github.rowanleeder.simplecab.cabtrip;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class CabTripControllerTest extends CabTripDataTest {

    @Autowired
    private CabTripController controller;

    @Override
    public void setUp() {
        super.setUp();
        controller.clearTripsCache();
    }

    /**
     * Test single trip lookup.
     */
    @Test
    public void tripsCached() {
        LocalDateTime time = LocalDateTime.now();
        trip("a", time);
        trip("b", time);
        LocalDate date = time.toLocalDate();

        assertEquals(0, (long) controller.trips("a", date.plusDays(1), Optional.of(true)));
        assertEquals(1, (long) controller.trips("a", date, Optional.of(true)));
        assertEquals(0, (long) controller.trips("c", date, Optional.of(true)));

        // result in cache
        trip("a", time);
        assertEquals(1, (long) controller.trips("a", date, Optional.of(true)));
        assertEquals(2, (long) controller.trips("a", date, Optional.of(false)));
        assertEquals(1, (long) controller.trips("a", date, Optional.of(true)));

        // clearing cache should recalculate it
        controller.clearTripsCache();
        assertEquals(2, (long) controller.trips("a", date, Optional.of(true)));
    }

    /**
     * Test batch lookup.
     */
    @Test
    public void tripsBatch() {
        LocalDateTime time = LocalDateTime.now();
        LocalDate date = time.toLocalDate();
        trip("a", time);
        trip("b", time);

        Map<String, Integer> expected = new HashMap<>();
        expected.put("a", 1);
        expected.put("b", 1);
        expected.put("c", 0);

        assertEquals(expected, controller.trips(new String[]{"a", "b", "c"}, date, Optional.of(true)));

        trip("a", time);
        trip("d", time);

        // 'd' will be picked up, but 'a' is cached.
        expected.put("d", 1);
        assertEquals(expected, controller.trips(new String[]{"a", "b", "c", "d"}, date, Optional.of(true)));

        expected.put("a", 2);
        assertEquals(expected, controller.trips(new String[]{"a", "b", "c", "d"}, date, Optional.of(false)));
        controller.clearTripsCache();
        assertEquals(expected, controller.trips(new String[]{"a", "b", "c", "d"}, date, Optional.of(true)));
    }

    /**
     * Test the cache can be cleared.
     */
    @Test
    public void clearTripsCache() {
        LocalDateTime time = LocalDateTime.now();
        LocalDate date = time.toLocalDate();
        trip("a", time);
        assertEquals(1, (long) controller.trips("a", date, Optional.of(true)));

        trip("a", time);
        assertEquals(2, (long) controller.trips("a", date, Optional.of(false)));
        assertEquals(1, (long) controller.trips("a", date, Optional.of(true)));
        assertEquals(1, (long) controller.trips("a", date, Optional.empty()));
        assertEquals(1, (long) controller.trips("a", date, null));

        controller.clearTripsCache();
        assertEquals(2, (long) controller.trips("a", date, Optional.of(true)));
    }
}
