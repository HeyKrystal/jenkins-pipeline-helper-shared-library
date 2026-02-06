set -eu

# Create dockerized environment to run validation
docker run --rm \
-u "$(id -u):$(id -g)" \
-e HOME=/tmp \
-e PIP_CACHE_DIR=/tmp/pip-cache \
-e PATH="/tmp/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin" \
-v "$PWD:/work" -w /work \
'__DOCKER_IMAGE__' \
sh /work/resources/sh/python/validation_docker_exec.sh