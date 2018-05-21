package com.github.rowanleeder.simplecab.cabtrip;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/cabs")
public class CabTripController {

    /**
     * Self-autowiring reference. Allows access to the class proxy-wrappers (expanded @Cacheable etc) from inside the class.
     */
    @Resource
    private CabTripController self;

    @Autowired
    private CabTripRepository repo;

    /**
     * Get the count of trips made by specified cabs on a given date.
     *
     * @param medallions The medallion ids of the cabs.
     * @param date Date filter. Only count trips that were commenced on this date.
     * @param cache If false the cache will be bypassed. Default True.
     * @return The number of trips made by each cab. {str:medallion => int:count}
     */
    @GetMapping(path = "/trips/counts")
    public Map<String, Integer> trips(
            @RequestParam String[] medallions,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Optional<Boolean> cache
    ) {
        // caching individual medallions, not medallions sets.
        return Arrays.stream(medallions).distinct().collect(Collectors.toMap(c -> c, c -> self.trips(c, date, cache)));
    }

    /**
     * Get the count of trips made by a cab on a given date.
     *
     * @param medallion The cabs medallion id.
     * @param date Date filter. Only count trips that were commenced on this date.
     * @param cache If false the cache will be bypassed. Default True.
     * @return The number of trips made by the cab.
     */
    @Cacheable(
            value = "cabs.trips.counts",
            key = "#medallion + '.' + #date",
            condition = "#cache == null || !#cache.isPresent() || #cache.get()",
            sync = true
    )
    public Integer trips(String medallion, LocalDate date, Optional<Boolean> cache) {
        // - nonexistent medallions will just return 0.
        // - SQL injection isn't an issue as interfacing via a JpaRepository.
        return repo.countByMedallionAndPickupBetween(
                medallion,
                date.atTime(LocalTime.MIN),
                date.atTime(LocalTime.MAX)
        );
    }

    /**
     * Clear the trip count cache.
     */
    @DeleteMapping(path = "/trips/counts")
    @CacheEvict(value = "cabs.trips.counts", allEntries = true)
    public void clearTripsCache() {
        // - sample code implies the option to delete the entire cache, not just individual entries.
        // - 'Cache-Control: no-cache' support might be more apt then a DELETE endpoint.
    }
}
