#!/bin/bash

echo "./gradlew :standalone-utils:build"
./gradlew :standalone-utils:build || exit 1

echo "./gradlew :generic-jar:build"
./gradlew :generic-jar:build || exit 1

echo "./gradlew :spigot-nms:api:build"
./gradlew :spigot-nms:api:build || exit 1

echo "./gradlew :spigot-nms:build"
./gradlew :spigot-nms:build || exit 1

echo "./gradlew :spigot-utils:build"
./gradlew :spigot-utils:build || exit 1