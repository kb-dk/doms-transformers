#!/bin/sh
java -classpath $(dirname $0)/../lib/* dk.statsbiblioteket.doms.transformers.shardremover.ShardRemover $*