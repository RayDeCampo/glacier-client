#!/bin/bash
#
bin="$( cd $( dirname "$0" ) && pwd )"
base="$( dirname "${bin}" )"
data="${base}/data"
conf="${base}/conf"
logd="${base}/log"
uplog="${logd}/up.log"
dellog="${logd}/del.log"
dnlog="${logd}/down.log"
tard="/backup/tars"
upq="${data}/upload.q"
delq="${data}/delete.q"
dnq="${data}/download.q"
tmpf="/tmp/tmp.$$"
inv="${data}/inv.dat"
awsclient="java -cp ${conf}:${bin}/glacier-client.jar"
#
success=1
#
tars="fin ray anne pictures.2010s video.2013 music eli"
#
function init
{
    if [ ! -e ${logd} ]; then
        mkdir ${logd}
    fi
    if [ ! -e ${upq} ]; then
        touch ${upq}
    fi
    if [ ! -e ${delq} ]; then
        touch ${delq}
    fi
}
#
function techo
{
    echo "$( date "+%F %T" )" "$*"
}
#
function queue_default
{
    for tar in ${tars}
    do
        q_for_upload ${tard}/${tar}.tar.gz
    done
}
#
function first_in_q
{
    head -1 "$1"
}
#
function pop_from_q
{
    q="$1"
    tail -n +2 $q >$tmpf
    mv $tmpf $q
}
#
function q_one
{
    q="$1"
    tar="$2"
    if grep -q "^${tar}" "${q}"; then
        techo "${tar} already queued in ${q}"
    else
        echo "${tar}" >>${q}
    fi
}
#
function remove_archive_from_inv
{
    archive="$1"
    grep -v -e "${archive}" ${inv} >${tmpf}
    mv ${tmpf} ${inv}
}
#
function q_for_upload
{
    q_one "${upq}" "$1"
}
#
function q_for_delete
{
    q_one "${delq}" "$1"
}
#
function q_for_download
{
    q_one "${dnq}" "$1"
}
#
function upload_one
{
    tgt="$( first_in_q ${upq} )"
    if [ -n "${tgt}" ]
    then
        techo "Uploading:"
        ls -lh "${tgt}"
        if aws_upload "${tgt}"
        then
            pop_from_q ${upq}
            q_for_delete "$tgt"
            techo "${tgt} uploaded and queued for delete"
        else
            techo "Upload of ${tgt} failed"
        fi
    else
        echo "Nothing in upload queue"
    fi
}
#
function delete_one
{
    tgt="$( first_in_q ${delq} )"
    if [ -n "${tgt}" ]
    then
        techo "Deleting ${tgt}"
        archive="$( grep "^${tgt}:" ${inv} | head -1 | cut -d: -f2 )"
        if [ -n "${archive}" ]; then
            lc="$( grep "^${tgt}:" "${inv}" | wc -l )"
            if [ $lc -ge 2 ]; then
                if aws_delete "${archive}"
                then
                    remove_archive_from_inv "${archive}"
                    pop_from_q ${delq}
                    techo "${tgt} deleted"
                else
                    techo "Delete of ${tgt} failed"
                fi
             else
                 techo "Only one archive found in inventory, not deleting ${tgt}"
                 pop_from_q ${delq}
             fi
        else
            techo "No archive found for ${tgt}"
            pop_from_q ${delq}
        fi
    else
        techo "Nothing in delete queue"
    fi
}
#
function delete_file
{
    tgt="$1"
    techo "Deleting ${tgt}"
    archive="$( grep "^${tgt}:" ${inv} | head -1 | cut -d: -f2 )"
    if [ -n "${archive}" ]; then
        if aws_delete "${archive}"
        then
            remove_archive_from_inv "${archive}"
            techo "${tgt} deleted"
        else
            techo "Delete of ${tgt} failed"
        fi
    else
        techo "No archive found for ${tgt}"
    fi
}
#
function download_file
{
    tgt="$1"
    techo "Downloading ${tgt}"
    archive="$( grep "/${tgt}:" ${inv} | head -1 | cut -d: -f2 )"
    if [ -n "${archive}" ]; then
        if aws_download "${archive}" "${tgt}"
        then
            techo "${tgt} downloaded"
        else
            techo "Download of ${tgt} failed"
        fi
    else
        techo "No archive found for ${tgt}"
    fi
}
#
function aws_upload
{
    ${awsclient} org.decampo.aws.glacier.UploadArchive "$1" >>${inv}
    return $?
}
#
function aws_delete
{
    ${awsclient} org.decampo.aws.glacier.DeleteArchive "$1"
    return $?
}
#
function aws_download
{
    ${awsclient} org.decampo.aws.glacier.DownloadArchive "$1" "$2"
    return $?
}
#
#
init

