#!/bin/bash

# Run application with profiling and monitoring enabled

echo "Starting application with profiling enabled"
echo "==========================================="
echo ""
echo "Profiling Tools:"
echo "- VisualVM: Connect to localhost:9010"
echo "- JConsole: Connect to localhost:9010"
echo "- JProfiler: Attach to running JVM"
echo "- Java Mission Control: Connect to localhost:9010"
echo ""
echo "Monitoring Endpoints:"
echo "- Actuator: http://localhost:8080/actuator"
echo "- Metrics: http://localhost:8080/actuator/metrics"
echo "- Prometheus: http://localhost:8080/actuator/prometheus"
echo "- Thread Dump: http://localhost:8080/actuator/threaddump"
echo ""

java \
  -Xms512m \
  -Xmx2g \
  -XX:+UseG1GC \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/tmp/heapdump.hprof \
  -Xlog:gc*:file=/tmp/gc.log:time,uptime:filecount=5,filesize=10M \
  -Dcom.sun.management.jmxremote \
  -Dcom.sun.management.jmxremote.port=9010 \
  -Dcom.sun.management.jmxremote.local.only=false \
  -Dcom.sun.management.jmxremote.authenticate=false \
  -Dcom.sun.management.jmxremote.ssl=false \
  -Djdk.tracePinnedThreads=full \
  -XX:+FlightRecorder \
  -XX:StartFlightRecording=duration=60s,filename=/tmp/recording.jfr \
  -jar target/performance-optimization-demo-1.0.0.jar

echo ""
echo "Application started with profiling enabled"
echo "GC logs: /tmp/gc.log"
echo "Flight Recording: /tmp/recording.jfr"
