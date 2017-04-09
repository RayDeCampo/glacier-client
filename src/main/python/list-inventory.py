#!/usr/bin/env python
import json

with open('inventory.dat') as f:
    for line in f:
        data = json.loads(line)
for archive in data['ArchiveList']:
    print("%s\t%s" % (archive['CreationDate'], archive['ArchiveDescription']))
