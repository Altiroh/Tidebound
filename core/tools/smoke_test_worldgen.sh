#!/usr/bin/env bash
set -euo pipefail

run_dir="${PWD}/run"
log_file="${run_dir}/logs/latest.log"
console_log="${PWD}/build/worldgen-smoke.log"

mkdir -p "${run_dir}" "${PWD}/build"
printf 'eula=true\n' > "${run_dir}/eula.txt"
rm -rf "${run_dir}/world" "${run_dir}/world_nether" "${run_dir}/world_the_end"

setsid ./gradlew runServer --no-daemon >"${console_log}" 2>&1 &
server_pid=$!

cleanup() {
    if kill -0 "${server_pid}" 2>/dev/null; then
        kill -- "-${server_pid}" 2>/dev/null || true
        wait "${server_pid}" 2>/dev/null || true
    fi
}
trap cleanup EXIT

for _ in $(seq 1 180); do
    if [[ -f "${log_file}" ]] && grep -Fq 'Done (' "${log_file}"; then
        echo "Tidebound worldgen smoke test: server reached ready state"
        exit 0
    fi
    if ! kill -0 "${server_pid}" 2>/dev/null; then
        echo "Tidebound worldgen smoke test: server stopped before ready state" >&2
        tail -n 200 "${console_log}" >&2 || true
        exit 1
    fi
    sleep 1
done

echo "Tidebound worldgen smoke test: timed out" >&2
tail -n 200 "${console_log}" >&2 || true
exit 1
