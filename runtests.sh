#!/bin/bash

function pause() {
    read -n1 -r -p "Press any key to continue..." key
}

java -Dfile.encoding=UTF8 -cp artifacts/*:lib/*:out info.kgeorgiy.java.advanced.walk.Tester RecursiveWalk ru.ifmo.ctddev.kuplkri.walk.Walk info.kgeorgiy.java.advanced.arrayset.Tester NavigableSet ru.ifmo.ctddev.kuplkri.arrayset.ArraySet
if [ "${1}" != "" ]; then
    pause
fi


NavigableSetTest