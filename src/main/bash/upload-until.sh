#!/bin/bash
#
source "$( dirname "$0")/env.sh"
#
time=$1
#
function main
{
    target=$( date -d "${time}" '+%s' )
    now=$( date '+%s' )
    if [[ ${target} -le ${now} ]]; then
        let "target += 60*60*24"
    fi
    echo "Uploading until" $( date -d @${target} )
    while [[ $( date '+%s' ) -le ${target} ]]
    do
        if [ -n "$( first_in_q ${upq} )" ]; then
            upload_one
        else
            echo "Nothing in upload queue"
            exit
        fi
    done
}

main >>${uplog} 2>&1

