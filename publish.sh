# Check if GITHUB_REPOSITORY is present to change the root command
root_command="./gradlew"
if [ -n "$GITHUB_REPOSITORY" ]; then
  root_command="gradle"
fi

echo "$root_command clean"
"$root_command" clean || exit 1

echo "$root_command :standalone-utils:publish"
"$root_command" :standalone-utils:publish || exit 1

echo "$root_command :generic-jar:publish"
"$root_command" :generic-jar:publish || exit 1

echo "$root_command :standalone-jar:publish"
"$root_command" :standalone-jar:publish || exit 1

# Build these 3 for spigot-utils to have the latest changes
echo "$root_command :spigot-nms:api:build"
"$root_command" :spigot-nms:api:build || exit 1

echo "$root_command :spigot-nms:build"
"$root_command" :spigot-nms:build || exit 1

echo "$root_command :spigot-utils:publish"
"$root_command" :spigot-utils:publish || exit 1

echo "$root_command :spigot-jar:publish"
"$root_command" :spigot-jar:publish || exit 1