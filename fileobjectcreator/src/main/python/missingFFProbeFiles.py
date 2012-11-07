#!/bin/env python

import os
import sys

"""
Input: lines formatted as "checksum filesize filename", and a directory containing ffprobe data.

This tool is useful for two things:

1) Filter away files with missing ffprobe data, and produce output with the same format as the input:
cat data | fileobjectcreator/src/main/python/missingFFProbeFiles.py ~/tmp/ffprobe.result complete

2) Create a list of files that lacks ffprobe data:
cat data | fileobjectcreator/src/main/python/missingFFProbeFiles.py ~/tmp/ffprobe.result incomplete
"""

showModes = ["all", "complete", "incomplete"]
showMode = "all"

def printUsage():
    print "Usage: %s path-to-ffprobe-data [%s]" % (sys.argv[0], "|".join(showModes))

if len(sys.argv) < 2:
    printUsage()
    sys.exit(1)

if len(sys.argv) >= 3:
    mode = sys.argv[2]
    if mode in showModes:
        showMode = mode
    else:
        printUsage()
        sys.exit(2)

ffProbeDir = sys.argv[1]

for line in sys.stdin.readlines():
    line = line.strip()

    if not line.endswith(".log") and not "_digivid_" in line:
        (checksum, size, filename) = line.split(" ")
        stdoutPath = os.path.join(ffProbeDir, filename) + ".stdout"
        stderrPath = os.path.join(ffProbeDir, filename) + ".stderr"

        hasStdout = os.path.exists(stdoutPath) and os.path.isfile(stdoutPath)
        hasStderr = os.path.exists(stderrPath) and os.path.isfile(stderrPath)

        completeFFProbe = hasStdout and hasStderr
        if showMode == "all":
            if completeFFProbe:
                print "complete:" + filename
            else:
                print "incomplete:" + filename

        elif showMode == "complete":
            if completeFFProbe:
                print line

        elif showMode == "incomplete":
            if not completeFFProbe:
                print filename


