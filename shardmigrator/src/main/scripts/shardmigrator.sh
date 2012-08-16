#!/bin/sh

cd $(dirname $(readlink -f $0))

CLASSPATH="-classpath ..conf:../resources:../lib/*"
MAINCLASS="dk.statsbiblioteket.doms.transformers.shardmigrator.ShardMigrator"
java $CLASSPATH $MAINCLASS $*

