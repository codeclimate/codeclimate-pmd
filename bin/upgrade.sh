#!/bin/sh

URL=$(curl -s https://api.github.com/repos/pmd/pmd/releases/latest | jq -c '.assets[] | select(.name | contains("bin")) | .browser_download_url')
ESCAPED_URL=$(echo $URL | sed 's/\&/\\&/g')
sed -i -E "s#URL=.*#URL=${ESCAPED_URL}#" bin/install-pmd.sh

echo $ESCAPED_URL
