#!/bin/bash

# Check if a command is provided
if [ $# -eq 0 ]; then
    echo "Usage: $0 <command>"
    exit 1
fi
root_command="$1"

echo "$root_command clean"
"$root_command" clean || exit 1

echo "$root_command :standalone-utils:build"
"$root_command" :standalone-utils:build || exit 1

echo "$root_command :generic-jar:build"
"$root_command" :generic-jar:build || exit 1

echo "$root_command :standalone-jar:build"
"$root_command" :standalone-jar:build || exit 1

echo "$root_command :spigot-nms:api:build"
"$root_command" :spigot-nms:api:build || exit 1

echo "$root_command :spigot-nms:build"
"$root_command" :spigot-nms:build || exit 1

echo "$root_command :spigot-utils:build"
"$root_command" :spigot-utils:build || exit 1

echo "$root_command :spigot-jar:build"
"$root_command" :spigot-jar:build || exit 1