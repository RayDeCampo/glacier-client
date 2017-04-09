#!/bin/bash
#
source "$( dirname "$0")/env.sh"
#
if [ -z "$*" ]; then
    queue_default
else
    while [ -n "$1" ]
    do
        q_for_upload "$1"
        shift
    done
fi

