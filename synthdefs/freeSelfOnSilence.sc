// https://github.com/supercollider/supercollider/wiki/Recording-in-SuperCollider-(WIP)#serverrecord-and-compositions
(
    // This SynthDef has no output, only stereo input.
    // It frees itself the next time the input audio is silent.
    SynthDef(\freeSelfOnSilence, {
        |in = 0, threshold = (-80.dbamp), time = 1|
        var snd;
        snd = In.ar(in, 2);
        // This weird construction is explained in the DetectSilence help file.
        FreeSelf.kr(
            DetectSilence.ar(snd + Impulse.ar(0), threshold, time).product
        );
    }).store;
)
