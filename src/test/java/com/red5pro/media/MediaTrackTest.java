package com.red5pro.media;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.red5.codec.AudioCodec;
import org.red5.codec.VideoCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.red5pro.group.GroupEvent;

public class MediaTrackTest {

    private static Logger log = LoggerFactory.getLogger(MediaTrackTest.class);

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @SuppressWarnings("incomplete-switch")
    @Test
    public void test() {
        MediaTrack[] tracks = new MediaTrack[2];
        // XXX if and when the tracks are not opus and h264, we'll have to add them to
        // the provision
        tracks[0] = new MediaTrack(MediaType.AUDIO, FourCC.OPUS, "audio0");
        tracks[1] = new MediaTrack(MediaType.VIDEO, FourCC.H264, "video0");
        // create some events and push to the tracks
        // push(event);
        do {
            createEvents().forEach(event -> {
                switch (event.getFourCC()) {
                    case OPUS:
                        tracks[0].push(event);
                        break;
                    case H264:
                        tracks[1].push(event);
                        break;
                }
            });
        } while (tracks[0].events.size() < 10);
        log.info("Audio events: {}", tracks[0].events);
        // pops head from the track
        // pop();
        int videoEventsCount = tracks[1].events.size();
        assertTrue(((GroupEvent) tracks[1].pop()).getFourCC().equals(FourCC.H264));
        assertTrue(videoEventsCount > tracks[1].events.size());
        // pops head from the track matching a given fourcc
        // pop(fourCC);
        int audioEventsCount = tracks[0].events.size();
        GroupEvent age = (GroupEvent) tracks[0].pop(FourCC.OPUS);
        assertTrue(age.getFourCC().equals(FourCC.OPUS));
        assertTrue(audioEventsCount > tracks[0].events.size());
        // check the composite data
        CompositeMediaSample cms = (CompositeMediaSample) age.getObject();
        // assertTrue(cms.containsFourCC(FourCC.OPUS));
        // assertTrue(cms.containsFourCC(FourCC.PCM));
        assertNotNull(cms.get(AudioCodec.PCM.getFourcc()));
    }

    // create events for the tracks
    // GroupEvent.build(IEvent.STREAM_DATA, null, data, fourCC);
    private LinkedList<GroupEvent> createEvents() {
        long now = System.currentTimeMillis();
        LinkedList<GroupEvent> events = new LinkedList<>();
        // create media samples to hold opus and pcm
        MediaSample opus = MediaSample.build(now, new byte[] { 3, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, MediaType.AUDIO);
        opus.setFourCC(AudioCodec.OPUS.getFourcc());
        MediaSample pcm = MediaSample.build(now, new byte[] { 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, MediaType.AUDIO);
        pcm.setFourCC(AudioCodec.PCM.getFourcc());
        // create CompositeMediaSample for holding an opus frame with its decoded pcm
        // bytes
        CompositeMediaSample cms = new CompositeMediaSample();
        // TODO!
        // cms.add(opus);
        // cms.add(pcm);
        // create the audio group event
        GroupEvent age = GroupEvent.build(cms, FourCC.OPUS);
        events.add(age);
        // create MediaSample to hold h264 bytes
        MediaSample h264 = MediaSample.build(now, new byte[] { 7, 1, 0, 1, 1, 1, 3, 3 }, MediaType.VIDEO);
        h264.setFourCC(VideoCodec.AVC.getFourcc());
        // create the video group event
        GroupEvent vge = GroupEvent.build(h264, FourCC.H264);
        events.add(vge);
        return events;
    }

}
