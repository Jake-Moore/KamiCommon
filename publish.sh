#!/bin/bash

echo "./gradlew clean"
./gradlew clean || exit 1

echo "./gradlew :standalone-utils:publish"
./gradlew :standalone-utils:publish || exit 1

echo "./gradlew :standalone-jar:publish"
./gradlew :standalone-jar:publish || exit 1

echo "./gradlew :generic-jar:publish"
./gradlew :generic-jar:publish || exit 1

echo "./gradlew :spigot-nms:api:publish"
./gradlew :spigot-nms:api:publish || exit 1

echo "./gradlew :spigot-nms:publish"
./gradlew :spigot-nms:publish || exit 1

echo "./gradlew :spigot-utils:publish"
./gradlew :spigot-utils:publish || exit 1

echo "./gradlew :spigot-jar:publish"
./gradlew :spigot-jar:publish || exit 1