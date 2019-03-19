#!/usr/bin/env python3
"""
Generate a random .sc synth, and corresponding JSON file with
parameters.

It would also be nicer to use a proper templating library, and
probably more DRY.
"""

import sys
import math
import random
import json

assert len(sys.argv) == 2, "USAGE: ./autogenerate.py SEED"
SEED = int(sys.argv[1])
random.seed(SEED)
longseed = "%08d" % SEED

p = """
var parameters = [
        freq: %d,
        volume: %f,
        osc1Shape: %d, // 0 = saw, 1 = sine, 2 = square
        osc1Level: %f,
        osc2Shape: %d,
        osc2Level: %f,
        filterCutoff: %d,
        filterResonance: %f,
        filterEnvAmt: %d
        // TODO: It looks like there are other params that are not here?
];"""

def sample_hertz(min_hertz, max_hertz):
    h = min_hertz * 2 ** (random.random() * math.log(max_hertz / min_hertz) / math.log(2))
    assert(h >= min_hertz)
    assert(h <= max_hertz)
    return h

freq = sample_hertz(20, 20000)
volume = random.random()
osc1Shape = random.choice([0, 1, 2])
osc1Level = random.random()
osc2Shape = random.choice([0, 1, 2])
osc2Level = random.random()
filterCutoff = sample_hertz(20, 20000)
filterResonance = random.random() * 4       # 0 - 4
# range should be about -5 to 5, but negative values won't work correctly yet
filterEnvAmt = random.random() * 5

parameters = (p % (freq, volume, osc1Shape, osc1Level, osc2Shape, osc2Level, filterCutoff, filterResonance, filterEnvAmt))
sctmpl = open("play.sc.tmpl").read()

# I'd like to put all the output files in output/ but then sc
# gives me cryptic error messages
scscript = (sctmpl % (parameters, "%s.wav" % longseed))
open("%s.sc" % longseed, "wt").write(scscript)
open("%s.json" % longseed, "wt").write(json.dumps(
{
"freq": freq,
"volume": volume,
"osc1Shape": osc1Shape,
"osc1Level": osc1Level,
"osc2Shape": osc2Shape,
"osc2Level": osc2Level,
"filterCutoff": filterCutoff,
"filterResonance": filterResonance,
"filterEnvAmt": filterEnvAmt
}
))
