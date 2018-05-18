package com.datarepublic.simplecab.repository;

import java.util.Date;

public interface SimpleCabRepository {

  Integer getCountByMedallionAndPickupDatetime(String medallionId, Date pickupDate);

}
