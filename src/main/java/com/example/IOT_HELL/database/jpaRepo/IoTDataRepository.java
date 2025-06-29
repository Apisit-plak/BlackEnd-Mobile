package com.example.IOT_HELL.database.jpaRepo;


import com.example.IOT_HELL.database.entity.IoTData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IoTDataRepository extends JpaRepository<IoTData, Long> {

@Query("SELECT COALESCE(MAX(i.id), 0) FROM IoTData i")
    long findLastId();

    boolean existsByLongitudeAndLatitude(double longitude, double latitude);
}
