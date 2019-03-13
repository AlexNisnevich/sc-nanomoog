// To run:
// /Applications/SuperCollider.app/Contents/MacOS/sclang play.sc
// play ./out.wav
(
// Parameter values go here. See synth def below for parameter ranges and notes.
// (Any unspecified parameters will use the default value specified in the synth def.)
var parameters = [
	freq: 440,
	volume: 1,
	osc1Shape: 2, // 0 = saw, 1 = sine, 2 = square
	osc1Level: 0.5,
	osc2Level: 0,
	filterCutoff: 2000,
	filterResonance: 3.0,
	filterEnvAmt: 2
];

// Adapted from https://superdupercollider.blogspot.com/2009/02/this-is-blog-about-supercollider.html
// For basic architecture see http://w2.mat.ucsb.edu/240/A/2017/04/26/supercollider.html
SynthDef("Moog", {
    arg osc1Shape = 0, osc2Shape = 2,  // 0 = saw, 1 = sin, 2 = square
        osc1Level = 0.5, osc2Level = 0.5, volume = 0.5,  // range: 0-1
        filterCutoff = 2000, filterResonance = 0,  // cutoff: 20-20000, resonance: 0-4
        filterEnvAmt = 0,  // range should be about -5 to 5, but negative values won't work correctly yet
                           // In general this isn't right yet because it should scale the cutoff logarithmically but it's a little tricky to model exactly
                           // (e.g. on my Sub37, a cutoff of 500 Hz will go up to ~1.2 KHz when EnvAmt = 1 and down to ~320 Hz when EnvAmt = -1)
        // Envelope ranges: 0-10 for attack, decay, release (in seconds) but the wait times in play.sc should be modified accordingly
        //                  sustain is 0-1
        ampAttack = 0.1, ampDecay = 0.1, ampSustain = 0.7, ampRelease = 0.2,
        filterAttack = 0.1, filterDecay = 0.1, filterSustain = 0.9, filterRelease = 0.2,
        gate = 1, freq = 440;

    var oscArray1 = [Saw.ar(freq), SinOsc.ar(freq), Pulse.ar(freq)];  // TODO pulse width control; more oscillator types
    var oscArray2 = [Saw.ar(freq), SinOsc.ar(freq), Pulse.ar(freq)];
    var ampEnv = EnvGen.ar(Env.adsr(ampAttack, ampDecay, ampSustain, ampRelease, volume), gate, doneAction:2);
    var filterEnv = EnvGen.ar(Env.adsr(filterAttack, filterDecay, filterSustain, filterRelease, filterEnvAmt), gate, doneAction:2);

    // TODO oscillator modulation (frequency, PWM)
    var osc1 = Select.ar(osc1Shape, oscArray1);
    var osc2 = Select.ar(osc2Shape, oscArray2);  // TODO detune; hard sync
    // TODO third or sub oscillator

    // TODO noise source
	var mixer = (osc1 * osc1Level) + (osc2 * osc2Level);  // TODO mixer should "overdrive" at high levels

    // TODO filter and amp modulation
    // TODO the filter cutoff (modulated by envelope) formula isn't quite right (see filterEnvAmt comment above)
    // TODO clip filter amount when it goes above 20 kHz
    var filter = MoogFF.ar(mixer, filterCutoff * (1 + filterEnv), filterResonance);
    var amp = filter * ampEnv;

    Out.ar(0, amp)
}).store;

s = Server.local;
s.waitForBoot {
    Routine.run {
		var synth = Synth("Moog", parameters);

        s.sync;
        s.record("./out.wav".standardizePath, 0, 2);

		1.wait;  // TODO this should be attack time + decay time + hold time
		synth.set('gate', 0);
        1.wait;  // TODO this should be release time
        s.stopRecording;

        0.exit;  // comment out this line if running this script in SuperCollider IDE
    }
}
)