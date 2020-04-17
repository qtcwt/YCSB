#!/bin/sh

mvn -pl kvtracer -am clean package
TRACE_DIR="traces/"
WORKLOAD_DIR="workloads/workload"
WORKLOADS="a b"
rm ${TRACE_DIR}"*"
for WL in $WORKLOADS
do
    WL_PATH=${WORKLOAD_DIR}${WL}
    for threads in 1 2 4 6 10 16 20 24 32 40
    do
        ./bin/ycsb load kvtracer -P $WL_PATH -threads $threads -p "kvtracer.tracefile="${TRACE_DIR}${WL}"-load" -p "kvtracer.totalthread="${threads}
        ./bin/ycsb run kvtracer -P $WL_PATH -threads $threads -p "kvtracer.tracefile="${TRACE_DIR}${WL}"-run" -p "kvtracer.totalthread="${threads}
    done
done