#!/bin/bash

# Run application with Shenandoah GC (Low pause time garbage collector)

echo "Starting application with Shenandoah Garbage Collector"
echo "======================================================"

java \
  -Xms512m \
  -Xmx2g \
  -XX:+UseShenandoahGC \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/tmp/heapdump.hprof \
  -Xlog:gc*:file=/tmp/gc-shenandoah.log:time,uptime:filecount=5,filesize=10M \
  -Dcom.sun.management.jmxremote \
  -Dcom.sun.management.jmxremote.port=9010 \
  -Dcom.sun.management.jmxremote.local.only=false \
  -Dcom.sun.management.jmxremote.authenticate=false \
  -Dcom.sun.management.jmxremote.ssl=false \
  -jar target/performance-optimization-demo-1.0.0.jar

echo ""
echo "GC logs written to: /tmp/gc-shenandoah.log"
echo "Heap dump (if OOM): /tmp/heapdump.hprof"
echo "JMX Port: 9010"
echo ""
echo "Shenandoah provides predictable low pause times through concurrent compaction"
