# Check if GITHUB_REPOSITORY is present to change the root command
root_command="gradle"
if [ -z "$GITHUB_REPOSITORY" ]; then
  root_command="./gradlew"

  # Default value is true
  bClean=true
  # Check if an argument is provided
  if [ $# -gt 0 ]; then
      # Check if the argument is "false"
      if [ "$1" = "false" ]; then
          bClean=false
      fi
  fi

  if [ "$bClean" = true ]; then
    echo "$root_command clean"
    "$root_command" clean || exit 1
  fi
fi

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