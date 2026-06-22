package com.dcops.ingestion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClusterIngestionService {

    private final ClusterNodeRecordRepository repository;

    private static final int BATCH_SIZE = 500;
    private static final String CSV_FILE_PATH = "data/sample/cluster_sample.csv";

    public void ingest() throws IOException {
        Instant startTime = Instant.now();
        int processedCount = 0;
        List<ClusterNodeRecord> batch = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Path.of(CSV_FILE_PATH))) {
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {
                try {
                    String[] tokens = line.split(",", -1);

                    if (tokens.length < 4) {
                        log.warn("Skipping row with insufficient columns: {}", line);
                        continue;
                    }

                    String nodeId = tokens[0].trim();
                    long timestamp = Long.parseLong(tokens[1].trim());
                    Instant observedAt = Instant.ofEpochSecond(timestamp);
                    BigDecimal cpuUsage = new BigDecimal(tokens[2].trim());
                    BigDecimal memoryUsage = new BigDecimal(tokens[3].trim());

                    if (cpuUsage.compareTo(BigDecimal.ZERO) < 0 || cpuUsage.compareTo(new BigDecimal("100")) > 0 ||
                        memoryUsage.compareTo(BigDecimal.ZERO) < 0 || memoryUsage.compareTo(new BigDecimal("100")) > 0) {
                        log.warn("Skipping invalid row: nodeId={}, cpu={}, mem={}", nodeId, cpuUsage, memoryUsage);
                        continue;
                    }

                    BigDecimal workloadIntensity = cpuUsage.add(memoryUsage)
                            .divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);

                    ClusterNodeRecord record = ClusterNodeRecord.builder()
                            .nodeId(nodeId)
                            .observedAt(observedAt)
                            .cpuUsage(cpuUsage)
                            .memoryUsage(memoryUsage)
                            .workloadIntensity(workloadIntensity)
                            .build();

                    batch.add(record);
                    processedCount++;

                    if (batch.size() >= BATCH_SIZE) {
                        repository.saveAll(batch);
                        batch.clear();
                    }

                } catch (NumberFormatException e) {
                    log.warn("Skipping unparseable row: {}", line);
                }
            }

            if (!batch.isEmpty()) {
                repository.saveAll(batch);
                batch.clear();
            }
        }

        log.info("Ingestion complete: processed={} rows, elapsed={}ms",
                processedCount, Duration.between(startTime, Instant.now()).toMillis());
    }
}
