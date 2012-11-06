#!/bin/sh

BASE_DIR=$(dirname $(dirname $(readlink -f $0 ) ) )

CLASSPATH="-classpath $BASE_DIR/conf:$BASE_DIR/resources:$BASE_DIR/lib/*"
MAINCLASS="dk.statsbiblioteket.doms.transformers.fileenricher.FileEnricher"
java $CLASSPATH $MAINCLASS $*
