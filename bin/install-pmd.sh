#!/usr/bin/env bash
set -euo pipefail

LIB_DIR=/usr/src/app/lib

download_latest_pmd() {
  curl -s https://api.github.com/repos/pmd/pmd/releases/latest | \
    jq -c '.assets[] | select(.name | contains("bin")) | .browser_download_url' | \
    xargs wget -O pmd.zip
}

install_pmd() {
  download_latest_pmd
  unzip pmd.zip
  mv ${LIB_DIR}/pmd-bin*/* ${LIB_DIR}/pmd
}

cleanup() {
  rmdir ${LIB_DIR}/pmd-bin*
  rm pmd.zip
}

mkdir -p ${LIB_DIR}/pmd
cd ${LIB_DIR}

install_pmd
cleanup
