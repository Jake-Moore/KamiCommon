echo "VERSION=$(awk -F'"' '/var VERSION =/{print $2}' build.gradle.kts)"
