#!/bin/bash

# Run application with G1GC (Recommended for most applications)

echo "Starting application with G1 Garbage Collector"
echo "=============================================="

java \
  -Xms512m \
  -Xmx2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:G1HeapRegionSize=16M \
  -XX:InitiatingHeapOccupancyPercent=45 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/tmp/heapdump.hprof \
  -Xlog:gc*:file=/tmp/gc-g1.log:time,uptime:filecount=5,filesize=10M \
  -Dcom.sun.management.jmxremote \
  -Dcom.sun.management.jmxremote.port=9010 \
  -Dcom.sun.management.jmxremote.local.only=false \
  -Dcom.sun.management.jmxremote.authenticate=false \
  -Dcom.sun.management.jmxremote.ssl=false \
  -jar target/performance-optimization-demo-1.0.0.jar

echo ""
echo "GC logs written to: /tmp/gc-g1.log"
echo "Heap dump (if OOM): /tmp/heapdump.hprof"
echo "JMX Port: 9010"
