#!/bin/bash
#
source "$( dirname "$0")/env.sh"
#
if [ "$#" -eq 0 ]; then
    ${awsclient} org.decampo.aws.glacier.VaultInventory
else
    ${awsclient} org.decampo.aws.glacier.VaultInventoryResult "$@"
fi

