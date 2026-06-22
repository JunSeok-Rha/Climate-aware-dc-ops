package com.dcops.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "dcops.scoring.weights")
public class ScoringWeightConfig {

    private HeatRiskWeights heatRisk = new HeatRiskWeights();
    private CoolingStressWeights coolingStress = new CoolingStressWeights();

    @Data
    public static class HeatRiskWeights {
        private double temperature = 0.4;
        private double cpuUsage = 0.35;
        private double workload = 0.25;
    }

    @Data
    public static class CoolingStressWeights {
        private double temperature = 0.35;
        private double humidity = 0.25;
        private double cpuUsage = 0.25;
        private double memoryUsage = 0.15;
    }
}
