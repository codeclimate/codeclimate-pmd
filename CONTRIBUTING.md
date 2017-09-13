# Contributing

## Testing
`make test` will build the docker image and run the tests

## Upgrading PMD Version
1. `make upgrade` => will adjust `bin/install-pmd.sh` with the URL to the latest version
1. `make test`
1. If all looks good, commit the change and open a PR :)
