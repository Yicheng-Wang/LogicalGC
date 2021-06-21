import os
import re
import time
import threading
import subprocess

import tkinter as tk
from tkinter import Button, Label, Entry, StringVar, Text, Scrollbar
from tkinter.constants import DISABLED, NORMAL, END, INSERT, RIGHT, Y
from tkinter.filedialog import askopenfilename

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

    def main(self):
        def generate_LogReader():
            if os.path.exists('./LogReader.jar') == False:
                os.system('make jar')

        generate_LogReader()

        window = tk.Tk()
        window.title('Optimizer')
        width = 800
        height = 300
        window.minsize(width, height)
        window.maxsize(width, height)

        def choose_file():
            jar_path.set(askopenfilename(filetypes=[('jar文件', '*.jar')]))

        jar_path = StringVar()
        Label(window, text='jar文件', font=('Arial', 12)).place(x=0, y=5, width=150, height=30)
        Entry(window, textvariable=jar_path, font=('Arial', 12)).place(x=150, y=5, width=550, height=30)
        Button(window, text='选择', font=('Arial', 12), width=10, command=choose_file).place(x=700, y=5, width=100, height=30)

        run_argument = StringVar()
        heap_size = StringVar()
        test_argument = StringVar()
        test_range = StringVar(value='start:end:step')
        Label(window, text='jar参数', font=('Arial', 12)).place(x=0, y=40, width=150, height=30)
        Entry(window, textvariable=run_argument, font=('Arial', 12)).place(x=150, y=40, width=250, height=30)
        Label(window, text='堆大小', font=('Arial', 12)).place(x=400, y=40, width=150, height=30)
        Entry(window, textvariable=heap_size, font=('Arial', 12)).place(x=550, y=40, width=250, height=30)
        Label(window, text='测试参数', font=('Arial', 12)).place(x=0, y=75, width=150, height=30)
        Entry(window, textvariable=test_argument, font=('Arial', 12)).place(x=150, y=75, width=250, height=30)
        Label(window, text='测试范围', font=('Arial', 12)).place(x=400, y=75, width=150, height=30)
        Entry(window, textvariable=test_range, font=('Arial', 12)).place(x=550, y=75, width=250, height=30)

        def set_for_test():
            jar_path.set('D:/share/MM/LogicalGC/tool/dacapo-9.12-MR1-bach.jar')
            run_argument.set('h2 -s small')
            heap_size.set('75M')
            test_argument.set('SurvivorRatio')
            test_range.set('2:16:2')

        set_for_test()

        def parse_test_range():
            test_range_str = test_range.get()
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

        def warm_up(thread):
            # execute warm-up first to stabilize the caches
            cmd = 'java -Xmx{0} -Xms{0} -jar {1} {2} >NUL 2>&1'.format(heap_size.get(), jar_path.get(), run_argument.get())
            self.output_str('执行预热\n')
            os.system(cmd)
            thread.start()

        def buttun_optimize():
            self.clear_output()
            optimize_thread = threading.Thread(target=self.optimize, args=(heap_size.get(), jar_path.get(), run_argument.get()))
            threading.Thread(target=warm_up, args=(optimize_thread,)).start()

        def buttun_generate():
            parsed_test_range = parse_test_range()
            if parsed_test_range != None:
                self.clear_output()
                generate_thread = threading.Thread(target=self.generate, args=(parsed_test_range, test_argument.get(), heap_size.get(), jar_path.get(), run_argument.get()))
                threading.Thread(target=warm_up, args=(generate_thread,)).start()

        def buttun_show():
            parsed_test_range = parse_test_range()
            if parsed_test_range != None:
                self.show(parsed_test_range, test_argument.get())

        def buttun_time():
            parsed_test_range = parse_test_range()
            if parsed_test_range != None:
                self.clear_output()
                time_thread = threading.Thread(target=self.execution_time, args=(heap_size.get(), test_argument.get(), parsed_test_range, jar_path.get(), run_argument.get(), True))
                threading.Thread(target=warm_up, args=(time_thread,)).start()

        Button(window, text='优化参数', font=('Arial', 12), width=10, command=buttun_optimize).place(x=80, y=110, width=100, height=30)
        Button(window, text='生成日志', font=('Arial', 12), width=10, command=buttun_generate).place(x=260, y=110, width=100, height=30)
        Button(window, text='展示日志', font=('Arial', 12), width=10, command=buttun_show).place(x=440, y=110, width=100, height=30)
        Button(window, text='执行时间', font=('Arial', 12), width=10, command=buttun_time).place(x=620, y=110, width=100, height=30)

        self.output = Text(window, state=DISABLED, font=('Arial', 12))
        self.output.place(x=5, y=145, width=790, height=150)
        bar = Scrollbar(self.output, command=self.output.yview)
        bar.pack(side=RIGHT, fill=Y)
        self.output.config(yscrollcommand=bar.set)

        window.mainloop()

    def output_str(self, str):
        self.output.config(state=NORMAL)
        self.output.insert(INSERT, str)
        self.output.config(state=DISABLED)

    def clear_output(self):
        self.output.config(state=NORMAL)
        self.output.delete(1.0, END)
        self.output.config(state=DISABLED)

    def optimize(self, heap_size, jar_path, run_argument):
        append_args = '-Xloggc:log/{0}_default.log -Xmx{0} -Xms{0} '.format(heap_size)
        cmd = 'java ' + self.default_args + append_args + '-jar {0} {1} >NUL 2>&1'.format(jar_path, run_argument)
        self.output_str('生成日志到log/{0}_default.log\n'.format(heap_size))
        os.system(cmd)

        self.output_str('解析日志log/{0}_default.log\n'.format(heap_size))
        cmd = 'java -jar LogReader.jar analyze log/{0}_default.log'.format(heap_size)
        p = subprocess.Popen(cmd, stdout=subprocess.PIPE)
        p.wait()
        out1 = str(p.stdout.read())[2:-5].split('|')

        cmd = 'java -jar LogReader.jar optimize log/{0}_default.log'.format(heap_size)
        p = subprocess.Popen(cmd, stdout=subprocess.PIPE)
        p.wait()
        line = str(p.stdout.readline())
        NewRatio_line = line[2:-3]
        print(NewRatio_line)
        NewRatio = int(NewRatio_line[NewRatio_line.rfind(' ')+1:])
        line = str(p.stdout.readline())
        SurvivorRatio_line = line[2:-3]
        print(SurvivorRatio_line)
        SurvivorRatio = int(SurvivorRatio_line[SurvivorRatio_line.rfind(' ')+1:])
        line = str(p.stdout.readline())
        TargetSurvivorRatio_line = line[2:-3]
        print(TargetSurvivorRatio_line)
        TargetSurvivorRatio = int(TargetSurvivorRatio_line[TargetSurvivorRatio_line.rfind(' ')+1:])
        self.output_str('建议的NewRatio：%d\n'%(NewRatio))
        self.output_str('建议的SurvivorRatio：%d\n'%(SurvivorRatio))
        self.output_str('建议的TargetSurvivorRatio：%d\n'%(TargetSurvivorRatio))

        # fix: NewRatio
        append_args = '-Xloggc:log/{0}_opt.log -Xmx{0} -Xms{0} -XX:NewRatio={1} '.format(heap_size, NewRatio)
        cmd = 'java ' + self.default_args + append_args + '-jar {0} {1} >NUL 2>&1'.format(jar_path, run_argument)
        self.output_str('生成日志到log/{0}_opt.log\n'.format(heap_size))
        os.system(cmd)

        self.output_str('解析日志log/{0}_opt.log\n'.format(heap_size))
        cmd = 'java -jar LogReader.jar analyze log/{0}_opt.log'.format(heap_size)
        p = subprocess.Popen(cmd, stdout=subprocess.PIPE)
        p.wait()
        out2 = str(p.stdout.read())[2:-5].split('|')

        run_time = (float(out1[0]) - float(out2[0]))/float(out1[0])*100
        throught_rate = float(out2[1]) - float(out1[1])
        gc_time = (float(out1[2]) - float(out2[2]))/float(out1[2])*100
        self.output_str('执行时间减少：%.2f%%\n'%(run_time))
        self.output_str('吞吐率提升：%.2f%%\n'%(throught_rate))
        self.output_str('GC时间减少：%.2f%%\n' % (gc_time))
        print(out1, out2)

    def generate(self, test_range, test_argument, heap_size, jar_path, run_argument):
        for i in test_range:
            append_args = '-Xloggc:log/{0}_{1}.log -XX:{0}={1} -Xmx{2} -Xms{2} '.format(test_argument, i, heap_size)
            cmd = 'java ' + self.default_args + append_args + '-jar {0} {1} >NUL 2>&1'.format(jar_path, run_argument)
            self.output_str('生成日志到log/{0}_{1}.log\n'.format(test_argument, i))
            st = time.time()
            os.system(cmd)
            et = time.time()
            print(i, ':', et - st)

    def show(self, test_range, test_argument):
        for i in test_range:
            cmd = 'java -jar LogReader.jar show log/{0}_{1}.log &'.format(test_argument, i)
            subprocess.Popen(cmd)
            time.sleep(0.2)

    def execution_time(self, heap_size, test_argument, test_range, jar_path, run_argument, output):
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
            if output:
                self.output_str('参数值%d: %.2fs\n'%(i, sum))
        if output:
            self.output_str('最少执行时间: %.2fs, 参数值: %d\n'%(min_time, min_index))
        return min_time, min_index

if __name__ == '__main__':
    optimizer = Optimizer()
    optimizer.main()
