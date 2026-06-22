package com.dcops.ingestion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawWeatherDataRepository extends JpaRepository<RawWeatherData, Long> {
}
