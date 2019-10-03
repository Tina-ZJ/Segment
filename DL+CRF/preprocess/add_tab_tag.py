#coding:utf-8
import re
import sys

sep = '&_'

for line in sys.stdin:
  line = line.strip()
  if line == '':
    print()
  elif line in [u'，',u'。',u'；',u'？',u'！',u',',u';',u'?',u'!']:
    print (line+'\t'+'O')
    #print 
  elif sep not in line:
    line = ''.join(line.split())
    for x in list(line):
      print (x+'\t'+'O')
  else:
    text = line.split(sep)[0]
    tag = line.split(sep)[1]
    for i,x in enumerate(list(text)):
      if i==0 and len(text)==1:
        print (x+'\t'+'S_'+tag)
      elif i==0:
        print (x+'\t'+'B_'+tag)
      elif i==len(text)-1:
        print (x+'\t'+'E_'+tag)
      else:
        print (x+'\t'+'I_'+tag)
