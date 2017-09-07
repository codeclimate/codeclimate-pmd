#!/usr/bin/env bash
set -euo pipefail

download_latest_pmd() {
  curl -s https://api.github.com/repos/pmd/pmd/releases/latest | \
    jq -c '.assets[] | select(.name | contains("bin")) | .browser_download_url' | \
    xargs wget -O pmd.zip
}

cd /opt
download_latest_pmd
unzip pmd.zip
mkdir /opt/pmd
mv /opt/pmd-bin*/* /opt/pmd
rmdir /opt/pmd-bin*
rm pmd.zip
