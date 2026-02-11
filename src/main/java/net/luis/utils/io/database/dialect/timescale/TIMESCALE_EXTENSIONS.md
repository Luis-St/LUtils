# TimescaleDB Dialect Extension Guide

## Current State

Summary of current interfaces including inherited PostgreSQL methods.

## TimescaleColumn Extensions

### Gap Filling

#### interpolate
```java
@NonNull SqlExpression<?> interpolate();
```
Generates SQL: `interpolate(column)`

Fills missing values by linear interpolation between known data points. Must be used with `time_bucket_gapfill`. Unlike `locf()` which carries forward the last value, `interpolate()` calculates intermediate values.

#### interpolateWithLookup
```java
@NonNull SqlExpression<?> interpolateWithLookup(@NonNull SqlExpression<?> lookup);
```
Generates SQL: `interpolate(column, lookup)`

Fills missing values by linear interpolation using an explicit lookup expression for boundary values.

### Time Bucket Variants

#### timeBucketNg
```java
@NonNull SqlExpression<?> timeBucketNg(@NonNull Duration interval);
```
Generates SQL: `time_bucket_ng('interval', column)`

Next-generation time bucketing function that supports monthly and yearly intervals, unlike the standard `time_bucket` which only supports fixed-duration intervals.

#### timeBucketNgWithOrigin
```java
@NonNull SqlExpression<?> timeBucketNgWithOrigin(@NonNull Duration interval, @NonNull Instant origin);
```
Generates SQL: `time_bucket_ng('interval', column, 'origin')`

Next-generation time bucketing with a custom origin point for bucket alignment.

#### timeBucketWithOffset
```java
@NonNull SqlExpression<?> timeBucketWithOffset(@NonNull Duration interval, @NonNull Duration offset);
```
Generates SQL: `time_bucket('interval', column, 'offset')`

Time bucketing with an offset to shift bucket boundaries (e.g., aligning weekly buckets to start on Monday).

#### timeBucketWithTimezone
```java
@NonNull SqlExpression<?> timeBucketWithTimezone(@NonNull Duration interval, @NonNull String timezone);
```
Generates SQL: `time_bucket('interval', column, timezone => 'timezone')`

Time bucketing that respects time zone boundaries (e.g., daily buckets aligned to local midnight).

### Statistical Aggregates

#### statsAgg
```java
@NonNull SqlExpression<?> statsAgg();
```
Generates SQL: `stats_agg(column)`

Creates a statistical aggregate that can be used to compute various statistics (average, standard deviation, etc.) in a single pass. Used with accessor functions.

#### average (accessor)
```java
@NonNull SqlExpression<Double> statsAggAverage();
```
Generates SQL: `average(stats_agg(column))`

Returns the average from a statistical aggregate.

#### stddev (accessor)
```java
@NonNull SqlExpression<Double> statsAggStddev();
```
Generates SQL: `stddev(stats_agg(column))`

Returns the standard deviation from a statistical aggregate.

#### variance (accessor)
```java
@NonNull SqlExpression<Double> statsAggVariance();
```
Generates SQL: `variance(stats_agg(column))`

Returns the variance from a statistical aggregate.

#### numVals (accessor)
```java
@NonNull SqlExpression<Long> statsAggNumVals();
```
Generates SQL: `num_vals(stats_agg(column))`

Returns the number of values from a statistical aggregate.

### Counter & Gauge Aggregates

#### counterAgg
```java
@NonNull SqlExpression<?> counterAgg();
```
Generates SQL: `counter_agg(column, timeColumn)`

Creates a counter aggregate for monotonically increasing counters (e.g., bytes transferred, request counts). Handles counter resets.

#### gaugeAgg
```java
@NonNull SqlExpression<?> gaugeAgg();
```
Generates SQL: `gauge_agg(column, timeColumn)`

Creates a gauge aggregate for values that can go up and down (e.g., temperature, CPU usage).

#### delta
```java
@NonNull SqlExpression<Double> delta();
```
Generates SQL: `delta(counter_agg(column))`

Returns the total change in a counter over the time period, accounting for resets.

#### rate
```java
@NonNull SqlExpression<Double> rate();
```
Generates SQL: `rate(counter_agg(column))`

Returns the per-second rate of change of a counter over the time period.

#### idelta
```java
@NonNull SqlExpression<Double> idelta();
```
Generates SQL: `idelta(counter_agg(column))`

Returns the instantaneous delta (change between the last two values).

#### irate
```java
@NonNull SqlExpression<Double> irate();
```
Generates SQL: `irate(counter_agg(column))`

Returns the instantaneous per-second rate of change (based on the last two values).

#### extrapolatedDelta
```java
@NonNull SqlExpression<Double> extrapolatedDelta(@NonNull Duration interval);
```
Generates SQL: `extrapolated_delta(counter_agg(column), 'interval')`

Returns the delta extrapolated to the full bucket interval, handling partial buckets at boundaries.

#### extrapolatedRate
```java
@NonNull SqlExpression<Double> extrapolatedRate(@NonNull Duration interval);
```
Generates SQL: `extrapolated_rate(counter_agg(column), 'interval')`

Returns the rate extrapolated to the full bucket interval.

### Percentile & Approximate Functions

#### percentileAgg
```java
@NonNull SqlExpression<?> percentileAgg();
```
Generates SQL: `percentile_agg(column)`

Creates a percentile aggregate using the t-digest algorithm for approximate percentile calculation.

#### approxPercentile
```java
@NonNull SqlExpression<Double> approxPercentile(double percentile);
```
Generates SQL: `approx_percentile(percentile, percentile_agg(column))`

Returns the approximate value at the specified percentile (0.0 to 1.0).

#### approxPercentileRank
```java
@NonNull SqlExpression<Double> approxPercentileRank(double value);
```
Generates SQL: `approx_percentile_rank(value, percentile_agg(column))`

Returns the approximate percentile rank of the specified value.

#### hyperloglogDistinctCount
```java
@NonNull SqlExpression<Long> hyperloglogDistinctCount();
```
Generates SQL: `distinct_count(hyperloglog(column))`

Returns an approximate distinct count using HyperLogLog. Memory-efficient for high-cardinality columns.

### Time-Weighted Averages

#### timeWeight
```java
@NonNull SqlExpression<?> timeWeight(@NonNull String method, @NonNull SqlColumn<?> timeColumn);
```
Generates SQL: `time_weight('method', column, timeColumn)`

Creates a time-weighted aggregate. The `method` can be `'linear'` or `'LOCF'` (last observation carried forward).

#### timeWeightAverage
```java
@NonNull SqlExpression<Double> timeWeightAverage(@NonNull String method, @NonNull SqlColumn<?> timeColumn);
```
Generates SQL: `average(time_weight('method', column, timeColumn))`

Returns the time-weighted average of the column values.

### Histogram

#### histogram
```java
@NonNull SqlExpression<?> histogram(double min, double max, int numBuckets);
```
Generates SQL: `histogram(column, min, max, numBuckets)`

Creates a histogram with evenly-spaced buckets over the specified range.

### Rollup & Combine

#### rollup
```java
@NonNull SqlExpression<?> rollup(@NonNull SqlExpression<?> aggregate);
```
Generates SQL: `rollup(aggregate)`

Re-aggregates a previously computed aggregate at a coarser granularity. Used for hierarchical continuous aggregates.

---

## TimescaleTable Extensions

### Hypertable Management

#### createDistributedHypertable
```java
void createDistributedHypertable(@NonNull SqlColumn<?> timeColumn, @NonNull SqlColumn<?> partitionColumn);
```
Generates SQL: `SELECT create_distributed_hypertable('table', 'timeColumn', 'partitionColumn')`

Creates a distributed hypertable that spans multiple TimescaleDB nodes. Data is partitioned by both time and the partition column.

#### createDistributedHypertable (with replication)
```java
void createDistributedHypertable(@NonNull SqlColumn<?> timeColumn, @NonNull SqlColumn<?> partitionColumn, int replicationFactor);
```
Generates SQL: `SELECT create_distributed_hypertable('table', 'timeColumn', 'partitionColumn', replication_factor => n)`

Creates a distributed hypertable with a specified replication factor.

#### addDimension
```java
void addDimension(@NonNull SqlColumn<?> column, int numPartitions);
```
Generates SQL: `SELECT add_dimension('table', 'column', number_partitions => n)`

Adds a space dimension to the hypertable. Data is partitioned across this dimension using hashing.

#### addDimensionByRange
```java
void addDimensionByRange(@NonNull SqlColumn<?> column, long rangeSize);
```
Generates SQL: `SELECT add_dimension('table', 'column', chunk_time_interval => rangeSize)`

Adds a range-based dimension to the hypertable (e.g., for partitioning by a non-time column).

#### setIntegerNowFunc
```java
void setIntegerNowFunc(@NonNull String functionName);
```
Generates SQL: `SELECT set_integer_now_func('table', 'functionName')`

Sets the function used to determine "now" for hypertables with integer time columns.

### Chunk Management

#### showChunks
```java
@NonNull List<String> showChunks();
```
Generates SQL: `SELECT show_chunks('table')`

Returns a list of all chunks belonging to this hypertable.

#### showChunksOlderThan
```java
@NonNull List<String> showChunksOlderThan(@NonNull Duration olderThan);
```
Generates SQL: `SELECT show_chunks('table', older_than => 'interval')`

Returns chunks older than the specified duration.

#### dropChunks
```java
void dropChunks(@NonNull Duration olderThan);
```
Generates SQL: `SELECT drop_chunks('table', older_than => 'interval')`

Drops all chunks older than the specified duration.

#### dropChunksNewerThan
```java
void dropChunksNewerThan(@NonNull Duration newerThan);
```
Generates SQL: `SELECT drop_chunks('table', newer_than => 'interval')`

Drops all chunks newer than the specified duration.

#### moveChunk
```java
void moveChunk(@NonNull String chunkName, @NonNull String targetTablespace);
```
Generates SQL: `SELECT move_chunk('chunkName', destination_tablespace => 'target')`

Moves a chunk to a different tablespace. Useful for tiered storage.

#### moveChunkWithIndex
```java
void moveChunk(@NonNull String chunkName, @NonNull String targetTablespace, @NonNull String indexTablespace);
```
Generates SQL: `SELECT move_chunk('chunkName', destination_tablespace => 'target', index_destination_tablespace => 'indexTarget')`

Moves a chunk and its indexes to different tablespaces.

#### reorderChunk
```java
void reorderChunk(@NonNull String chunkName, @NonNull String indexName);
```
Generates SQL: `SELECT reorder_chunk('chunkName', index => 'indexName')`

Reorders a chunk's data according to the specified index. Improves query performance.

#### decompressChunk
```java
void decompressChunk(@NonNull String chunkName);
```
Generates SQL: `SELECT decompress_chunk('chunkName')`

Decompresses a previously compressed chunk.

### Compression Configuration

#### setCompressionSegmentBy
```java
void setCompressionSegmentBy(SqlColumn<?> @NonNull ... columns);
```
Generates SQL: `ALTER TABLE ... SET (timescaledb.compress, timescaledb.compress_segmentby = 'columns')`

Configures compression with segment-by columns. Data within each segment is compressed separately. Choose columns used in WHERE clauses.

#### setCompressionOrderBy
```java
void setCompressionOrderBy(SqlColumn<?> @NonNull ... columns);
```
Generates SQL: `ALTER TABLE ... SET (timescaledb.compress, timescaledb.compress_orderby = 'columns')`

Configures the order in which data is stored within compressed segments.

#### setCompressionChunkTimeInterval
```java
void setCompressionChunkTimeInterval(@NonNull Duration interval);
```
Generates SQL: `ALTER TABLE ... SET (timescaledb.compress, timescaledb.compress_chunk_time_interval = 'interval')`

Sets the time interval for merging small chunks during compression.

### Policy Management

#### removeCompressionPolicy
```java
void removeCompressionPolicy();
```
Generates SQL: `SELECT remove_compression_policy('table')`

Removes the automatic compression policy.

#### removeRetentionPolicy
```java
void removeRetentionPolicy();
```
Generates SQL: `SELECT remove_retention_policy('table')`

Removes the automatic data retention policy.

#### removeContinuousAggregatePolicy
```java
void removeContinuousAggregatePolicy(@NonNull String viewName);
```
Generates SQL: `SELECT remove_continuous_aggregate_policy('viewName')`

Removes the continuous aggregate refresh policy.

#### addReorderPolicy
```java
void addReorderPolicy(@NonNull String indexName);
```
Generates SQL: `SELECT add_reorder_policy('table', 'indexName')`

Adds an automatic reorder policy that reorders newly created chunks.

#### removeReorderPolicy
```java
void removeReorderPolicy();
```
Generates SQL: `SELECT remove_reorder_policy('table')`

Removes the automatic reorder policy.

### Continuous Aggregates

#### createContinuousAggregate
```java
void createContinuousAggregate(@NonNull String name, @NonNull String query);
```
Generates SQL: `CREATE MATERIALIZED VIEW name WITH (timescaledb.continuous) AS query`

Creates a continuous aggregate (automatically refreshed materialized view).

#### createContinuousAggregateNoData
```java
void createContinuousAggregateNoData(@NonNull String name, @NonNull String query);
```
Generates SQL: `CREATE MATERIALIZED VIEW name WITH (timescaledb.continuous) AS query WITH NO DATA`

Creates a continuous aggregate without initially populating it.

#### refreshContinuousAggregate
```java
void refreshContinuousAggregate(@NonNull String name, @NonNull Instant start, @NonNull Instant end);
```
Generates SQL: `CALL refresh_continuous_aggregate('name', 'start', 'end')`

Manually refreshes a continuous aggregate for the specified time range.

#### dropContinuousAggregate
```java
void dropContinuousAggregate(@NonNull String name);
```
Generates SQL: `DROP MATERIALIZED VIEW name`

Drops a continuous aggregate.

#### enableContinuousAggregateRealtime
```java
void enableContinuousAggregateRealtime(@NonNull String name);
```
Generates SQL: `ALTER MATERIALIZED VIEW name SET (timescaledb.materialized_only = false)`

Enables real-time aggregation that combines materialized data with recent unmaterialized data.

#### disableContinuousAggregateRealtime
```java
void disableContinuousAggregateRealtime(@NonNull String name);
```
Generates SQL: `ALTER MATERIALIZED VIEW name SET (timescaledb.materialized_only = true)`

Disables real-time aggregation. Only returns materialized data.

### Diagnostics

#### hypertableSize
```java
long hypertableSize();
```
Generates SQL: `SELECT hypertable_size('table')`

Returns the total disk size of the hypertable including indexes and toast.

#### hypertableDetailedSize
```java
@NonNull SqlExpression<?> hypertableDetailedSize();
```
Generates SQL: `SELECT * FROM hypertable_detailed_size('table')`

Returns detailed size information including table, index, and toast sizes per node.

#### chunkCompressionStats
```java
@NonNull SqlExpression<?> chunkCompressionStats();
```
Generates SQL: `SELECT * FROM chunk_compression_stats('table')`

Returns compression statistics for each chunk.

#### approximateRowCount
```java
long approximateRowCount();
```
Generates SQL: `SELECT approximate_row_count('table')`

Returns an approximate row count using statistics. Much faster than `COUNT(*)`.

---

## Supporting Types

### TimescaleCompressMethod (new enum)
```java
public enum TimescaleCompressMethod {
    GORILLA,
    DELTADELTA,
    ARRAY,
    DICTIONARY,
    LZ4
}
```

### TimescaleTimeWeightMethod (new enum)
```java
public enum TimescaleTimeWeightMethod {
    LINEAR,
    LOCF
}
```
