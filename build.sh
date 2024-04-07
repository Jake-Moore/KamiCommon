#!/bin/bash

echo "./gradlew clean"
./gradlew clean || exit 1

echo "./gradlew :standalone-utils:build"
./gradlew :standalone-utils:build || exit 1

echo "./gradlew :standalone-jar:build"
./gradlew :standalone-jar:build || exit 1

echo "./gradlew :generic-jar:build"
./gradlew :generic-jar:build || exit 1

echo "./gradlew :spigot-nms:api:build"
./gradlew :spigot-nms:api:build || exit 1

echo "./gradlew :spigot-nms:build"
./gradlew :spigot-nms:build || exit 1

echo "./gradlew :spigot-utils:build"
./gradlew :spigot-utils:build || exit 1

echo "./gradlew :spigot-jar:build"
./gradlew :spigot-jar:build || exit 1