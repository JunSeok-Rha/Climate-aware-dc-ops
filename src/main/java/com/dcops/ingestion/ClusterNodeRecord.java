package com.dcops.ingestion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "raw_cluster_node_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClusterNodeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "node_id", nullable = false, length = 50)
    private String nodeId;

    @Column(name = "observed_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant observedAt;

    @Column(name = "cpu_usage", nullable = false, precision = 5, scale = 2)
    private BigDecimal cpuUsage;

    @Column(name = "memory_usage", nullable = false, precision = 5, scale = 2)
    private BigDecimal memoryUsage;

    @Column(name = "workload_intensity", nullable = false, precision = 5, scale = 2)
    private BigDecimal workloadIntensity;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ DEFAULT now()")
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
