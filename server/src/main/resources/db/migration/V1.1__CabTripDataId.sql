-- assumes ny_cab_data_cab_trip_data_full.sql already set up.

-- add a primary key to the cab_trip_data table.
alter table cab_trip_data add column if not exists id int primary key auto_increment first;

-- adding indexes for good measure.
create index medallion on cab_trip_data (medallion(32));
create index pickup_datetime on cab_trip_data (pickup_datetime);
create index dropoff_datetime on cab_trip_data (dropoff_datetime);
