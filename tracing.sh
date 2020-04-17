#!/bin/sh

# mvn -pl kvtracer -am clean package
TRACE_DIR="traces/"
WORKLOAD_DIR="workloads/workload"
WORKLOADS="a b"
RECORD_AMOUNTS="100k 1m"
mkdir ${TRACE_DIR}
for WL in $WORKLOADS
do
    for AMT in $RECORD_AMOUNTS
    do
        mkdir ${TRACE_DIR}"/"${AMT}
        WL_PATH=${WORKLOAD_DIR}${WL}"_"${AMT}
        for threads in 1 2 4 6 10 16 20 24 32 40
        do
            ./bin/ycsb load kvtracer -P $WL_PATH -threads $threads -p "kvtracer.tracefile="${TRACE_DIR}"/"${AMT}"/"${WL}"-load" -p "kvtracer.totalthread="${threads}
            ./bin/ycsb run kvtracer -P $WL_PATH -threads $threads -p "kvtracer.tracefile="${TRACE_DIR}"/"${AMT}"/"${WL}"-run" -p "kvtracer.totalthread="${threads}
        done
    done
done