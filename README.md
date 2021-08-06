```bash
# 需要配置的环境：jdk8、python3
make jar # 生成LogReader.jar
java -jar LogReader.jar show log_file # 调用LogReader展示log信息
python tool.py # 打开GUI
# 输入框示例：
#     jar路径：D:/share/MM/LogicalGC/tool/dacapo-9.12-MR1-bach.jar
#     jar参数：h2 -s small
#     堆大小：75M
#     测试参数：SurvivorRatio
#     测试范围：4:16:4 # 会被转换成range(4, 16, 4)
# 按钮功能：
#     优化参数：根据规则和搜索对参数进行优化
#     生成日志：根据测试参数及其范围生成一系列运行时日志信息
#     展示日志：调用LogReader展示由生成日志所生成的日志信息
#     执行时间：根据测试参数及其范围测试程序执行时间
#     展示效果：输出实际测试中得到的信息
```
