#!/bin/bash

cd $(dirname $(readlink -f $0))
CONFIGFILE="../conf/fileenricher.properties"
OUTPUTFILE="../resources/$1"
OUTPUTFILE="$1"
WEBHOST=$(cat $CONFIGFILE | grep domsurl | cut -d "=" -f2 | cut -d "/" -f1,2,3)
USER=$(cat $CONFIGFILE | grep domsuser | cut -d "=" -f2)
PASS=$(cat $CONFIGFILE | grep domspass | cut -d "=" -f2)

curl --user $USER:$PASS "$WEBHOST/fedora/risearch?query=select%20%24x%20%0Afrom%20%3C%23ri%3E%0Awhere%20%24x%20%3Cinfo%3Afedora%2Ffedora-system%3Adef%2Fmodel%23hasModel%3E%20%3Cinfo%3Afedora%2Fdoms%3AContentModel_RadioTVFile%3E%20minus%20%24x%20%3Cinfo%3Afedora%2Ffedora-system%3Adef%2Fmodel%23state%3E%20%3Cinfo%3Afedora%2Ffedora-system%3Adef%2Fmodel%23Deleted%3E%20minus%20%24x%20%3Chttp%3A%2F%2Fecm.sourceforge.net%2Frelations%2F0%2F2%2F%23isTemplateFor%3E%20%24y&lang=itql&format=csv&limit=0" | tail -n+2 | cut -d "/" -f2 > $OUTPUTFILE



