package com.dcops.ingestion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherIngestionService {

    private static final int BATCH_SIZE = 500;
    private static final String SOURCE = "NOAA";
    private static final Path CSV_PATH = Paths.get("data/sample/weather_sample.csv");

    private final RawWeatherDataRepository repository;

    public void ingest() {
        long startTime = System.currentTimeMillis();
        int processedCount = 0;
        int skippedCount = 0;

        List<RawWeatherData> batch = new ArrayList<>(BATCH_SIZE);

        try (BufferedReader reader = Files.newBufferedReader(CSV_PATH)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                log.warn("CSV file is empty");
                return;
            }

            Map<String, Integer> columnIndices = parseHeader(headerLine);

            String line;
            while ((line = reader.readLine()) != null) {
                RawWeatherData record = parseLine(line, columnIndices);
                if (record == null) {
                    skippedCount++;
                    continue;
                }

                batch.add(record);
                processedCount++;

                if (batch.size() >= BATCH_SIZE) {
                    repository.saveAll(batch);
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                repository.saveAll(batch);
                batch.clear();
            }

            long elapsed = System.currentTimeMillis() - startTime;
            log.info("Weather ingestion complete. processed={}, skipped={}, elapsed={}ms",
                    processedCount, skippedCount, elapsed);

        } catch (IOException e) {
            log.error("Failed to read CSV file: {}", CSV_PATH, e);
            throw new RuntimeException("Weather ingestion failed", e);
        }
    }

    private Map<String, Integer> parseHeader(String headerLine) {
        Map<String, Integer> indices = new HashMap<>();
        String[] columns = headerLine.split(",");
        for (int i = 0; i < columns.length; i++) {
            indices.put(columns[i].trim(), i);
        }
        return indices;
    }

    private RawWeatherData parseLine(String line, Map<String, Integer> columnIndices) {
        String[] values = line.split(",", -1);

        try {
            String zoneId = getColumnValue(values, columnIndices, "STATION");
            if (zoneId == null || zoneId.isEmpty()) {
                log.warn("Missing STATION field, skipping row");
                return null;
            }

            OffsetDateTime observedAt = parseDateTime(
                    getColumnValue(values, columnIndices, "DATE"));
            if (observedAt == null) {
                log.warn("Invalid DATE field, skipping row");
                return null;
            }

            BigDecimal temperature = parseNumeric(
                    getColumnValue(values, columnIndices, "HourlyDryBulbTemperature"));
            if (temperature != null && !isValidTemperature(temperature)) {
                log.warn("Invalid temperature {}, skipping row", temperature);
                return null;
            }

            BigDecimal humidity = parseNumeric(
                    getColumnValue(values, columnIndices, "HourlyRelativeHumidity"));
            if (humidity != null && !isValidHumidity(humidity)) {
                log.warn("Invalid humidity {}, skipping row", humidity);
                return null;
            }

            return RawWeatherData.builder()
                    .zoneId(zoneId)
                    .source(SOURCE)
                    .observedAt(observedAt)
                    .temperature(temperature)
                    .humidity(humidity)
                    .windSpeed(null)
                    .precipitation(null)
                    .rawPayload(null)
                    .build();

        } catch (Exception e) {
            log.warn("Failed to parse row, skipping: {}", e.getMessage());
            return null;
        }
    }

    private String getColumnValue(String[] values, Map<String, Integer> columnIndices, String columnName) {
        Integer index = columnIndices.get(columnName);
        if (index == null || index >= values.length) {
            return null;
        }
        return values[index].trim();
    }

    private OffsetDateTime parseDateTime(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return java.time.LocalDateTime.parse(value, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .atOffset(ZoneOffset.UTC);
        } catch (Exception e) {
            log.warn("Failed to parse datetime: {}", value);
            return null;
        }
    }

    private BigDecimal parseNumeric(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            String cleaned = value.replaceAll("[^0-9.\\-]", "").trim();
            if (cleaned.isEmpty()) {
                return null;
            }
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            log.warn("Failed to parse numeric value: {}", value);
            return null;
        }
    }

    private boolean isValidTemperature(BigDecimal temperature) {
        return temperature.compareTo(BigDecimal.valueOf(-60)) >= 0 &&
               temperature.compareTo(BigDecimal.valueOf(60)) <= 0;
    }

    private boolean isValidHumidity(BigDecimal humidity) {
        return humidity.compareTo(BigDecimal.ZERO) >= 0 &&
               humidity.compareTo(BigDecimal.valueOf(100)) <= 0;
    }
}
