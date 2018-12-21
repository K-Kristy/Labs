#!/bin/bash

function pause() {
    read -n1 -r -p "Press any key to continue..." key
}

java -Dfile.encoding=UTF8 -cp artifacts/*:lib/*:out info.kgeorgiy.java.advanced.walk.Tester RecursiveWalk ru.ifmo.ctddev.kuplkri.walk.RecursiveWalk
STATUS="${?}"
if [ "${STATUS}" != "0" ]; then
    exit "${STATUS}"
fi
if [ "${1}" != "" ]; then
    pause
fi

java -Dfile.encoding=UTF8 -cp artifacts/*:lib/*:out info.kgeorgiy.java.advanced.walk.Tester RecursiveWalk ru.ifmo.ctddev.kuplkri.walk.Walk info.kgeorgiy.java.advanced.arrayset.Tester NavigableSet ru.ifmo.ctddev.kuplkri.arrayset.ArraySet
STATUS="${?}"
if [ "${STATUS}" != "0" ]; then
    exit "${STATUS}"
fi
if [ "${1}" != "" ]; then
    pause
fi
