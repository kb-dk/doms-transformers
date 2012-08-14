#!/bin/bash

cd $(dirname $(readlink -f $0))

CLASSPATH="-classpath ..conf:../resources:../lib/*"
MAINCLASS="dk.statsbiblioteket.doms.transformers.fileenricher.FileEnricher"
java $CLASSPATH $MAINCLASS $*

