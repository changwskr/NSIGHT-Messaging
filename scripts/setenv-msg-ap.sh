#!/usr/bin/env bash
# NSIGHT Message Management AP JVM 기준 예시
export JAVA_OPTS="${JAVA_OPTS} \
-Xms12g \
-Xmx12g \
-Xss512k \
-XX:+UseG1GC \
-XX:MaxGCPauseMillis=200 \
-XX:MetaspaceSize=256m \
-XX:MaxMetaspaceSize=1g \
-XX:+HeapDumpOnOutOfMemoryError \
-XX:HeapDumpPath=/logs/dump \
-Xlog:gc*:file=/logs/gc/msg-ap-gc-%t.log:time,uptime,level,tags:filecount=10,filesize=100M \
-Dfile.encoding=UTF-8 \
-Duser.timezone=Asia/Seoul \
-Dnsight.ap-id=${AP_ID:-MSG-C1-AP01}"
