# sc-nanomoog

Simple Moog-like subtractive synth in SuperCollider, meant to be
runnable from command line.

Install [SuperCollider](https://supercollider.github.io/download).

First, generate a random synth:
```
./autogenerate SEED
```

Then, generate a WAV from the synth:
```
path/to/sclang SEED.sc
```
(Is it possible to disable the output, and thus render faster than
realtime?)
