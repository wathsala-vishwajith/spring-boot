# Performance Optimization Demo

A comprehensive Java/Spring Boot application demonstrating various performance optimization techniques including profiling, memory leak detection, GC tuning, connection pool optimization, and async processing with CompletableFuture and virtual threads.

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Building the Application](#building-the-application)
- [Running the Application](#running-the-application)
- [Performance Optimization Techniques](#performance-optimization-techniques)
  - [1. Profiling with JProfiler/VisualVM](#1-profiling-with-jprofilervisualvm)
  - [2. Memory Leak Detection](#2-memory-leak-detection)
  - [3. GC Tuning](#3-gc-tuning)
  - [4. Connection Pool Optimization](#4-connection-pool-optimization)
  - [5. Async Processing with CompletableFuture](#5-async-processing-with-completablefuture)
  - [6. Virtual Threads (Java 21+)](#6-virtual-threads-java-21)
- [API Endpoints](#api-endpoints)
- [Monitoring and Metrics](#monitoring-and-metrics)
- [Best Practices](#best-practices)

## Features

✅ **Profiling & Monitoring**
- JMX-based monitoring
- JVM metrics collection (CPU, Memory, Threads, GC)
- Integration with VisualVM, JProfiler, JConsole
- Spring Boot Actuator endpoints
- Prometheus metrics export

✅ **Memory Management**
- Memory leak detection examples
- Common leak patterns and fixes
- Heap dump analysis guidance
- ThreadLocal cleanup strategies

✅ **Garbage Collection**
- Multiple GC configurations (G1, ZGC, Shenandoah)
- GC logging and analysis
- GC tuning recommendations
- Performance comparison scripts

✅ **Connection Pooling**
- HikariCP optimization
- Pool sizing calculations
- Connection leak detection
- Batch processing examples

✅ **Async Processing**
- CompletableFuture patterns
- Parallel processing
- Error handling in async context
- CPU vs I/O bound operations

✅ **Virtual Threads**
- Java 21 virtual threads examples
- Massive concurrency demonstrations
- Structured concurrency
- Performance comparisons

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- (Optional) VisualVM or JProfiler for profiling
- (Optional) Docker for containerized deployment

## Building the Application

```bash
# Build with Maven
mvn clean package

# Skip tests if needed
mvn clean package -DskipTests
```

## Running the Application

### Standard Run

```bash
java -jar target/performance-optimization-demo-1.0.0.jar
```

### With G1 Garbage Collector (Recommended)

```bash
chmod +x run-with-g1gc.sh
./run-with-g1gc.sh
```

### With ZGC (Low Latency)

```bash
chmod +x run-with-zgc.sh
./run-with-zgc.sh
```

### With Shenandoah GC

```bash
chmod +x run-with-shenandoah.sh
./run-with-shenandoah.sh
```

### With Profiling Enabled

```bash
chmod +x run-for-profiling.sh
./run-for-profiling.sh
```

The application will start on `http://localhost:8080`

## Performance Optimization Techniques

### 1. Profiling with JProfiler/VisualVM

#### Using VisualVM

1. Start the application with JMX enabled (use `run-for-profiling.sh`)
2. Open VisualVM
3. Connect to `localhost:9010`
4. Monitor:
   - CPU usage and thread activity
   - Memory usage and GC activity
   - Thread dumps and deadlock detection
   - Heap dumps for memory analysis

#### Using JProfiler

1. Start JProfiler
2. Attach to the running JVM process
3. Analyze:
   - CPU hotspots
   - Memory allocations
   - Thread analysis
   - Database queries

#### API Endpoints for Profiling

```bash
# Get comprehensive profiling info
curl http://localhost:8080/api/profiling/info

# Get memory info
curl http://localhost:8080/api/profiling/memory

# Get thread info
curl http://localhost:8080/api/profiling/threads

# Get GC statistics
curl http://localhost:8080/api/profiling/gc

# Run CPU-intensive test
curl -X POST "http://localhost:8080/api/profiling/cpu-test?iterations=10"

# Run memory allocation test
curl -X POST "http://localhost:8080/api/profiling/memory-test?objectCount=1000"
```

### 2. Memory Leak Detection

#### Common Memory Leak Patterns

The application demonstrates these common leak patterns:

1. **Static Collections** - Objects never removed from static collections
2. **Unbounded Caches** - Caches without eviction policies
3. **ThreadLocal Variables** - Not cleaned up properly
4. **Event Listeners** - Registered but never unregistered
5. **Unclosed Resources** - Streams, connections not closed

#### Detection Steps

1. Run the application and execute leak demonstrations:

```bash
# Demonstrate static collection leak
curl -X POST "http://localhost:8080/api/memory/demonstrate-static-leak?objectCount=1000"

# Demonstrate cache leak
curl -X POST "http://localhost:8080/api/memory/demonstrate-cache-leak?entries=1000"

# Demonstrate ThreadLocal leak
curl -X POST "http://localhost:8080/api/memory/demonstrate-threadlocal-leak?dataSize=100"
```

2. Monitor heap usage in VisualVM
3. Take heap dumps before and after
4. Analyze with Eclipse MAT or VisualVM

#### Cleanup Methods

```bash
# Cleanup static collection
curl -X POST http://localhost:8080/api/memory/cleanup-static

# Cleanup ThreadLocal
curl -X POST http://localhost:8080/api/memory/cleanup-threadlocal
```

#### Get Detection Tips

```bash
curl http://localhost:8080/api/memory/leak-detection-tips
```

### 3. GC Tuning

#### G1GC (Recommended for Most Applications)

```bash
java -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:G1HeapRegionSize=16M \
     -XX:InitiatingHeapOccupancyPercent=45 \
     -jar target/performance-optimization-demo-1.0.0.jar
```

**When to use:**
- General-purpose applications
- Heap sizes 4GB+
- Target predictable pause times

#### ZGC (Ultra-Low Latency)

```bash
java -XX:+UseZGC \
     -XX:ZCollectionInterval=5 \
     -jar target/performance-optimization-demo-1.0.0.jar
```

**When to use:**
- Applications requiring < 10ms pause times
- Large heaps (8GB+)
- Latency-sensitive applications

#### Shenandoah GC

```bash
java -XX:+UseShenandoahGC \
     -jar target/performance-optimization-demo-1.0.0.jar
```

**When to use:**
- Low pause time requirements
- Medium to large heaps
- Alternative to ZGC

#### GC Logging

All run scripts enable GC logging:

```bash
# View GC logs
tail -f /tmp/gc.log

# Analyze with GCViewer or GCEasy.io
```

#### Memory Settings

```bash
-Xms512m              # Initial heap size
-Xmx2g                # Maximum heap size
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/tmp/heapdump.hprof
```

### 4. Connection Pool Optimization

The application uses HikariCP (fastest connection pool).

#### Optimal Pool Sizing

Formula: `pool_size = cores * 2 + effective_spindle_count`

```bash
# Get optimal pool size calculation
curl http://localhost:8080/api/connection-pool/optimal-size
```

#### Pool Statistics

```bash
# Get real-time pool statistics
curl http://localhost:8080/api/connection-pool/statistics
```

#### Configuration (application.yml)

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 30000
      max-lifetime: 1800000
      idle-timeout: 600000
      leak-detection-threshold: 2000
      register-mbeans: true
```

#### Stress Testing

```bash
# Test pool with concurrent requests
curl -X POST "http://localhost:8080/api/connection-pool/stress-test?concurrentRequests=50"
```

#### Batch Operations

```bash
# Demonstrate efficient batch operations
curl -X POST "http://localhost:8080/api/connection-pool/batch-demo?recordCount=1000"
```

#### Best Practices

```bash
# Get connection pool best practices
curl http://localhost:8080/api/connection-pool/best-practices
```

### 5. Async Processing with CompletableFuture

#### Simple Async Operation

```bash
curl "http://localhost:8080/api/async/simple?input=test"
```

#### Parallel Processing

```bash
curl -X POST http://localhost:8080/api/async/parallel \
  -H "Content-Type: application/json" \
  -d '["task1", "task2", "task3", "task4", "task5"]'
```

#### Chained Operations

```bash
curl "http://localhost:8080/api/async/chained?input=data"
```

#### Combined Operations

```bash
curl "http://localhost:8080/api/async/combined?userId=user123"
```

#### Racing Operations

```bash
curl http://localhost:8080/api/async/race
```

#### With Timeout

```bash
curl "http://localhost:8080/api/async/timeout?input=test"
```

#### Batch Processing

```bash
curl -X POST "http://localhost:8080/api/async/batch?batchSize=10" \
  -H "Content-Type: application/json" \
  -d '["item1", "item2", "item3", "item4", "item5", "item6", "item7", "item8", "item9", "item10"]'
```

#### CPU-Bound vs I/O-Bound

```bash
# CPU-bound processing
curl "http://localhost:8080/api/async/cpu-bound?iterations=5"
```

### 6. Virtual Threads (Java 21+)

#### Enable Virtual Threads

Set in `application.yml`:

```yaml
app:
  virtual-threads:
    enabled: true
```

#### Simple Virtual Thread

```bash
curl http://localhost:8080/api/virtual-threads/simple
```

#### Massive Concurrency (10,000 threads)

```bash
curl "http://localhost:8080/api/virtual-threads/massive-concurrency?threadCount=10000"
```

#### Compare Virtual vs Platform Threads

```bash
curl "http://localhost:8080/api/virtual-threads/compare?threadCount=1000"
```

#### I/O-Bound Operations

```bash
curl "http://localhost:8080/api/virtual-threads/io-bound?operationCount=100"
```

#### Structured Concurrency

```bash
curl http://localhost:8080/api/virtual-threads/structured-concurrency
```

#### Virtual Thread Best Practices

```bash
curl http://localhost:8080/api/virtual-threads/best-practices
```

#### Monitor Thread Pinning

Run with flag:
```bash
java -Djdk.tracePinnedThreads=full -jar target/performance-optimization-demo-1.0.0.jar
```

## API Endpoints

### General

- `GET /api/info` - Application information and available endpoints

### Profiling

- `GET /api/profiling/info` - Complete profiling information
- `GET /api/profiling/memory` - Memory statistics
- `GET /api/profiling/threads` - Thread statistics
- `GET /api/profiling/gc` - GC statistics
- `POST /api/profiling/cpu-test` - Run CPU-intensive test
- `POST /api/profiling/memory-test` - Run memory allocation test

### Memory Leak Detection

- `GET /api/memory/leak-detection-tips` - Detection tips
- `POST /api/memory/demonstrate-static-leak` - Demo static collection leak
- `POST /api/memory/cleanup-static` - Cleanup static collection
- `POST /api/memory/demonstrate-cache-leak` - Demo cache leak
- `POST /api/memory/demonstrate-threadlocal-leak` - Demo ThreadLocal leak
- `POST /api/memory/cleanup-threadlocal` - Cleanup ThreadLocal

### Async Processing

- `GET /api/async/simple` - Simple async operation
- `POST /api/async/parallel` - Parallel processing
- `GET /api/async/chained` - Chained operations
- `GET /api/async/combined` - Combined operations
- `GET /api/async/race` - Racing operations
- `GET /api/async/timeout` - With timeout
- `POST /api/async/batch` - Batch processing
- `GET /api/async/cpu-bound` - CPU-bound processing

### Virtual Threads

- `GET /api/virtual-threads/simple` - Simple virtual thread
- `GET /api/virtual-threads/massive-concurrency` - Massive concurrency test
- `GET /api/virtual-threads/compare` - Compare with platform threads
- `GET /api/virtual-threads/io-bound` - I/O-bound operations
- `GET /api/virtual-threads/structured-concurrency` - Structured concurrency
- `GET /api/virtual-threads/best-practices` - Best practices

### Connection Pool

- `GET /api/connection-pool/statistics` - Pool statistics
- `GET /api/connection-pool/optimal-size` - Calculate optimal size
- `POST /api/connection-pool/stress-test` - Stress test pool
- `POST /api/connection-pool/batch-demo` - Batch operations demo
- `GET /api/connection-pool/leak-detection` - Leak detection info
- `GET /api/connection-pool/best-practices` - Best practices
- `GET /api/connection-pool/monitoring` - Monitoring queries

## Monitoring and Metrics

### Actuator Endpoints

- **Health**: `http://localhost:8080/actuator/health`
- **Metrics**: `http://localhost:8080/actuator/metrics`
- **Prometheus**: `http://localhost:8080/actuator/prometheus`
- **Thread Dump**: `http://localhost:8080/actuator/threaddump`
- **Heap Dump**: `http://localhost:8080/actuator/heapdump`

### JMX Monitoring

Connect to `localhost:9010` using:
- VisualVM
- JConsole
- Java Mission Control

### Prometheus Integration

Metrics are available at `/actuator/prometheus`

Example metrics:
- `jvm_memory_used_bytes`
- `jvm_gc_pause_seconds`
- `hikaricp_connections_active`
- `http_server_requests_seconds`

## Best Practices

### Memory Management

1. Always close resources (use try-with-resources)
2. Avoid static collections
3. Implement bounded caches with eviction
4. Clean up ThreadLocal variables
5. Unregister listeners and callbacks

### GC Tuning

1. Start with G1GC for most applications
2. Set realistic heap sizes (don't over-allocate)
3. Enable GC logging for production
4. Monitor GC pause times
5. Use ZGC/Shenandoah for ultra-low latency

### Connection Pooling

1. Size pool based on formula: `cores * 2 + spindle_count`
2. Enable connection leak detection in development
3. Use batch operations when possible
4. Monitor pool metrics
5. Set appropriate timeouts

### Async Processing

1. Use CompletableFuture for async operations
2. Handle exceptions properly in async context
3. Use appropriate thread pool for task type
4. Avoid blocking in async operations
5. Consider virtual threads for massive I/O concurrency

### Virtual Threads

1. Use for I/O-bound tasks only
2. Avoid synchronized blocks (use ReentrantLock)
3. Don't pool virtual threads
4. Create one thread per task
5. Monitor for thread pinning

## Profiling Tools

### VisualVM

1. Download from [visualvm.github.io](https://visualvm.github.io)
2. Connect to JMX port 9010
3. Monitor CPU, Memory, Threads, and GC

### JProfiler

1. Commercial tool with advanced features
2. Attach to running JVM
3. Analyze CPU hotspots, memory allocations, and more

### Java Flight Recorder

```bash
# Start recording
java -XX:StartFlightRecording=duration=60s,filename=/tmp/recording.jfr

# Analyze with Java Mission Control
jmc /tmp/recording.jfr
```

### Eclipse MAT (Memory Analyzer)

1. Take heap dump: `curl http://localhost:8080/actuator/heapdump -o heap.hprof`
2. Open in Eclipse MAT
3. Run Leak Suspects Report

## Troubleshooting

### High Memory Usage

1. Check for memory leaks using heap dump
2. Review GC logs
3. Adjust heap size
4. Enable leak detection

### Slow Performance

1. Profile with VisualVM/JProfiler
2. Check thread pools and connection pools
3. Review GC pause times
4. Optimize database queries

### Connection Pool Exhaustion

1. Check pool statistics
2. Look for connection leaks
3. Increase pool size if needed
4. Review transaction boundaries

## License

MIT License

## Author

Performance Optimization Demo - A comprehensive guide to Java performance optimization
