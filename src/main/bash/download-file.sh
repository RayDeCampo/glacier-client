#!/bin/bash -x
#
source "$( dirname "$0")/env.sh"
#
for tar in "$@"
do
    download_file "${tar}" >>${dnlog} 2>&1
done
