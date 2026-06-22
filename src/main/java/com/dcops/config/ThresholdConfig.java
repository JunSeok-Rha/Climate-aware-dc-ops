package com.dcops.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "dcops.threshold")
public class ThresholdConfig {

    private ScoreBand score = new ScoreBand();
    private ImbalanceBand imbalance = new ImbalanceBand();

    @Data
    public static class ScoreBand {
        private double lowMax = 30.0;
        private double mediumMax = 55.0;
        private double highMax = 75.0;
    }

    @Data
    public static class ImbalanceBand {
        private double noneMax = 20.0;
        private double moderateMax = 50.0;
    }
}
