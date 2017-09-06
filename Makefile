all: build
.PHONY: all build

build:
	./prefixator.groovy | tee output.txt
	cat jobs-after.txt | sort | uniq -c | grep -v '^ *1 ' | tee dups.txt
