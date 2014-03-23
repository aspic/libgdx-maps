package no.mehl.libgdx.map.util;

import com.badlogic.gdx.math.Interpolation;

/** Class to wrap an interpolation in order to keep track of elapsed and max time. */
public abstract class Interpolator<T> {

	private float runtime;
	private float time;
	private Interpolation interpolation;
	private T start;
	private T end;

	public Interpolator(float runtime, Interpolation interpolation) {
		this.runtime = runtime;
		this.interpolation = interpolation;
		this.time = runtime;
	}

	/** Runs this interpolation unless it's finished. */
	public void interpolate(float delta) {
		if(time >= runtime) return;

		time += delta;
		interpolate(time / runtime, interpolation, start, end);
	}

	/** Start the interpolation with the current start and end values */
	public void start(T start, T end) {
		this.start = start;
		this.end = end;
		this.time = 0;
	}

	/** Perform the interpolation based on elapsed time */
	protected abstract void interpolate(float elapsed, Interpolation interpolation, T start, T end);
}
