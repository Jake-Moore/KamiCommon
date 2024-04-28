# Check if GITHUB_REPOSITORY is present to change the root command
root_command="gradle"
if [ -z "$GITHUB_REPOSITORY" ]; then
  root_command="./gradlew"

  echo "$root_command clean"
  "$root_command" clean || exit 1
fi

echo "$root_command :standalone-utils:publish"
"$root_command" :standalone-utils:publish || exit 1

echo "$root_command :generic-jar:publish"
"$root_command" :generic-jar:publish || exit 1

echo "$root_command :standalone-jar:publish"
"$root_command" :standalone-jar:publish || exit 1

echo "$root_command :spigot-nms:api:build"
"$root_command" :spigot-nms:api:build || exit 1

echo "$root_command :spigot-nms:build"
"$root_command" :spigot-nms:build || exit 1

echo "$root_command :spigot-utils:publish"
"$root_command" :spigot-utils:publish || exit 1

echo "$root_command :spigot-jar:publish"
"$root_command" :spigot-jar:publish || exit 1