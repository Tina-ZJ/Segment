# -*- coding:utf-8 -*-
import sys

for line in sys.stdin:
    if line.strip() == "":
        sys.stdout.write('\n')
    line = line.strip().split('\t')
    if len(line) != 2:
        continue
    key = line[0].strip()
    tab = line[1].strip()
    sys.stdout.write(key +'/'+ tab + '\t')
