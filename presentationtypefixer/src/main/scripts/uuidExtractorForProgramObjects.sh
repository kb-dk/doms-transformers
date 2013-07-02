#!/bin/sh

if [ $# -ne 1 ]
then
  echo "Usage: $0 config-file"
  exit 1
fi

CONFIGFILE="$1"
WEBHOST=$(grep domsurl "$CONFIGFILE" | cut -d "=" -f2 | cut -d "/" -f1,2,3 | sed -e "s/ //g")
USER=$(grep domsuser "$CONFIGFILE" | cut -d "=" -f2 | sed -e "s/ //g")
PASS=$(grep domspass "$CONFIGFILE" | cut -d "=" -f2 | sed -e "s/ //g")

curl --user $USER:$PASS "$WEBHOST/fedora/risearch?type=tuples&lang=sparql&format=CSV&limit=&stream=on&query=SELECT+%3Fobject+%3Fdate+WHERE+%7B%0D%0A++%3Fobject+%3Cinfo%3Afedora%2Ffedora-system%3Adef%2Fmodel%23hasModel%3E+%3Cinfo%3Afedora%2Fdoms%3AContentModel_Program%3E%3B%0D%0A++++++++++%3Cinfo%3Afedora%2Ffedora-system%3Adef%2Fmodel%23createdDate%3E+%3Fdate+%3B%0D%0A++++++++++%3Chttp%3A%2F%2Fdoms.statsbiblioteket.dk%2Frelations%2Fdefault%2F0%2F1%2F%23isPartOfCollection%3E+%3Cinfo%3Afedora%2Fdoms%3ARadioTV_Collection%3E+%3B%0D%0A++++++++++%3Cinfo%3Afedora%2Ffedora-system%3Adef%2Fmodel%23state%3E+%3Cinfo%3Afedora%2Ffedora-system%3Adef%2Fmodel%23Active%3E+.%0D%0A++FILTER+%28%0D%0A++++%3Fdate+%3E%3D+%272012-10-31T00%3A00%3A00.000Z%27%5E%5Exsd%3AdateTime%0D%0A++%29%0D%0A%7D+ORDER+BY+%3Fdate" | tail -n+2 | cut -d, -f1 | cut -d "/" -f2
