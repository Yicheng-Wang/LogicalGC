```bash
# 需要配置的环境：jdk8、python3
make jar # 生成LogReader.jar
java -jar LogReader.jar show log_file # 调用LogReader展示log信息
python3 tool.py # 打开python脚本
# 参数示例(tool.ini)：
#     jar路径(jar_path)：D:/share/MM/LogicalGC/tool/dacapo-9.12-MR1-bach.jar
#     jar参数(run_argument)：h2 -s small
#     堆大小(heap_size)：75M
#     测试参数(test_argument)：SurvivorRatio
#     测试范围(test_range)：4:16:4 # 会被转换成range(4, 16, 4)
# 脚本功能：
#     优化参数(opt)：根据规则和搜索对参数进行优化
#     生成日志(gen)：根据测试参数及其范围生成一系列运行时日志信息
#     展示日志(show)：调用LogReader展示由生成日志所生成的日志信息
#     执行时间(time)：根据测试参数及其范围测试程序执行时间
#     展示效果(demo)：输出实际测试中得到的信息
#     重载参数(reload)：重新加载ini文件内的参数
```
