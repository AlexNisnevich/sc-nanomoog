// To run:
// /Applications/SuperCollider.app/Contents/MacOS/sclang -d . play.sc
// play ./out.wav
(
s = Server.local;
s.waitForBoot {
    Routine.run {
		var synth = Synth("Moog", [
			freq: 440,
			volume: 1,
			osc1Shape: 2, // 0 = saw, 1 = sine, 2 = square
			osc1Level: 0.5,
			osc2Level: 0,
			filterCutoff: 2000,
			filterResonance: 3.0,
			filterEnvAmt: 2
		]);

        s.sync;
        s.record("./out.wav".standardizePath, 0, 2);

		1.wait;  // this should be attack time + decay time + hold time
		synth.set('gate', 0);
        1.wait;  // this should be release time
        s.stopRecording;

        0.exit;  // comment out this line in SuperCollider IDE
    }
}
)