package com.dcops.ingestion;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClusterNodeRecordRepository extends JpaRepository<ClusterNodeRecord, Long> {
}
