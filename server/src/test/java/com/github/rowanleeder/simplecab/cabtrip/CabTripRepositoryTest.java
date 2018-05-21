package com.github.rowanleeder.simplecab.cabtrip;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;

public class CabTripRepositoryTest extends CabTripDataTest {

    /**
     * Test that the medallion counter is working as expected.
     */
    @Test
    public void countByMedallionAndPickupBetween() {
        LocalDateTime time1 = LocalDateTime.of(2018, 12, 29, 0, 0);
        LocalDateTime time2 = LocalDateTime.of(2018, 12, 30, 0, 0);
        LocalDateTime time3 = LocalDateTime.of(2018, 12, 30, 13, 20);
        LocalDateTime time4 = LocalDateTime.of(2018, 12, 31, 0, 0);

        trip("a", time1);
        trip("a", time2);
        trip("a", time3);
        trip("a", time4);

        trip("b", time1);
        trip("b", time2);
        trip("b", time4);

        trip("c", time4);

        assertEquals(2, (long) repo.countByMedallionAndPickupBetween("a", time2, time3));
        assertEquals(3, (long) repo.countByMedallionAndPickupBetween("a", time1, time3));
        assertEquals(0, (long) repo.countByMedallionAndPickupBetween("c", time1, time3));
        assertEquals(1, (long) repo.countByMedallionAndPickupBetween("c", time4, time4));
        assertEquals(0, (long) repo.countByMedallionAndPickupBetween("d", time1, time4));
    }

    /**
     * The SQL backing of 'between' means that dates have to be in chronological order.
     * This doesn't cause an error and always returns 0.
     * More trouble then it is worth to add param checking to the interface-based repository.
     */
    @Test
    public void testCountByMedallionAndPickupBetweenInvalid() {
        LocalDateTime time1 = LocalDateTime.of(2018, 12, 29, 0, 0);
        LocalDateTime time2 = LocalDateTime.of(2018, 12, 30, 0, 0);

        trip("a", time1);
        trip("a", time2);

        assertEquals(0, (long) repo.countByMedallionAndPickupBetween("a", time2, time1));
    }
}
