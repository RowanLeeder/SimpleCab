package com.github.rowanleeder.simplecab.cabtrip;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data // auto-gen the getter/setter boilerplate.
@Entity
@Table(name = "cab_trip_data")
class CabTrip {

    /*
     * The SQL for the cab_trip_data table does not specify a primary key.
     *
     * A composite key of (medallion, pickup_datetime and dropoff_datetime) appears unique in the data set, however
     * there is no guarantee that it would be unique in practice. Additionally, pickup_datetime and dropoff_datetime
     * are nullable.
     *
     * Spring ORM requires a unique id on all DAOs. This could be achieved via an embedded id class of (medallion,
     * pickup_datetime and dropoff_datetime), however this does not support null key, nor does it remove the implicit
     * fragility.
     *
     * Better solution is to add a unique primary key to each row in the data set.
     *
     * Unfortunately Spring doesn't create retroactive ids for existing data when adding a new auto_increment column so
     * this is being handled via a migration (see resources/db/migration/V1.1__CabTripDataId.sql).
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private int id;
    @NotNull String medallion;
    @NotNull String hackLicense;
    @NotNull String vendorId;
    Long rateCode;
    String storeAndFwdFlag;
    @Column(name = "pickupDatetime") LocalDateTime pickup;
    @Column(name = "dropoffDatetime") LocalDateTime dropoff;
    Long passengerCount;
    Long tripTimeInSecs;
    Double tripDistance;
    Double pickupLongitude;
    Double pickupLatitude;
    Double dropoffLongitude;
    Double dropoffLatitude;
}
