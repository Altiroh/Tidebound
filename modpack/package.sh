#!/usr/bin/env bash
set -euo pipefail

script_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
repo_root="$(cd "$script_dir/.." && pwd)"
version="$(sed -n 's/^mod_version=//p' "$repo_root/core/gradle.properties")"
jar_path="$repo_root/core/build/libs/tidebound-$version.jar"
output_dir="$repo_root/dist"
output_path="$output_dir/Tidebound_Devpack_$version.zip"

if [[ ! -f "$jar_path" ]]; then
    echo "JAR introuvable : $jar_path" >&2
    echo "Exécutez d'abord : cd core && ./gradlew build" >&2
    exit 1
fi

staging_dir="$(mktemp -d)"
trap 'rm -rf "$staging_dir"' EXIT

cp -R "$script_dir/." "$staging_dir/"
rm "$staging_dir/package.sh"
find "$staging_dir" -name .gitkeep -delete
mkdir -p "$staging_dir/overrides/mods" "$output_dir"
cp "$jar_path" "$staging_dir/overrides/mods/"
rm -f "$output_path"

(
    cd "$staging_dir"
    zip -qr "$output_path" .
)

echo "$output_path"
