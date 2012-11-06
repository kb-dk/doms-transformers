#!/bin/bash

CWD=$(dirname $0)
pushd $CWD > /dev/null
popd > /dev/null

CLASSPATH="-classpath conf:resources:lib/*"
MAINCLASS="dk.statsbiblioteket.doms.transformers.fileobjectcreator.FileObjectCreator"
java $CLASSPATH $MAINCLASS $*

