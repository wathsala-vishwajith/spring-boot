#!/bin/bash

# Run application with ZGC (Low latency garbage collector)

echo "Starting application with Z Garbage Collector (Low Latency)"
echo "==========================================================="

java \
  -Xms512m \
  -Xmx2g \
  -XX:+UseZGC \
  -XX:ZCollectionInterval=5 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/tmp/heapdump.hprof \
  -Xlog:gc*:file=/tmp/gc-zgc.log:time,uptime:filecount=5,filesize=10M \
  -Dcom.sun.management.jmxremote \
  -Dcom.sun.management.jmxremote.port=9010 \
  -Dcom.sun.management.jmxremote.local.only=false \
  -Dcom.sun.management.jmxremote.authenticate=false \
  -Dcom.sun.management.jmxremote.ssl=false \
  -jar target/performance-optimization-demo-1.0.0.jar

echo ""
echo "GC logs written to: /tmp/gc-zgc.log"
echo "Heap dump (if OOM): /tmp/heapdump.hprof"
echo "JMX Port: 9010"
echo ""
echo "ZGC provides predictable low pause times (< 10ms) independent of heap size"
