#!/usr/bin/env bash

set -euxo pipefail

main() {
  git fetch --prune origin "+refs/tags/*:refs/tags/*"
  OLD_VERSION=$(git describe --match "*.*.*" --abbrev=0 --tags $(git rev-list --tags --max-count=1))
  NEW_VERSION=$(bump2version --current-version $OLD_VERSION --list --tag --commit --verbose --allow-dirty patch | grep -oP 'new_version=\K.*$')
  git push origin tag $NEW_VERSION

  exit 0
}

main "$@"
