import os
import sys
import subprocess

test_range = range(2, 4)
heap_size = '350M'
test_size = 'default'
test_suite = 'h2'

if sys.argv[1] == 'generate':
    if len(sys.argv) >= 3:
        test_size = sys.argv[2]
    for i in test_range:
        cmd = 'java -XX:+PrintGCDetails -XX:+UnlockDiagnosticVMOptions \
                    -XX:+PrintClassHistogramAfterFullGC -XX:+PrintClassHistogramBeforeFullGC \
                    -XX:+PrintGCApplicationConcurrentTime -XX:+PrintGCApplicationStoppedTime \
                    -XX:+PrintHeapAtGC -XX:+PrintHeapAtGCExtended \
                    -XX:+PrintOldPLAB -XX:+PrintPLAB -XX:+PrintTLAB \
                    -XX:+PrintParallelOldGCPhaseTimes -XX:+PrintReferenceGC \
                    -XX:+PrintTenuringDistribution -XX:+PrintPromotionFailure \
                    -XX:+PrintStringDeduplicationStatistics -XX:+PrintAdaptiveSizePolicy \
                    -XX:+TraceDynamicGCThreads -XX:+TraceMetadataHumongousAllocation \
                    -Xloggc:log/{3}_{0}.log \
                    -Xmx{1} -Xms{1} \
                    -XX:NewRatio={0} -XX:MaxTenuringThreshold=2 \
               -jar tool/dacapo-9.12-MR1-bach.jar {3} -s {2}'.format(i, heap_size, test_size, test_suite)
        os.system(cmd)
else:
    for i in test_range:
        cmd = 'java -jar LogReader.jar log/h2_{0}_2.log &'.format(i)
        subprocess.Popen(cmd, stderr=None)
