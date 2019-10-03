#coding:utf-8
import re
import sys
import fileinput
import re

for line in sys.stdin:
    line = line.replace('_organization','&_organization').replace('_time','&_time').replace('_person','&_person').replace('_location','&_location').replace('_profession','&_profession')
    line = line.strip()
    line_list = line.split()
    for i in range(len(line_list)) :
        if '&_' not in line_list[i]:
            line_list[i] = line_list[i] + '&_O'
    print (' '.join(line_list))
