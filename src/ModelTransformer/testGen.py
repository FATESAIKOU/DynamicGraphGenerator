from pprint import pprint
import json
import sys

INPUTFILE  = sys.argv[1]
OUTPUTFILE = sys.argv[2]

rsrc = open(INPUTFILE, 'r')
data = rsrc.read()
rsrc.close()

mdl = json.loads(data)

features = {}
for tri_g in mdl['routes']:
    if 'routes' not in tri_g:
        continue

    for bi_g in tri_g['routes']:
        if 'routes' not in bi_g:
            continue
        
        for state in bi_g['routes']:
            if state['name'] in features:
                now_count = features[state['name']]
            else:
                now_count = 0

            features[state['name']] = now_count + state['count']


wsrc = open(OUTPUTFILE, 'w')
wsrc.write(json.dumps(features))
wsrc.close()

print "TransFormComplete"
pprint(features)
