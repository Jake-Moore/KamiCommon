#!/bin/bash

echo "./gradlew clean"
./gradlew clean || exit 1

echo "./gradlew :standalone-utils:publish"
./gradlew :standalone-utils:publish || exit 1

echo "./gradlew :generic-jar:publish"
./gradlew :generic-jar:publish || exit 1

echo "./gradlew :standalone-jar:publish"
./gradlew :standalone-jar:publish || exit 1

# Build these 3 for spigot-utils to have the latest changes
echo "./gradlew :spigot-nms:api:build"
./gradlew :spigot-nms:api:build || exit 1

echo "./gradlew :spigot-nms:build"
./gradlew :spigot-nms:build || exit 1

echo "./gradlew :spigot-utils:publish"
./gradlew :spigot-utils:publish || exit 1

echo "./gradlew :spigot-jar:publish"
./gradlew :spigot-jar:publish || exit 1