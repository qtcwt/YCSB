#!/bin/sh

mvn -pl kvtracer -am clean package
TRACE_DIR="traces/"
WORKLOAD_DIR="workloads/workload"
WORKLOADS="a b"
AMT="1m"
mkdir ${TRACE_DIR}
mkdir ${TRACE_DIR}"/1m"
for threads in 1
do
    for WL in $WORKLOADS
    do
        WL_PATH=${WORKLOAD_DIR}${WL}"_"${AMT}
        ./bin/ycsb load kvtracer -P $WL_PATH -threads $threads -p "kvtracer.tracefile="${TRACE_DIR}"/1m/"${WL}"-load" -p "kvtracer.totalthread="${threads}
        ./bin/ycsb run kvtracer -P $WL_PATH -threads $threads -p "kvtracer.tracefile="${TRACE_DIR}"/1m/"${WL}"-run" -p "kvtracer.totalthread="${threads}
    done
    # WL_PATH=${WORKLOAD_DIR}"_5m"
    # ./bin/ycsb load kvtracer -P $WL_PATH -threads $threads -p "kvtracer.tracefile="${TRACE_DIR}"/insert/load" -p "kvtracer.totalthread="${threads}
done

AMT="10m"
mkdir ${TRACE_DIR}"/ycsb"
for threads in 1 2 4 6 8 10 12 16 20 24 28 32 36 40
do
    for WL in $WORKLOADS
    do
        WL_PATH=${WORKLOAD_DIR}${WL}"_"${AMT}
        ./bin/ycsb load kvtracer -P $WL_PATH -threads $threads -p "kvtracer.tracefile="${TRACE_DIR}"/ycsb/"${WL}"-load" -p "kvtracer.totalthread="${threads}
        ./bin/ycsb run kvtracer -P $WL_PATH -threads $threads -p "kvtracer.tracefile="${TRACE_DIR}"/ycsb/"${WL}"-run" -p "kvtracer.totalthread="${threads}
    done
    # WL_PATH=${WORKLOAD_DIR}"_5m"
    # ./bin/ycsb load kvtracer -P $WL_PATH -threads $threads -p "kvtracer.tracefile="${TRACE_DIR}"/insert/load" -p "kvtracer.totalthread="${threads}
done

