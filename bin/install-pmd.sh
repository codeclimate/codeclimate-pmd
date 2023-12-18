#!/usr/bin/env bash
set -euo pipefail

LIB_DIR=/usr/src/app/lib

download_pmd() {
  URL="https://github.com/pmd/pmd/releases/download/pmd_releases/7.0.0-rc4/pmd-dist-7.0.0-rc4-bin.zip"
  wget -O pmd.zip $URL
}

install_pmd() {
  unzip pmd.zip
  mv ${LIB_DIR}/pmd-bin*/* ${LIB_DIR}/pmd
}

cleanup() {
  rmdir ${LIB_DIR}/pmd-bin*
  rm pmd.zip
}

mkdir -p ${LIB_DIR}/pmd
cd ${LIB_DIR}

download_pmd
install_pmd
cleanup
