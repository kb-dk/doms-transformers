#!/bin/bash

CLASSPATH="-classpath conf:resources:lib/*"
MAINCLASS="dk.statsbiblioteket.doms.transformers.fileenricher.FileEnricher"
java $CLASSPATH $MAINCLASS $*
