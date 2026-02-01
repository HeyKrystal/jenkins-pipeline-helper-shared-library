set -eux

# Run validation inside a fresh container on the agent
docker run --rm \
-u "$(id -u):$(id -g)" \
-e HOME=/tmp \
-e PIP_CACHE_DIR=/tmp/pip-cache \
-e PATH="/tmp/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin" \
-v "$PWD:/work" -w /work \
__IMAGE__ \
sh -lc '
    set -eux

    # If an old root-owned cache exists, avoid using it at all.
    # Deleting may fail if it is root-owned; ignore that.
    rm -rf .ruff_cache || true

    python --version
    python -m pip install --upgrade pip

    # Install project + dev deps (if present)
    if [ -f requirements.txt ]; then
    python -m pip install -r requirements.txt
    fi

    if [ -f requirements-dev.txt ]; then
    python -m pip install -r requirements-dev.txt
    fi

    # Lint (ruff) - fail build if lint fails
    python -m ruff --version >/dev/null 2>&1 && python -m ruff check . --no-cache

    # Tests (pytest) - run only if tests exist
    if find . -type f \( -name "test_*.py" -o -name "*_test.py" \) -print -quit 2>/dev/null | grep -q .; then
    python -m pytest -q --disable-warnings --maxfail=1
    else
    echo "No tests found; skipping pytest."
    fi

    # Extra sanity: ensure files compile
    python -m compileall -q .
'