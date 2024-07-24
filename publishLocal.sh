# Check if GITHUB_REPOSITORY is present to change the root command
root_command="gradle"
if [ -z "$GITHUB_REPOSITORY" ]; then
  root_command="./gradlew"

  echo "$root_command clean"
  "$root_command" clean || exit 1
fi

echo "$root_command :standalone-utils:build"
"$root_command" :standalone-utils:build || exit 1
echo "$root_command :standalone-utils:publishToMavenLocal"
"$root_command" :standalone-utils:publishToMavenLocal || exit 1

echo "$root_command :generic-jar:build"
"$root_command" :generic-jar:build || exit 1
echo "$root_command :generic-jar:publishToMavenLocal"
"$root_command" :generic-jar:publishToMavenLocal || exit 1

echo "$root_command :standalone-jar:build"
"$root_command" :standalone-jar:build || exit 1
echo "$root_command :standalone-jar:publishToMavenLocal"
"$root_command" :standalone-jar:publishToMavenLocal || exit 1

echo "$root_command :spigot-nms:api:build"
"$root_command" :spigot-nms:api:build || exit 1

echo "$root_command :spigot-nms:build"
"$root_command" :spigot-nms:build || exit 1

echo "$root_command :spigot-utils:build"
"$root_command" :spigot-utils:build || exit 1
echo "$root_command :spigot-utils:publishToMavenLocal"
"$root_command" :spigot-utils:publishToMavenLocal || exit 1

echo "$root_command :spigot-jar:build"
"$root_command" :spigot-jar:build || exit 1
echo "$root_command :spigot-jar:publishToMavenLocal"
"$root_command" :spigot-jar:publishToMavenLocal || exit 1