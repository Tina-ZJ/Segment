#coding:utf-8
import re
import sys


for line in sys.stdin:
  for x in line.split():
    print (x.strip())
  print()
