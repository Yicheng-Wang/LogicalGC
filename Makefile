clean:
	@rm -rf out scratch LogReader.jar

clean_log:
	@rm -rf log/*.log

javac:
	@mkdir -p out/classes
	javac -encoding utf8 -d out/classes/ -sourcepath src/ src/LogReader.java

jar: javac
	jar -cfe LogReader.jar LogReader -C out/classes/ .

run_decapo: tool/dacapo-9.12-MR1-bach.jar
	java -Xms9M -Xmx9M -jar tool/dacapo-9.12-MR1-bach.jar fop -s small

final_flags:
	java -XX:+UnlockExperimentalVMOptions -XX:+UnlockDiagnosticVMOptions -XX:+PrintFlagsFinal -version > log/final_flags.txt
