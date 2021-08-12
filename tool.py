# encoding: utf-8

import os
import re
import time
import subprocess
import configparser

class Optimizer:
    def __init__(self):
        self.default_args = '-XX:+PrintGCDetails -XX:+UnlockDiagnosticVMOptions \
                             -XX:+PrintClassHistogramAfterFullGC -XX:+PrintClassHistogramBeforeFullGC \
                             -XX:+PrintGCApplicationConcurrentTime -XX:+PrintGCApplicationStoppedTime \
                             -XX:+PrintHeapAtGC -XX:+PrintHeapAtGCExtended \
                             -XX:+PrintOldPLAB -XX:+PrintPLAB -XX:+PrintTLAB \
                             -XX:+PrintParallelOldGCPhaseTimes -XX:+PrintReferenceGC \
                             -XX:+PrintTenuringDistribution -XX:+PrintPromotionFailure \
                             -XX:+PrintStringDeduplicationStatistics -XX:+PrintAdaptiveSizePolicy \
                             -XX:+TraceDynamicGCThreads -XX:+TraceMetadataHumongousAllocation '
        self.conf = configparser.ConfigParser()
        self.conf_path = './tool.ini'
        if os.path.exists(self.conf_path):
            self.conf.read(self.conf_path)
        else:
            self.conf.add_section('conf')
            with open(self.conf_path, 'w') as f:
                self.conf.write(f)
        self.func = {
            'opt': self.optimize,
            'gen': self.generate,
            'show': self.show,
            'time': self.execution_time,
            'demo': self.show_demo,
            'quit': self.quit,
            'reload': self.reload,
        }

    def main(self):
        def wrong_input():
            self.output_str('no such operation')

        if os.path.exists('./LogReader.jar') == False:
            os.system('make jar')

        # self.set_for_test()

        while(True):
            t = input('opration: ')
            self.func.get(t, wrong_input)()

    def set_for_test(self):
        self.conf.set('conf', 'jar_path', 'D:/share/MM/LogicalGC/tool/dacapo-9.12-MR1-bach.jar')
        self.conf.set('conf', 'run_argument', 'h2 -s small')
        self.conf.set('conf', 'heap_size', '75M')
        self.conf.set('conf', 'test_argument', 'SurvivorRatio')
        self.conf.set('conf', 'test_range', '4:16:4')
        with open(self.conf_path, 'w') as f:
            self.conf.write(f)

    def warm_up(self):
        # execute warm-up first to stabilize the caches
        heap_size = self.require_arg('heap_size')
        jar_path = self.require_arg('jar_path')
        run_argument = self.require_arg('run_argument')
        cmd = 'java -Xmx{0} -Xms{0} -jar {1} {2} >NUL 2>&1'.format(heap_size, jar_path, run_argument)
        self.output_str('执行预热')
        os.system(cmd)

    def quit(self):
        exit(0)

    def reload(self):
        self.conf.read(self.conf_path)

    def parse_test_range(self, test_range_str):
        if re.match(r'^\d+:\d+:\d+$', test_range_str) == None:
            return None
        index = test_range_str.find(':')
        start = int(test_range_str[:index])
        test_range_str = test_range_str[index+1:]
        index = test_range_str.find(':')
        end = int(test_range_str[:index])
        test_range_str = test_range_str[index+1:]
        step = int(test_range_str)
        return range(start, end, step)

    def require_arg(self, name):
        if self.conf.has_option('conf', name):
            return self.conf.get('conf', name)
        else:
            t = input(name + ': ')
            self.conf.set('conf', name, t)
            with open(self.conf_path, 'w') as f:
                self.conf.write(f)
            return t

    def output_str(self, string):
        print(string)

    def show_demo(self):
        output_data = [
            '数据来自实测：',
            '执行预热',
            '生成日志到log/75M_default.log',
            '解析日志log/75M_default.log',
            '建议的NewRatio：5',
            '正在搜索参数...',
            '搜索到的最优参数：-XX:SurvivorRatio=4 -XX:TargetSurvivorRatio=25 -XX:OldPLABSize=1024 -XX:YoungPLABSize=4096 -XX:PLABWeight=80 ',
            '生成日志到log/75M_opt.log',
            '解析日志log/75M_opt.log',
            '执行时间减少：68.55%',
            '吞吐率提升：56.32%',
            'GC时间减少：98.84%'
        ]
        for s in output_data:
            self.output_str(s)
            time.sleep(0.8)

    def search_arguments(self, opt_arg):
        heap_size = self.require_arg('heap_size')
        jar_path = self.require_arg('jar_path')
        run_argument = self.require_arg('run_argument')
        self.output_str('正在搜索参数...')
        arguments = [
            ('SurvivorRatio', (4, 16, 4)), # default: 8
            ('TargetSurvivorRatio', (25, 100, 25)), # default: 50
            # ('OldPLABSize', (1024, 5120, 1024)), # default: 1024
            # ('YoungPLABSize', (1024, 5120, 1024)), # default: 4096
            # ('PLABWeight', (70, 85, 5)), # default: 75
        ]
        index = [0]*len(arguments)
        for i in range(0, len(index)):
            index[i] = arguments[i][1][0]
        min_time = -1
        result = ''
        while (index[0] < arguments[0][1][1]):
            cmd = 'java -Xmx{0} -Xms{0} {1} '.format(heap_size, opt_arg)
            cmd_arg = ''
            for i in range(0, len(arguments)):
                arg = arguments[i]
                cmd_arg += '-XX:{0}={1} '.format(arg[0], index[i])
            cmd += cmd_arg
            cmd += '-jar {0} {1} >NUL 2>&1'.format(jar_path, run_argument)
            st = time.time()
            os.system(cmd)
            et = time.time()
            if min_time == -1 or min_time > (et - st):
                min_time = (et - st)
                result = cmd_arg
            i = len(arguments) - 1
            while True:
                arg = arguments[i]
                index[i] += arg[1][2]
                if index[i] >= arg[1][1]:
                    if i > 0:
                        index[i] = arg[1][0]
                        i -= 1
                    else:
                        break
                else:
                    break
        self.output_str('搜索到的最优参数：' + result)
        return result

    def optimize(self):
        heap_size = self.require_arg('heap_size')
        jar_path = self.require_arg('jar_path')
        run_argument = self.require_arg('run_argument')
        self.warm_up()

        append_args = '-Xloggc:log/{0}_default.log -Xmx{0} -Xms{0} '.format(heap_size)
        cmd = 'java ' + self.default_args + append_args + '-jar {0} {1} >NUL 2>&1'.format(jar_path, run_argument)
        self.output_str('生成日志到log/{0}_default.log'.format(heap_size))
        os.system(cmd)

        self.output_str('解析日志log/{0}_default.log'.format(heap_size))
        cmd = 'java -jar LogReader.jar analyze log/{0}_default.log'.format(heap_size)
        p = subprocess.Popen(cmd, stdout=subprocess.PIPE, shell=True)
        p.wait()
        out1 = str(p.stdout.read())[2:-5].split('|')

        cmd = 'java -jar LogReader.jar optimize log/{0}_default.log'.format(heap_size)
        p = subprocess.Popen(cmd, stdout=subprocess.PIPE, shell=True)
        p.wait()
        line = str(p.stdout.readline())
        NewRatio_line = line[2:-3]
        NewRatio = int(NewRatio_line[NewRatio_line.rfind(' ')+1:])
        line = str(p.stdout.readline())
        # SurvivorRatio_line = line[2:-3]
        # SurvivorRatio = int(SurvivorRatio_line[SurvivorRatio_line.rfind(' ')+1:])
        line = str(p.stdout.readline())
        # TargetSurvivorRatio_line = line[2:-3]
        # TargetSurvivorRatio = int(TargetSurvivorRatio_line[TargetSurvivorRatio_line.rfind(' ')+1:])
        self.output_str('建议的NewRatio：%d'%(NewRatio))
        # self.output_str('建议的SurvivorRatio：%d'%(SurvivorRatio))
        # self.output_str('建议的TargetSurvivorRatio：%d'%(TargetSurvivorRatio))

        search_result = self.search_arguments('-XX:NewRatio={0}'.format(NewRatio))

        append_args = '-Xloggc:log/{0}_opt.log -Xmx{0} -Xms{0} -XX:NewRatio={1} {2} '.format(heap_size, NewRatio, search_result)
        cmd = 'java ' + self.default_args + append_args + '-jar {0} {1} >NUL 2>&1'.format(jar_path, run_argument)
        self.output_str('生成日志到log/{0}_opt.log'.format(heap_size))
        os.system(cmd)

        self.output_str('解析日志log/{0}_opt.log'.format(heap_size))
        cmd = 'java -jar LogReader.jar analyze log/{0}_opt.log'.format(heap_size)
        p = subprocess.Popen(cmd, stdout=subprocess.PIPE, shell=True)
        p.wait()
        out2 = str(p.stdout.read())[2:-5].split('|')

        run_time = (float(out1[0]) - float(out2[0]))/float(out1[0])*100
        throught_rate = float(out2[1]) - float(out1[1])
        gc_time = (float(out1[2]) - float(out2[2]))/float(out1[2])*100
        self.output_str('执行时间减少：%.2f%%'%(run_time))
        self.output_str('吞吐率提升：%.2f%%'%(throught_rate))
        self.output_str('GC时间减少：%.2f%%' % (gc_time))

    def generate(self):
        test_range = self.require_arg('test_range')
        test_range = self.parse_test_range(test_range)
        if test_range == None:
            return
        test_argument = self.require_arg('test_argument')
        heap_size = self.require_arg('heap_size')
        jar_path = self.require_arg('jar_path')
        run_argument = self.require_arg('run_argument')
        self.warm_up()
        for i in test_range:
            append_args = '-Xloggc:log/{0}_{1}.log -XX:{0}={1} -Xmx{2} -Xms{2} '.format(test_argument, i, heap_size)
            cmd = 'java ' + self.default_args + append_args + '-jar {0} {1} >NUL 2>&1'.format(jar_path, run_argument)
            self.output_str('生成日志到log/{0}_{1}.log'.format(test_argument, i))
            st = time.time()
            os.system(cmd)
            et = time.time()

    def show(self):
        test_range = self.require_arg('test_range')
        test_range = self.parse_test_range(test_range)
        if test_range == None:
            return
        test_argument = self.require_arg('test_argument')
        for i in test_range:
            cmd = 'java -jar LogReader.jar show log/{0}_{1}.log &'.format(test_argument, i)
            subprocess.Popen(cmd,shell=True)
            time.sleep(0.2)

    def execution_time(self):
        test_range = self.require_arg('test_range')
        test_range = self.parse_test_range(test_range)
        if test_range == None:
            return
        test_argument = self.require_arg('test_argument')
        heap_size = self.require_arg('heap_size')
        jar_path = self.require_arg('jar_path')
        run_argument = self.require_arg('run_argument')
        self.warm_up()
        min_time = -1
        min_index = -1
        for i in test_range:
            cmd = 'java -Xmx{0} -Xms{0} -XX:{1}={2} -jar {3} {4} >NUL 2>&1'.format(heap_size, test_argument, i, jar_path, run_argument)
            sum = 0
            for j in range(0, 3):
                st = time.time()
                os.system(cmd)
                et = time.time()
                sum += (et - st)
            sum /= 3
            if min_time == -1 or min_time > sum:
                min_time = sum
                min_index = i
            self.output_str('参数值%d: %.2fs'%(i, sum))
        self.output_str('最少执行时间: %.2fs, 参数值: %d'%(min_time, min_index))

if __name__ == '__main__':
    optimizer = Optimizer()
    optimizer.main()
