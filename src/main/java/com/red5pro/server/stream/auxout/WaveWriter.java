package com.red5pro.server.stream.auxout;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class WaveWriter implements AuxOut {
    /**
     * Format of decoded auxiliary out. Currently only pcm supported though aux
     * interface.
     */
    private static final int Format = ('P') | ('C' << 8) | ('M' << 16) | (' ' << 24);

    private static final int HEADER_LENGTH = 44;

    private static final int RIFF = 1380533830; // 'RIFF'

    private static final long WAVE_FMT = 6287401410857104416l; // 'WAVEfmt '

    public static final short WAVE_FORMAT_PCM = 0x0001;

    public static final short WAVE_FORMAT_ALAW = 0x0006;

    public static final short WAVE_FORMAT_MULAW = 0x0007;

    private static final int DATA = 1684108385; // 'data'

    /**
     * Number of channels requested in PCM
     */
    private int channels;

    /**
     * Sample rate requested in PCM.
     */
    private int rate;

    /**
     * Samples will always be received as 16 bits
     */
    private int bitsPerSample;

    /**
     * Count of cached samples.
     */
    private int segmentWritten;

    /**
     * wave file destination.
     */
    private RandomAccessFile out;

    /**
     * File size
     */
    private long bytesWritten;

    /**
     * Engine
     */
    private ExecutorService runner = Executors.newFixedThreadPool(1);

    /**
     * To join at stop.
     */
    private Future<?> future;

    /**
     * Duration cached between disk writes.
     */
    private int duration = 1;

    private boolean doRun = true;

    private LinkedBlockingQueue<WaveSamples> soundin = new LinkedBlockingQueue<WaveSamples>();

    private LinkedBlockingQueue<WaveSamples> soundout = new LinkedBlockingQueue<WaveSamples>();

    /**
     * Construct a wave writer and specify the desired sample rate, channel count,
     * and bit depth. Use zeros to bypass resampling.
     *
     * @param name
     *            full file path.
     * @param chan
     *            number of channels.
     * @param rate
     *            sample rate.
     * @param bitsPerSample
     *            bit depth.
     */
    public WaveWriter(String name, int chan, int rate, int bitsPerSample) {
        this.channels = chan;
        this.rate = rate;
        this.bitsPerSample = bitsPerSample;
        try {
            out = new RandomAccessFile(name, "rw");
            out.write(new byte[HEADER_LENGTH]);
            future = runner.submit(this);
        } catch (IOException e) {
        }
    }

    /**
     * Receive decoded PCMs
     */
    public void packetReceived(WaveSamples samples) {
        if (this.channels == 0) {
            this.channels = samples.channels;
        }
        if (this.rate == 0) {
            this.rate = samples.rate;
        }
        // samples will always be 16 bits depth.
        if (this.bitsPerSample == 0) {
            this.bitsPerSample = samples.bitsPerSample;
        } else {
            // so if you want 8bit ulaw/alaw, do it here.
            // If alaw/ulaw, change the WAVE_FORMAT_PCM at 'close'
            if (this.bitsPerSample == 8) {
                // TODO
            }
        }

        soundin.add(samples);
    }

    @Override
    public void run() {
        while (doRun) {
            try {
                WaveSamples data = soundin.poll(250, TimeUnit.MILLISECONDS);
                if (data != null) {
                    writeSamples(data);
                }
            } catch (InterruptedException e) {
            }
        }

        doFlush();

        try {
            close();
        } catch (IOException e) {
        }
    }

    private void writeSamples(WaveSamples data) {

        int sampleCount = (data.samples.length / (bitsPerSample / 8)) / data.channels;
        segmentWritten += sampleCount;
        soundout.add(data);
        if (segmentWritten >= rate * duration) {
            doFlush();
            segmentWritten = 0;
        }
    }

    private void doFlush() {
        WaveSamples samples;
        while ((samples = soundout.poll()) != null) {
            byte[] data = samples.samples;
            try {
                bytesWritten += data.length;
                out.write(data, 0, data.length);
            } catch (IOException e) {
            }
        }
    }

    public void stop() {
        this.doRun = false;
        try {
            future.get(500, TimeUnit.MILLISECONDS);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void close() throws IOException {
        out.seek(0);
        final int bytesPerSec = (bitsPerSample + 7) / 8;
        out.writeInt(RIFF); // wave label
        out.writeInt(Integer.reverseBytes((int) ((bytesWritten + 36) & 0xFFFFFFFFL))); // length in bytes without header
        out.writeLong(WAVE_FMT);
        out.writeInt(Integer.reverseBytes(16)); // length of pcm format declaration area
        out.writeShort(Short.reverseBytes((short) WAVE_FORMAT_PCM)); // is PCM
        out.writeShort(Short.reverseBytes((short) channels)); // number of channels
        out.writeInt(Integer.reverseBytes(rate)); // sample rate
        out.writeInt(Integer.reverseBytes(rate * channels * bytesPerSec)); // bytes per second
        out.writeShort(Short.reverseBytes((short) (channels * bytesPerSec))); // bytes per sample time
        out.writeShort(Short.reverseBytes((short) bitsPerSample)); // bits per sample
        out.writeInt(DATA); // data section label
        out.writeInt(Integer.reverseBytes((int) (bytesWritten & 0xFFFFFFFFL))); // length of raw pcm data in bytes
        out.close();
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getFormat() {
        return Format;
    }

    public int getBitsPerSample() {
        return bitsPerSample;
    }

    public long getSize() {
        return bytesWritten + 44;
    }

    /**
     * Set the file write frequency in seconds. Default is 1 second.
     *
     * @param duration
     *            between file writes.
     */
    public void setFlushDuration(int duration) {
        this.duration = duration;
    }
}
