#!/bin/bash

function pause() {
    read -n1 -r -p "Press any key to continue..." key
}

java -Dfile.encoding=UTF8 -cp artifacts/*:lib/*:out info.kgeorgiy.java.advanced.walk.Tester RecursiveWalk ru.ifmo.ctddev.kuplkri.walk.RecursiveWalk
STATUS="${?}"
if [ "${1}" != "" ]; then
    pause
fi
exit "${STATUS}"