package com.github.rowanleeder.simplecab.cabtrip;

import com.github.rowanleeder.simplecab.Server;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Server.class)
public abstract class CabTripDataTest {

    @Autowired
    protected CabTripRepository repo;

    @Before
    public void setUp() {
        repo.deleteAll();
    }

    /**
     * Create a trip.
     *
     * @param medallion The medallion of the trip.
     * @param pickup The pickup date.
     * @return The trip.
     */
    CabTrip trip(String medallion, LocalDateTime pickup) {
        CabTrip trip = new CabTrip();
        trip.setMedallion(medallion);
        trip.setVendorId("test");
        trip.setHackLicense("test");
        trip.setPickup(pickup);

        return repo.save(trip);
    }
}
