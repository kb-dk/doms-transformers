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

curl --user $USER:$PASS "$WEBHOST/fedora/risearch?type=tuples&lang=sparql&format=CSV&limit=&stream=on&query=SELECT%20%3Fobject%20WHERE%20%7B%0A%20%20%3Fobject%20%3Cinfo%3Afedora%2Ffedora-system%3Adef%2Fmodel%23hasModel%3E%20%3Cinfo%3Afedora%2Fdoms%3AContentModel_VHSFile%3E%3B%0A%20%20%20%20%20%20%20%20%20%20%3Cinfo%3Afedora%2Ffedora-system%3Adef%2Fmodel%23state%3E%20%3Cinfo%3Afedora%2Ffedora-system%3Adef%2Fmodel%23Active%3E%20.%0A%7D" | tail -n+2 | cut -d "/" -f2
