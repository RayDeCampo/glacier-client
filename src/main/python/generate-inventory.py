#!/usr/bin/env python
import json

with open('inventory.dat') as f:
    for line in f:
        data = json.loads(line)
for archive in data['ArchiveList']:
    print("%s:%s" % (archive['ArchiveDescription'], archive['ArchiveId']))
