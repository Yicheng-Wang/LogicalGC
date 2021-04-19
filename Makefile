clean:
	@rm -rf out scratch LogReader.jar

javac: src/*.java
	@mkdir -p out/classes
	javac -encoding utf8 -d out/classes/ -sourcepath src/ src/LogReader.java

jar: javac
	jar -cfe LogReader.jar LogReader -C out/classes/ .

.PHONY: log
log:
	@python log.py generate large

show:
	@python log.py show

run_decapo: lib/dacapo-9.12-MR1-bach.jar
	java -jar lib/dacapo-9.12-MR1-bach.jar h2 -s small
