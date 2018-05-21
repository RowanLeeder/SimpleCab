package com.github.rowanleeder.simplecab.cabtrip;

import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

public interface CabTripRepository extends CrudRepository<CabTrip, Long> {

    Integer countByMedallionAndPickupBetween(String medallion, LocalDateTime start, LocalDateTime end);
}
