# risk-scoring

TODO

## v1 가중치 초기값
heatRisk = temperature(0.4) + cpuUsage(0.35) + workload(0.25)
coolingStress = temperature(0.35) + humidity(0.25) + cpuUsage(0.25) + memory(0.15)

## Threshold 초기값
score: LOW(0~30) / MEDIUM(30~55) / HIGH(55~75) / EXTREME(75+)
imbalance: NONE(0~20) / MODERATE(20~50) / SEVERE(50+)

## 설계 전제
가중치는 validation 결과 후 조정 가능한 파라미터. 고정 진리 아님.
