package com.red5pro.media;

import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Container for multiple MediaSample instances. This is meant to contain one or
 * more MediaSamples of the same instant, segment, frame, slice...
 * 
 * @author Paul Gregoire
 *
 */
public class CompositeMediaSample extends MediaSampleAdapter {

	private Set<MediaSample> samples = new LinkedHashSet<>(2);

	private EnumSet<FourCC> fourCCs = EnumSet.noneOf(FourCC.class);

	public void add(MediaSample sample) {
		if (samples.add(sample)) {
			fourCCs.add(sample.getFourCC());
		}
	}

	public boolean remove(MediaSample sample) {
		if (samples.remove(sample)) {
			fourCCs.remove(sample.getFourCC());
			return true;
		}
		return false;
	}

	public int size() {
		return samples.size();
	}

	public boolean containsFourCC(FourCC fourCC) {
		return fourCCs.contains(fourCC);
	}

	/**
	 * Returns a MediaSample matching the FourCC given or null if not found; this
	 * does not remove the sample.
	 * 
	 * @param fourCC
	 * @return MediaSample or null if not found
	 */
	public MediaSample get(FourCC fourCC) {
		Optional<MediaSample> sample = samples.stream().filter(p -> p.getFourCC().equals(fourCC)).findFirst();
		if (sample.isPresent()) {
			return sample.get();
		}
		return null;
	}

	@Override
	public boolean isComposite() {
		return true;
	}

	@Override
	public Object getBuffer() {
		// just as a convenience the buffer from the first sample is returned
		if (!samples.isEmpty()) {
			return samples.iterator().next().getBuffer();
		}
		return null;
	}

	@Override
	public long getStartTime() {
		// just as a convenience the start time from the first sample is returned
		if (!samples.isEmpty()) {
			return samples.iterator().next().getStartTime();
		}
		return 0;
	}

}
