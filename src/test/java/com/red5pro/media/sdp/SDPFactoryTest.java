package com.red5pro.media.sdp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.red5pro.media.rtp.RTPCodecEnum;
import com.red5pro.media.sdp.model.AttributeField;
import com.red5pro.media.sdp.model.AttributeKey;
import com.red5pro.media.sdp.model.BandwidthField;
import com.red5pro.media.sdp.model.MediaField;
import com.red5pro.media.sdp.model.SDPMediaType;
import com.red5pro.server.util.NetworkManager;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class SDPFactoryTest {

    private static Logger log = LoggerFactory.getLogger(SDPFactoryTest.class);

    String mozillaOffer = "v=0\no=mozilla...THIS_IS_SDPARTA-45.0.2 3726311801733287589 0 IN IP4 0.0.0.0\ns=-\nt=0 0\na=fingerprint:sha-256 12:A8:FF:8B:83:05:43:5F:83:0F:E8:64:58:D2:61:97:ED:6C:D9:02:4C:F8:DD:68:7B:FB:48:96:56:91:3E:03\na=group:BUNDLE sdparta_0 sdparta_1\na=ice-options:trickle\na=msid-semantic:WMS *\na=identity:eyJpZHAiOnsicHJvdG9jb2wiOiJpZHAuanMiLCJkb21haW4iOiJtYXJ0aW50aG9tc29uLmdpdGh1Yi5pbyJ9LCJhc3NlcnRpb24iOiJ7XCJjb250ZW50c1wiOlwie1xcXCJmaW5nZXJwcmludFxcXCI6W3tcXFwiYWxnb3JpdGhtXFxcIjpcXFwic2hhLTI1NlxcXCIsXFxcImRpZ2VzdFxcXCI6XFxcIjEyOkE4OkZGOjhCOjgzOjA1OjQzOjVGOjgzOjBGOkU4OjY0OjU4OkQyOjYxOjk3OkVEOjZDOkQ5OjAyOjRDOkY4OkREOjY4OjdCOkZCOjQ4Ojk2OjU2OjkxOjNFOjAzXFxcIn1dfVwiLFwicHViXCI6XCJCRnppMV8zSVpyaUFldjBtajFsNmhTMTgzVkxNWWo4Xzk1V0RzdElCM1ZUUExFREx2QVp1aEZGektfemY5MEduRF9yeFI2UDlNZlMtYXp5bUxNTjFwaU1cIixcInNpZ25hdHVyZVwiOlwielB5TjZMZ2Q0bDU5a1hNeTZtTVBwZ2VGbVRkelZQeGVleWs3emxDaU1WSVBQcWh4dWtXOV9zRGtpYUVYRUp4aXI1WlJLQ3ZSY0psQlFvMU44NGxFWVFcIn0ifQ==\nm=audio 9 UDP/TLS/RTP/SAVPF 109 9 0 8\nc=IN IP4 0.0.0.0\na=sendrecv\na=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\na=ice-pwd:86f37419ac712f71efbe25fcba118905\na=ice-ufrag:9b6e8cdb\na=mid:sdparta_0\na=msid:{7e4b0f1c-760d-4d64-a67c-52f1590a1345} {e508715d-4b84-4366-b3c0-4e3bfa88644a}\na=rtcp-mux\na=rtpmap:109 opus/48000/2\na=rtpmap:9 G722/8000/1\na=rtpmap:0 PCMU/8000\na=rtpmap:8 PCMA/8000\na=setup:actpass\na=ssrc:458177797 cname:{1c1ec747-32bc-4491-8786-be3501749143}\nm=video 9 UDP/TLS/RTP/SAVPF 120 126 97\nc=IN IP4 0.0.0.0\na=sendrecv\na=fmtp:126 profile-level-id=42e01f;level-asymmetry-allowed=1;packetization-mode=1\na=fmtp:97 profile-level-id=42e01f;level-asymmetry-allowed=1\na=fmtp:120 max-fs=12288;max-fr=60\na=ice-pwd:86f37419ac712f71efbe25fcba118905\na=ice-ufrag:9b6e8cdb\na=mid:sdparta_1\na=msid:{7e4b0f1c-760d-4d64-a67c-52f1590a1345} {052dd88d-2324-4a06-a59b-b4781f8553df}\na=rtcp-fb:120 nack\na=rtcp-fb:120 nack pli\na=rtcp-fb:120 ccm fir\na=rtcp-fb:126 nack\na=rtcp-fb:126 nack pli\na=rtcp-fb:126 ccm fir\na=rtcp-fb:97 nack\na=rtcp-fb:97 nack pli\na=rtcp-fb:97 ccm fir\na=rtcp-mux\na=rtpmap:120 VP8/90000\na=rtpmap:126 H264/90000\na=rtpmap:97 H264/90000\na=setup:actpass\na=ssrc:2006261495 cname:{1c1ec747-32bc-4491-8786-be3501749143}";

    String mozillaOffer2 = "v=0\no=mozilla...THIS_IS_SDPARTA-45.0.2 923667816526852730 0 IN IP4 0.0.0.0\ns=-\nt=0 0\na=fingerprint:sha-256 91:59:D9:43:06:07:C3:82:90:F7:22:C9:07:8D:46:C8:6F:3C:5B:5C:D5:02:E8:5B:B7:C8:4E:69:0E:69:42:F9\na=group:BUNDLE sdparta_0 sdparta_1\na=ice-options:trickle\na=msid-semantic:WMS *\nm=audio 9 UDP/TLS/RTP/SAVPF 109 9 0 8\nc=IN IP4 0.0.0.0\na=sendonly\na=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\na=ice-pwd:7ce1bc39c0b9d80216e1d18b21acdbea\na=ice-ufrag:88149b7c\na=mid:sdparta_0\na=msid:{f5cfc3ed-0e56-4046-81dd-1e5068bfe197} {f7a7573b-a8dc-4bdb-b643-3262856ff274}\na=rtcp-mux\na=rtpmap:109 opus/48000/2\na=rtpmap:9 G722/8000/1\na=rtpmap:0 PCMU/8000\na=rtpmap:8 PCMA/8000\na=setup:actpass\na=ssrc:2173082761 cname:{b314ef0e-e7e2-481a-853d-a10cb758e301}\nm=video 9 UDP/TLS/RTP/SAVPF 120 126 97\nc=IN IP4 0.0.0.0\na=sendonly\na=fmtp:126 profile-level-id=42e01f;level-asymmetry-allowed=1;packetization-mode=1\na=fmtp:97 profile-level-id=42e01f;level-asymmetry-allowed=1\na=ice-pwd:7ce1bc39c0b9d80216e1d18b21acdbea\na=ice-ufrag:88149b7c\na=mid:sdparta_1\na=msid:{f5cfc3ed-0e56-4046-81dd-1e5068bfe197} {5c6f59ee-243a-4dc5-919c-c0c405e1a1d7}\na=rtcp-fb:120 nack\na=rtcp-fb:120 nack pli\na=rtcp-fb:120 ccm fir\na=rtcp-fb:126 nack\na=rtcp-fb:126 nack pli\na=rtcp-fb:126 ccm fir\na=rtcp-fb:97 nack\na=rtcp-fb:97 nack pli\na=rtcp-fb:97 ccm fir\na=rtcp-mux\na=rtpmap:120 VP8/90000\na=rtpmap:126 H264/90000\na=rtpmap:97 H264/90000\na=setup:actpass\na=ssrc:3821226334 cname:{b314ef0e-e7e2-481a-853d-a10cb758e301}";

    String chromeOffer = "v=0\no=flpublisher1 5273559690318 1 IN IP4 127.0.0.1\ns=-\nt=0 0\nm=audio 5000 UDP/TLS/RTP/SAVPF 111\nc=IN IP4 67.167.168.182\na=rtcp:5001 IN IP4 67.167.168.182\na=rtpmap:111 opus/48000/2\na=fmtp:111 minptime=10\na=maxptime:60\na=ice-ufrag:6rptv1a8eq00bs\na=ice-pwd:m54m8482bqpgd1rd1p7q47t96\na=fingerprint:sha-256 DE:E9:F6:B3:4E:4C:D5:7A:6A:5C:A1:43:78:7E:CE:D3:03:36:69:B6:42:D4:D6:53:A9:5A:B7:0E:E8:55:07:EC\na=setup:actpass\na=mid:audio\na=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\na=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\na=sendonly\na=ssrc:788675255 cname:flpublisher1\na=ssrc:788675255 msid:884f8786b51d7e66 fcc654be-37bc-43b1-bc7b-a8609c7c93f3\na=ssrc:788675255 mslabel:884f8786b51d7e66\na=ssrc:788675255 label:fcc654be-37bc-43b1-bc7b-a8609c7c93f3\nm=video 5020 UDP/TLS/RTP/SAVPF 100\nc=IN IP4 67.167.168.182\na=rtcp:5021 IN IP4 67.167.168.182\na=rtpmap:100 VP8/90000\na=rtcp-fb:100 ccm fir\na=rtcp-fb:100 nack\na=rtcp-fb:100 nack pli\na=ice-ufrag:6rptv1a8eq00bs\na=ice-pwd:m54m8482bqpgd1rd1p7q47t96\na=fingerprint:sha-256 0D:DF:44:19:A3:E1:7E:35:9E:D9:AD:8B:B0:64:2C:D8:6F:BF:A6:E6:A3:8C:90:B2:9A:D7:D2:7D:54:6D:07:30\na=setup:actpass\na=mid:video\na=extmap:2 urn:ietf:params:rtp-hdrext:toffset\na=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\na=extmap:4 urn:3gpp:video-orientation\na=sendonly\na=ssrc:480769722 cname:flpublisher1\na=ssrc:480769722 msid:884f8786b51d7e66 cb36c092-79da-404a-b164-b49bd9893bad\na=ssrc:480769722 mslabel:884f8786b51d7e66\na=ssrc:480769722 label:cb36c092-79da-404a-b164-b49bd9893bad";

    String chromeOffer2 = "v=0\r\no=- 6895001608778385081 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\na=group:BUNDLE audio video\r\na=msid-semantic: WMS sCmeUHbBQTtOFIjOTYd4Sv3SaAOsAEg5gMm2\r\nm=audio 9 UDP/TLS/RTP/SAVPF 111 103 104 9 0 8 106 105 13 126\r\nc=IN IP4 0.0.0.0\r\na=rtcp:9 IN IP4 0.0.0.0\r\na=ice-ufrag:dTvo\r\na=ice-pwd:NeoGreMWO4fUXCvNhjrhd4s2\r\na=fingerprint:sha-256 8D:79:55:CF:CD:D6:AC:94:0C:F2:79:2E:D2:27:69:E3:BA:1B:A7:5C:26:89:5C:80:44:41:6F:E0:51:0F:6A:F2\r\na=setup:actpass\r\na=mid:audio\r\nb=AS:50\r\na=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\r\na=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\r\na=sendrecv\r\na=rtcp-mux\r\na=rtpmap:111 opus/48000/2\r\na=rtcp-fb:111 transport-cc\r\na=fmtp:111 minptime=10;useinbandfec=1\r\na=rtpmap:103 ISAC/16000\r\na=rtpmap:104 ISAC/32000\r\na=rtpmap:9 G722/8000\r\na=rtpmap:0 PCMU/8000\r\na=rtpmap:8 PCMA/8000\r\na=rtpmap:106 CN/32000\r\na=rtpmap:105 CN/16000\r\na=rtpmap:13 CN/8000\r\na=rtpmap:126 telephone-event/8000\r\na=ssrc:3877872968 cname:qNVNnk0p6TyUotMq\r\na=ssrc:3877872968 msid:sCmeUHbBQTtOFIjOTYd4Sv3SaAOsAEg5gMm2 28d78f27-d1ef-40e3-bd48-9e5e01feeee6\r\na=ssrc:3877872968 mslabel:sCmeUHbBQTtOFIjOTYd4Sv3SaAOsAEg5gMm2\r\na=ssrc:3877872968 label:28d78f27-d1ef-40e3-bd48-9e5e01feeee6\r\nm=video 9 UDP/TLS/RTP/SAVPF 100 101 107 116 117 96 97 99 98\r\nc=IN IP4 0.0.0.0\r\na=rtcp:9 IN IP4 0.0.0.0\r\na=ice-ufrag:dTvo\r\na=ice-pwd:NeoGreMWO4fUXCvNhjrhd4s2\r\na=fingerprint:sha-256 8D:79:55:CF:CD:D6:AC:94:0C:F2:79:2E:D2:27:69:E3:BA:1B:A7:5C:26:89:5C:80:44:41:6F:E0:51:0F:6A:F2\r\na=setup:actpass\r\na=mid:video\r\nb=AS:256\r\na=extmap:2 urn:ietf:params:rtp-hdrext:toffset\r\na=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\r\na=extmap:4 urn:3gpp:video-orientation\r\na=extmap:5 http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01\r\na=extmap:6 http://www.webrtc.org/experiments/rtp-hdrext/playout-delay\r\na=sendrecv\r\na=rtcp-mux\r\na=rtcp-rsize\r\na=rtpmap:100 VP8/90000\r\na=rtcp-fb:100 ccm fir\r\na=rtcp-fb:100 nack\r\na=rtcp-fb:100 nack pli\r\na=rtcp-fb:100 goog-remb\r\na=rtcp-fb:100 transport-cc\r\na=rtpmap:101 VP9/90000\r\na=rtcp-fb:101 ccm fir\r\na=rtcp-fb:101 nack\r\na=rtcp-fb:101 nack pli\r\na=rtcp-fb:101 goog-remb\r\na=rtcp-fb:101 transport-cc\r\na=rtpmap:107 H264/90000\r\na=rtcp-fb:107 ccm fir\r\na=rtcp-fb:107 nack\r\na=rtcp-fb:107 nack pli\r\na=rtcp-fb:107 goog-remb\r\na=rtcp-fb:107 transport-cc\r\na=fmtp:107 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=42e01f\r\na=rtpmap:116 red/90000\r\na=rtpmap:117 ulpfec/90000\r\na=rtpmap:96 rtx/90000\r\na=fmtp:96 apt=100\r\na=rtpmap:97 rtx/90000\r\na=fmtp:97 apt=101\r\na=rtpmap:99 rtx/90000\r\na=fmtp:99 apt=107\r\na=rtpmap:98 rtx/90000\r\na=fmtp:98 apt=116\r\na=ssrc-group:FID 837217914 3601665145\r\na=ssrc:837217914 cname:qNVNnk0p6TyUotMq\r\na=ssrc:837217914 msid:sCmeUHbBQTtOFIjOTYd4Sv3SaAOsAEg5gMm2 15f35bc1-e1ff-4667-aa87-9f431518d327\r\na=ssrc:837217914 mslabel:sCmeUHbBQTtOFIjOTYd4Sv3SaAOsAEg5gMm2\r\na=ssrc:837217914 label:15f35bc1-e1ff-4667-aa87-9f431518d327\r\na=ssrc:3601665145 cname:qNVNnk0p6TyUotMq\r\na=ssrc:3601665145 msid:sCmeUHbBQTtOFIjOTYd4Sv3SaAOsAEg5gMm2 15f35bc1-e1ff-4667-aa87-9f431518d327\r\na=ssrc:3601665145 mslabel:sCmeUHbBQTtOFIjOTYd4Sv3SaAOsAEg5gMm2\r\na=ssrc:3601665145 label:15f35bc1-e1ff-4667-aa87-9f431518d327\r\n";

    String answer = "v=0\no=paul 304164567322 3 IN IP4 67.167.168.182\ns=-\nt=0 0\nm=audio 49152 UDP/TLS/RTP/SAVPF 111\nc=IN IP4 67.167.168.182\na=rtcp:49153 IN IP4 67.167.168.182\na=rtpmap:111 opus/48000/2\na=fmtp:111 minptime=10; useinbandfec=1\na=maxptime:60\na=ice-ufrag:c4emj1b3bn0qpq\na=ice-pwd:9t8kbjqbknhkchct525l1lj0h\na=ice-options:trickle\na=fingerprint:sha-256 AF:1C:FD:1D:91:A2:77:BE:8E:2F:77:8D:4B:DE:26:03:6C:DF:51:77:EB:3C:19:FD:CC:CF:CB:A7:5F:AC:6B:7E\na=setup:passive\na=mid:audio\na=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\na=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\na=recvonly\nm=video 49154 UDP/TLS/RTP/SAVPF 100\nc=IN IP4 67.167.168.182\na=rtcp:49155 IN IP4 67.167.168.182\na=rtpmap:100 VP8/90000\na=ice-ufrag:c4emj1b3bn0qpq\na=ice-pwd:9t8kbjqbknhkchct525l1lj0h\na=ice-options:trickle\na=fingerprint:sha-256 1F:AB:6D:5A:56:70:37:00:83:F2:7F:FC:B4:3A:F4:74:8A:04:59:70:A2:4A:80:A5:30:79:56:60:2D:D1:8E:76\na=setup:passive\na=mid:video\na=extmap:2 urn:ietf:params:rtp-hdrext:toffset\na=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\na=extmap:4 urn:3gpp:video-orientation\na=recvonly\n";

    String chromeH264Answer = "v=0\no=- 9113310718382273926 2 IN IP4 127.0.0.1\ns=-\nt=0 0\na=msid-semantic: WMS\nm=video 9 UDP/TLS/RTP/SAVPF 126\nc=IN IP4 0.0.0.0\na=rtcp:9 IN IP4 0.0.0.0\na=ice-ufrag:e+xP\na=ice-pwd:xu3OIm2fhRIVQvsfQ28+cCjy\na=fingerprint:sha-256 B8:C3:DE:66:73:E6:EB:31:81:74:41:B1:C0:73:58:C6:8A:FC:CE:FF:6F:E9:3C:C8:69:59:12:AB:50:29:A8:69\na=setup:active\na=mid:video\na=extmap:2 urn:ietf:params:rtp-hdrext:toffset\na=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\na=extmap:4 urn:3gpp:video-orientation\na=recvonly\na=rtpmap:126 H264/90000\na=fmtp:126 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=42e01f";

    String H264fmtp = "a=fmtp:126 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=42e01f";

    String mozillaOffer50_audio_only = "v=0\no=mozilla...THIS_IS_SDPARTA-50.1.0 3282031449458449386 0 IN IP4 0.0.0.0\ns=-\nt=0 0\na=fingerprint:sha-256 C0:76:20:79:3C:EA:74:45:35:33:42:7F:0B:F4:37:E7:CF:22:7C:B8:21:35:D2:88:5C:E0:56:DF:CB:08:EC:88\na=ice-options:trickle\na=msid-semantic:WMS *\nm=audio 9 UDP/TLS/RTP/SAVPF 109 9 0 8\nc=IN IP4 0.0.0.0\na=sendonly\na=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\na=ice-pwd:0b610b3173b66a350d37fe97fc87d1f1\na=ice-ufrag:7c8cef93\na=mid:sdparta_0\na=msid:{34ace637-67d5-4943-9b38-0961dbf0039a} {8399b2ae-aee6-4305-861d-ad042d79995a}\na=rtcp-mux\na=rtpmap:109 opus/48000/2\na=rtpmap:9 G722/8000/1\na=rtpmap:0 PCMU/8000\na=rtpmap:8 PCMA/8000\na=setup:actpass\na=ssrc:1546053066 cname:{01847353-da29-479f-9331-cac5cb4b0a55}\n";

    String edgeOffer = "v=0\r\no=thisisadapterortc 8169639915646943137 2 IN IP4 127.0.0.1\r\ns=-\r\nt=0 0\r\na=group:BUNDLE 9p2wvtyahv t9c6mhpao7\r\na=ice-options:trickle\r\nm=audio 9 UDP/TLS/RTP/SAVPF 104 102 9 0 8 103 97 13 118 101\r\nc=IN IP4 0.0.0.0\r\na=rtcp:9 IN IP4 0.0.0.0\r\na=rtpmap:104 SILK/16000\r\na=rtcp-fb:104 x-message app send:dsh recv:dsh\r\na=rtpmap:102 opus/48000/2\r\na=rtcp-fb:102 x-message app send:dsh recv:dsh\r\na=rtpmap:9 G722/8000\r\na=rtcp-fb:9 x-message app send:dsh recv:dsh\r\na=rtpmap:0 PCMU/8000\r\na=rtcp-fb:0 x-message app send:dsh recv:dsh\r\na=rtpmap:8 PCMA/8000\r\na=rtcp-fb:8 x-message app send:dsh recv:dsh\r\na=rtpmap:103 SILK/8000\r\na=rtcp-fb:103 x-message app send:dsh recv:dsh\r\na=rtpmap:97 RED/8000\r\na=rtpmap:13 CN/8000\r\na=rtpmap:118 CN/16000\r\na=rtpmap:101 telephone-event/8000\r\na=fmtp:101 events=0-16\r\na=maxptime:100\r\na=rtcp-mux\r\na=extmap:1 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\r\na=extmap:3 http://skype.com/experiments/rtp-hdrext/fast_bandwidth_feedback#version_2\r\na=ice-ufrag:lMRF\r\na=ice-pwd:NkRjfmT4UzuYaOo/xIKa0iuT\r\na=setup:actpass\r\na=fingerprint:sha-256 09:0C:4F:FC:C6:54:B9:75:0D:CB:6D:91:9B:F2:9B:8C:BE:75:84:82:32:28:FE:DA:29:AE:50:53:1E:ED:D1:75\r\na=mid:9p2wvtyahv\r\na=sendrecv\r\na=msid:13E696D1-5F95-42F5-B149-887B0448FF19 F7DE95AA-B6D8-4242-882D-477CDE3660FF\r\na=ssrc:1001 msid:13E696D1-5F95-42F5-B149-887B0448FF19 F7DE95AA-B6D8-4242-882D-477CDE3660FF\r\na=ssrc:1001 cname:z1gldgcaba\r\na=rtcp-rsize\r\nm=video 9 UDP/TLS/RTP/SAVPF 122 107 100 99 96 123\r\nc=IN IP4 0.0.0.0\r\na=rtcp:9 IN IP4 0.0.0.0\r\na=rtpmap:122 X-H264UC/90000\r\na=fmtp:122 packetization-mode=1;mst-mode=NI-TC\r\na=rtcp-fb:122 x-message app send:src,x-pli recv:src,x-pli\r\na=rtpmap:107 H264/90000\r\na=fmtp:107 profile-level-id=42C02A;packetization-mode=1;level-asymmetry-allowed=1\r\na=rtcp-fb:107 nack\r\na=rtcp-fb:107 nack pli\r\na=rtcp-fb:107 goog-remb\r\na=rtpmap:100 VP8/90000\r\na=rtcp-fb:100 nack\r\na=rtcp-fb:100 nack pli\r\na=rtcp-fb:100 goog-remb\r\na=rtpmap:99 rtx/90000\r\na=fmtp:99 apt=107;rtx-time=3000\r\na=rtpmap:96 rtx/90000\r\na=fmtp:96 apt=100;rtx-time=3000\r\na=rtpmap:123 x-ulpfecuc/90000\r\na=rtcp-mux\r\na=extmap:1 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\r\na=extmap:3 http://skype.com/experiments/rtp-hdrext/fast_bandwidth_feedback#version_2\r\na=ice-ufrag:xmbm\r\na=ice-pwd:eCQ+vl3g5OeAoK4rPUdRYxb2\r\na=setup:actpass\r\na=fingerprint:sha-256 F2:85:9C:27:E1:22:EF:93:F0:63:D8:28:B0:AD:F1:A4:83:A7:05:F2:62:D6:EF:F4:2B:7A:A4:81:63:A3:D4:0E\r\na=mid:t9c6mhpao7\r\na=sendrecv\r\na=msid:13E696D1-5F95-42F5-B149-887B0448FF19 46D50AC9-1B33-4895-8196-CABB2501FF28\r\na=ssrc:3003 msid:13E696D1-5F95-42F5-B149-887B0448FF19 46D50AC9-1B33-4895-8196-CABB2501FF28\r\na=ssrc:3004 msid:13E696D1-5F95-42F5-B149-887B0448FF19 46D50AC9-1B33-4895-8196-CABB2501FF28\r\na=ssrc-group:FID 3003 3004\r\na=ssrc:3003 cname:z1gldgcaba\r\na=ssrc:3004 cname:z1gldgcaba\r\na=rtcp-rsize\r\n";

    String rawAnswer = "v=0\no=red5pro_edge 6613656658187 2 IN IP4 0.0.0.0\ns=-\nt=0 0\na=msid-semantic:WMS *\na=group:BUNDLE audio video\na=ice-options:trickle\nm=audio 9 UDP/TLS/RTP/SAVPF 102\nc=IN IP4 71.38.119.248\nb=AS:64\na=rtpmap:102 opus/48000/2\na=ice-ufrag:cu0a91cko22k4o\na=ice-pwd:3k67hkg2b654ga91ll0pme298f\na=fingerprint:sha-256 90:81:AE:AC:5F:AE:5E:08:EC:1C:1F:4F:52:41:B8:94:6B:F5:4D:41:09:8B:7D:0D:8B:E8:50:47:C9:C5:99:FD\na=setup:passive\na=mid:audio\na=recvonly\na=rtcp-mux\na=rtcp-rsize\na=fmtp:102 minptime=10;useinbandfec=0;cbr=1\nm=video 9 UDP/TLS/RTP/SAVPF 107\nc=IN IP4 71.38.119.248\nb=AS:256\na=fmtp:107 profile-level-id=42C02A;level-asymmetry-allowed=1;packetization-mode=1\na=rtpmap:107 H264/90000\na=ice-ufrag:cu0a91cko22k4o\na=ice-pwd:3k67hkg2b654ga91ll0pme298f\na=fingerprint:sha-256 90:81:AE:AC:5F:AE:5E:08:EC:1C:1F:4F:52:41:B8:94:6B:F5:4D:41:09:8B:7D:0D:8B:E8:50:47:C9:C5:99:FD\na=setup:passive\na=mid:video\na=recvonly\na=rtcp-mux\na=rtcp-rsize\na=rtcp-fb:107 nack\na=rtcp-fb:107 nack pli\na=rtcp-fb:107 goog-remb\n";

    // json
    String edgeSenderCapabilities = "{\"ice\":{\"usernameFragment\":\"bfpQ\",\"password\":\"24nW3bmt81X27fDLdboevA62\",\"iceLite\":false},\"dtls\":{\"role\":\"auto\",\"fingerprints\":[{\"algorithm\":\"sha-256\",\"value\":\"3F:91:9F:79:09:D8:51:15:B8:64:95:C6:5E:18:7F:74:76:47:4E:FC:D4:D8:F1:B3:67:B5:65:3B:07:9E:76:C9\"}]},\"sendAudioCaps\":{\"codecs\":[{\"name\":\"SILK\",\"kind\":\"audio\",\"clockRate\":16000,\"preferredPayloadType\":104,\"maxptime\":100,\"ptime\":20,\"numChannels\":1,\"rtcpFeedback\":[{\"type\":\"x-cinfo\",\"parameter\":\"\"},{\"type\":\"x-bwe\",\"parameter\":\"\"},{\"type\":\"x-message\",\"parameter\":\"app send:dsh recv:dsh\"}],\"parameters\":{},\"options\":{},\"maxTemporalLayers\":0,\"maxSpatialLayers\":0,\"svcMultiStreamSupport\":false},{\"name\":\"opus\",\"kind\":\"audio\",\"clockRate\":48000,\"preferredPayloadType\":102,\"maxptime\":60,\"ptime\":20,\"numChannels\":2,\"rtcpFeedback\":[{\"type\":\"x-cinfo\",\"parameter\":\"\"},{\"type\":\"x-bwe\",\"parameter\":\"\"},{\"type\":\"x-message\",\"parameter\":\"app send:dsh recv:dsh\"}],\"parameters\":{},\"options\":{},\"maxTemporalLayers\":0,\"maxSpatialLayers\":0,\"svcMultiStreamSupport\":false},{\"name\":\"PCMU\",\"kind\":\"audio\",\"clockRate\":8000,\"preferredPayloadType\":0,\"maxptime\":60,\"ptime\":20,\"numChannels\":1,\"rtcpFeedback\":[{\"type\":\"x-cinfo\",\"parameter\":\"\"},{\"type\":\"x-bwe\",\"parameter\":\"\"},{\"type\":\"x-message\",\"parameter\":\"app send:dsh recv:dsh\"}],\"parameters\":{},\"options\":{},\"maxTemporalLayers\":0,\"maxSpatialLayers\":0,\"svcMultiStreamSupport\":false},{\"name\":\"PCMA\",\"kind\":\"audio\",\"clockRate\":8000,\"preferredPayloadType\":8,\"maxptime\":60,\"ptime\":20,\"numChannels\":1,\"rtcpFeedback\":[{\"type\":\"x-cinfo\",\"parameter\":\"\"},{\"type\":\"x-bwe\",\"parameter\":\"\"},{\"type\":\"x-message\",\"parameter\":\"app send:dsh recv:dsh\"}],\"parameters\":{},\"options\":{},\"maxTemporalLayers\":0,\"maxSpatialLayers\":0,\"svcMultiStreamSupport\":false},{\"name\":\"SILK\",\"kind\":\"audio\",\"clockRate\":8000,\"preferredPayloadType\":103,\"maxptime\":60,\"ptime\":20,\"numChannels\":1,\"rtcpFeedback\":[{\"type\":\"x-cinfo\",\"parameter\":\"\"},{\"type\":\"x-bwe\",\"parameter\":\"\"},{\"type\":\"x-message\",\"parameter\":\"app send:dsh recv:dsh\"}],\"parameters\":{},\"options\":{},\"maxTemporalLayers\":0,\"maxSpatialLayers\":0,\"svcMultiStreamSupport\":false},{\"name\":\"RED\",\"kind\":\"audio\",\"clockRate\":8000,\"preferredPayloadType\":97,\"maxptime\":0,\"ptime\":0,\"numChannels\":1,\"rtcpFeedback\":[],\"parameters\":{},\"options\":{},\"maxTemporalLayers\":0,\"maxSpatialLayers\":0,\"svcMultiStreamSupport\":false},{\"name\":\"CN\",\"kind\":\"audio\",\"clockRate\":8000,\"preferredPayloadType\":13,\"maxptime\":0,\"ptime\":0,\"numChannels\":1,\"rtcpFeedback\":[],\"parameters\":{},\"options\":{},\"maxTemporalLayers\":0,\"maxSpatialLayers\":0,\"svcMultiStreamSupport\":false},{\"name\":\"CN\",\"kind\":\"audio\",\"clockRate\":16000,\"preferredPayloadType\":118,\"maxptime\":0,\"ptime\":0,\"numChannels\":1,\"rtcpFeedback\":[],\"parameters\":{},\"options\":{},\"maxTemporalLayers\":0,\"maxSpatialLayers\":0,\"svcMultiStreamSupport\":false},{\"name\":\"telephone-event\",\"kind\":\"audio\",\"clockRate\":8000,\"preferredPayloadType\":101,\"maxptime\":0,\"ptime\":0,\"numChannels\":1,\"rtcpFeedback\":[],\"parameters\":{\"events\":\"0-16\"},\"options\":{},\"maxTemporalLayers\":0,\"maxSpatialLayers\":0,\"svcMultiStreamSupport\":false}],\"headerExtensions\":[{\"kind\":\"audio\",\"uri\":\"http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\",\"preferredId\":1,\"preferredEncrypt\":false},{\"kind\":\"audio\",\"uri\":\"http://skype.com/experiments/rtp-hdrext/fast_bandwidth_feedback#version_2\",\"preferredId\":3,\"preferredEncrypt\":false}],\"fecMechanisms\":[\"RED\"]},\"sendVideoCaps\":{\"codecs\":[{\"name\":\"X-H264UC\",\"kind\":\"video\",\"clockRate\":90000,\"preferredPayloadType\":122,\"maxptime\":0,\"ptime\":0,\"numChannels\":1,\"rtcpFeedback\":[{\"type\":\"x-cinfo\",\"parameter\":\"\"},{\"type\":\"x-bwe\",\"parameter\":\"\"},{\"type\":\"x-message\",\"parameter\":\"app send:src,x-pli recv:src,x-pli\"}],\"parameters\":{\"packetization-mode\":\"1\",\"mst-mode\":\"NI-TC\"},\"options\":{},\"maxTemporalLayers\":3,\"maxSpatialLayers\":0,\"svcMultiStreamSupport\":true},{\"name\":\"H264\",\"kind\":\"video\",\"clockRate\":90000,\"preferredPayloadType\":107,\"maxptime\":0,\"ptime\":0,\"numChannels\":1,\"rtcpFeedback\":[{\"type\":\"x-cinfo\",\"parameter\":\"\"},{\"type\":\"x-bwe\",\"parameter\":\"\"},{\"type\":\"nack\",\"parameter\":\"\"},{\"type\":\"nack\",\"parameter\":\"pli\"},{\"type\":\"goog-remb\",\"parameter\":\"\"}],\"parameters\":{\"profile-level-id\":\"42C02A\",\"packetization-mode\":\"1\"},\"options\":{},\"maxTemporalLayers\":3,\"maxSpatialLayers\":0,\"svcMultiStreamSupport\":false},{\"name\":\"VP8\",\"kind\":\"video\",\"clockRate\":90000,\"preferredPayloadType\":100,\"maxptime\":0,\"ptime\":0,\"numChannels\":1,\"rtcpFeedback\":[{\"type\":\"x-cinfo\",\"parameter\":\"\"},{\"type\":\"x-bwe\",\"parameter\":\"\"},{\"type\":\"nack\",\"parameter\":\"\"},{\"type\":\"nack\",\"parameter\":\"pli\"},{\"type\":\"goog-remb\",\"parameter\":\"\"}],\"parameters\":{},\"options\":{},\"maxTemporalLayers\":0,\"maxSpatialLayers\":0,\"svcMultiStreamSupport\":false},{\"name\":\"rtx\",\"kind\":\"video\",\"clockRate\":90000,\"preferredPayloadType\":99,\"maxptime\":0,\"ptime\":0,\"numChannels\":1,\"rtcpFeedback\":[],\"parameters\":{\"apt\":\"107\",\"rtx-time\":\"3000\"},\"options\":{},\"maxTemporalLayers\":0,\"maxSpatialLayers\":0,\"svcMultiStreamSupport\":false},{\"name\":\"rtx\",\"kind\":\"video\",\"clockRate\":90000,\"preferredPayloadType\":96,\"maxptime\":0,\"ptime\":0,\"numChannels\":1,\"rtcpFeedback\":[],\"parameters\":{\"apt\":\"100\",\"rtx-time\":\"3000\"},\"options\":{},\"maxTemporalLayers\":0,\"maxSpatialLayers\":0,\"svcMultiStreamSupport\":false},{\"name\":\"x-ulpfecuc\",\"kind\":\"video\",\"clockRate\":90000,\"preferredPayloadType\":123,\"maxptime\":0,\"ptime\":0,\"numChannels\":1,\"rtcpFeedback\":[],\"parameters\":{},\"options\":{},\"maxTemporalLayers\":0,\"maxSpatialLayers\":0,\"svcMultiStreamSupport\":false}],\"headerExtensions\":[{\"kind\":\"video\",\"uri\":\"http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\",\"preferredId\":1,\"preferredEncrypt\":false},{\"kind\":\"video\",\"uri\":\"http://skype.com/experiments/rtp-hdrext/fast_bandwidth_feedback#version_2\",\"preferredId\":3,\"preferredEncrypt\":false}],\"fecMechanisms\":[]}}";

    // RPRO-6702
    String srtpOfferIOS = "v=0\no=- 1957747793 1957747793 IN IP4 stretchwest2.red5.org\ns=jSKiXRencrypt\nc=IN IP4 0.0.0.0\na=metadata:orientation=90;resolution=480,368;v-width=368;v-height=480;r5probuild=5.6.4.0;\nt=0 0\na=control:*\na=crypto:1 AES_CM_128_HMAC_SHA1_32 inline:a94uHpX0EdzQSd6y/Xic0Iz5aaQ=|SHA1\nm=video 0 RTP/AVP 96\nb=TIAS:0\na=maxprate:1.0000\na=control:video\na=rtpmap:96 H264/90000\na=mimetype:string;\"video/H264\"\na=framesize:96 480-368\na=Width:integer;480\na=Height:integer;368\na=fmtp:96 packetization-mode=1;profile-level-id=640015;sprop-parameter-sets=J2QAFaxWgeC/5ZqAgIMB,KO48sA==\nm=audio 0 RTP/AVP 97\na=rtpmap:97 MPEG4-GENERIC/16000/1\na=fmtp:97 ;profile-level-id=1;mode=AAC-hbr;sizelength=13;indexlength=3;indexdeltalength=3;constantDuration=1024;config=0808;\na=control:audio\n";

    String safariBadOrder = "v=0\no=- 1219919239376771942 2 IN IP4 127.0.0.1\ns=-\nt=0 0\na=group:BUNDLE 0 1\na=msid-semantic: WMS a5514857-0509-42df-9b25-5cb2de2c3b90\nm=video 9 UDP/TLS/RTP/SAVPF 96 97 98 99 100 101 127 125 104\nb=AS:750\nc=IN IP4 0.0.0.0\na=rtcp:9 IN IP4 0.0.0.0\na=ice-ufrag:7RLH\na=ice-pwd:s91BqZp2fj6daRUz9dv4akUS\na=ice-options:trickle\na=fingerprint:sha-256 46:63:34:6D:B8:26:5A:55:6A:B5:51:BC:56:1C:F9:C8:E3:E1:1A:CC:28:5F:3E:CB:2D:39:F6:80:81:5F:2F:F8\na=setup:actpass\na=mid:0\na=extmap:2 urn:ietf:params:rtp-hdrext:toffset\na=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\na=extmap:4 urn:3gpp:video-orientation\na=extmap:5 http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01\na=extmap:6 http://www.webrtc.org/experiments/rtp-hdrext/playout-delay\na=extmap:7 http://www.webrtc.org/experiments/rtp-hdrext/video-content-type\na=extmap:8 http://www.webrtc.org/experiments/rtp-hdrext/video-timing\na=extmap:10 http://tools.ietf.org/html/draft-ietf-avtext-framemarking-07\na=extmap:9 urn:ietf:params:rtp-hdrext:sdes:mid\na=sendrecv\na=msid:a5514857-0509-42df-9b25-5cb2de2c3b90 c7c47029-1bbb-483c-8d9a-f509587237f8\na=rtcp-mux\na=rtcp-rsize\na=rtpmap:96 H264/90000\na=rtcp-fb:96 goog-remb\na=rtcp-fb:96 transport-cc\na=rtcp-fb:96 ccm fir\na=rtcp-fb:96 nack\na=rtcp-fb:96 nack pli\na=fmtp:96 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=640c1f\na=rtpmap:97 rtx/90000\na=fmtp:97 apt=96\na=rtpmap:98 H264/90000\na=rtcp-fb:98 goog-remb\na=rtcp-fb:98 transport-cc\na=rtcp-fb:98 ccm fir\na=rtcp-fb:98 nack\na=rtcp-fb:98 nack pli\na=fmtp:98 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=42e01f\na=rtpmap:99 rtx/90000\na=fmtp:99 apt=98\na=rtpmap:100 VP8/90000\na=rtcp-fb:100 goog-remb\na=rtcp-fb:100 transport-cc\na=rtcp-fb:100 ccm fir\na=rtcp-fb:100 nack\na=rtcp-fb:100 nack pli\na=rtpmap:101 rtx/90000\na=fmtp:101 apt=100\na=rtpmap:127 red/90000\na=rtpmap:125 rtx/90000\na=fmtp:125 apt=127\na=rtpmap:104 ulpfec/90000\na=ssrc-group:FID 2923400095 3096525437\na=ssrc:2923400095 cname:xvXpVR2A7P3sf1xo\na=ssrc:2923400095 msid:a5514857-0509-42df-9b25-5cb2de2c3b90 c7c47029-1bbb-483c-8d9a-f509587237f8\na=ssrc:2923400095 mslabel:a5514857-0509-42df-9b25-5cb2de2c3b90\na=ssrc:2923400095 label:c7c47029-1bbb-483c-8d9a-f509587237f8\na=ssrc:3096525437 cname:xvXpVR2A7P3sf1xo\na=ssrc:3096525437 msid:a5514857-0509-42df-9b25-5cb2de2c3b90 c7c47029-1bbb-483c-8d9a-f509587237f8\na=ssrc:3096525437 mslabel:a5514857-0509-42df-9b25-5cb2de2c3b90\na=ssrc:3096525437 label:c7c47029-1bbb-483c-8d9a-f509587237f8\nm=audio 9 UDP/TLS/RTP/SAVPF 111 103 9 102 0 8 105 13 110 113 126\nb=AS:56\nc=IN IP4 0.0.0.0\na=rtcp:9 IN IP4 0.0.0.0\na=ice-ufrag:7RLH\na=ice-pwd:s91BqZp2fj6daRUz9dv4akUS\na=ice-options:trickle\na=fingerprint:sha-256 46:63:34:6D:B8:26:5A:55:6A:B5:51:BC:56:1C:F9:C8:E3:E1:1A:CC:28:5F:3E:CB:2D:39:F6:80:81:5F:2F:F8\na=setup:actpass\na=mid:1\na=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\na=extmap:9 urn:ietf:params:rtp-hdrext:sdes:mid\na=sendrecv\na=msid:a5514857-0509-42df-9b25-5cb2de2c3b90 97971c44-eadd-4caf-8c29-a5b80acef548\na=rtcp-mux\na=rtpmap:111 opus/48000/2\na=rtcp-fb:111 transport-cc\na=fmtp:111 minptime=10;useinbandfec=1\na=rtpmap:103 ISAC/16000\na=rtpmap:9 G722/8000\na=rtpmap:102 ILBC/8000\na=rtpmap:0 PCMU/8000\na=rtpmap:8 PCMA/8000\na=rtpmap:105 CN/16000\na=rtpmap:13 CN/8000\na=rtpmap:110 telephone-event/48000\na=rtpmap:113 telephone-event/16000\na=rtpmap:126 telephone-event/8000\na=ssrc:358736669 cname:xvXpVR2A7P3sf1xo\na=ssrc:358736669 msid:a5514857-0509-42df-9b25-5cb2de2c3b90 97971c44-eadd-4caf-8c29-a5b80acef548\na=ssrc:358736669 mslabel:a5514857-0509-42df-9b25-5cb2de2c3b90\na=ssrc:358736669 label:97971c44-eadd-4caf-8c29-a5b80acef548\n";

    // RPRO-6991 mac safari and chrome 78 failing
    String macMultiCodecs = "v=0\no=- 6247228922311429380 2 IN IP4 127.0.0.1\ns=-\nt=0 0\na=group:BUNDLE 0 1\na=msid-semantic: WMS 4E854lS6BxzK2zhgB7MkR2jDXVe6aYqOlgy2\nm=audio 9 UDP/TLS/RTP/SAVPF 111 103 104 9 0 8 106 105 13 110 112 113 126\nb=AS:56\nc=IN IP4 0.0.0.0\na=rtcp:9 IN IP4 0.0.0.0\na=ice-ufrag:5co+\na=ice-pwd:sIcBiHAoc4X2N7GzGrfMlAgi\na=ice-options:trickle\na=fingerprint:sha-256 DD:01:EA:95:DA:B4:A0:BB:B2:DC:31:FE:CA:C2:4C:FB:7E:8C:13:BA:98:0C:EE:09:A0:98:9E:69:FD:F9:19:6D\na=setup:actpass\na=mid:0\na=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\na=extmap:2 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\na=extmap:3 http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01\na=extmap:4 urn:ietf:params:rtp-hdrext:sdes:mid\na=extmap:5 urn:ietf:params:rtp-hdrext:sdes:rtp-stream-id\na=extmap:6 urn:ietf:params:rtp-hdrext:sdes:repaired-rtp-stream-id\na=sendrecv\na=msid:4E854lS6BxzK2zhgB7MkR2jDXVe6aYqOlgy2 6a2a23c4-931a-4037-a84a-4754bcdfdd5c\na=rtcp-mux\na=rtpmap:111 opus/48000/2\na=rtcp-fb:111 transport-cc\na=fmtp:111 minptime=10;useinbandfec=1\na=rtpmap:103 ISAC/16000\na=rtpmap:104 ISAC/32000\na=rtpmap:9 G722/8000\na=rtpmap:0 PCMU/8000\na=rtpmap:8 PCMA/8000\na=rtpmap:106 CN/32000\na=rtpmap:105 CN/16000\na=rtpmap:13 CN/8000\na=rtpmap:110 telephone-event/48000\na=rtpmap:112 telephone-event/32000\na=rtpmap:113 telephone-event/16000\na=rtpmap:126 telephone-event/8000\na=ssrc:4146099177 cname:sGc/DQJoz89H9hYR\na=ssrc:4146099177 msid:4E854lS6BxzK2zhgB7MkR2jDXVe6aYqOlgy2 6a2a23c4-931a-4037-a84a-4754bcdfdd5c\na=ssrc:4146099177 mslabel:4E854lS6BxzK2zhgB7MkR2jDXVe6aYqOlgy2\na=ssrc:4146099177 label:6a2a23c4-931a-4037-a84a-4754bcdfdd5c\nm=video 9 UDP/TLS/RTP/SAVPF 96 97 98 99 100 101 102 122 127 121 125 107 108 109 124 120 123 119 114 115 116\nb=AS:750\nc=IN IP4 0.0.0.0\na=rtcp:9 IN IP4 0.0.0.0\na=ice-ufrag:5co+\na=ice-pwd:sIcBiHAoc4X2N7GzGrfMlAgi\na=ice-options:trickle\na=fingerprint:sha-256 DD:01:EA:95:DA:B4:A0:BB:B2:DC:31:FE:CA:C2:4C:FB:7E:8C:13:BA:98:0C:EE:09:A0:98:9E:69:FD:F9:19:6D\na=setup:actpass\na=mid:1\na=extmap:14 urn:ietf:params:rtp-hdrext:toffset\na=extmap:2 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\na=extmap:13 urn:3gpp:video-orientation\na=extmap:3 http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01\na=extmap:12 http://www.webrtc.org/experiments/rtp-hdrext/playout-delay\na=extmap:11 http://www.webrtc.org/experiments/rtp-hdrext/video-content-type\na=extmap:7 http://www.webrtc.org/experiments/rtp-hdrext/video-timing\na=extmap:8 http://tools.ietf.org/html/draft-ietf-avtext-framemarking-07\na=extmap:9 http://www.webrtc.org/experiments/rtp-hdrext/color-space\na=extmap:4 urn:ietf:params:rtp-hdrext:sdes:mid\na=extmap:5 urn:ietf:params:rtp-hdrext:sdes:rtp-stream-id\na=extmap:6 urn:ietf:params:rtp-hdrext:sdes:repaired-rtp-stream-id\na=sendrecv\na=msid:4E854lS6BxzK2zhgB7MkR2jDXVe6aYqOlgy2 9f0e66ab-ef70-4997-ae38-48c727b4f5ee\na=rtcp-mux\na=rtcp-rsize\na=rtpmap:96 VP8/90000\na=rtcp-fb:96 goog-remb\na=rtcp-fb:96 transport-cc\na=rtcp-fb:96 ccm fir\na=rtcp-fb:96 nack\na=rtcp-fb:96 nack pli\na=rtpmap:97 rtx/90000\na=fmtp:97 apt=96\na=rtpmap:98 VP9/90000\na=rtcp-fb:98 goog-remb\na=rtcp-fb:98 transport-cc\na=rtcp-fb:98 ccm fir\na=rtcp-fb:98 nack\na=rtcp-fb:98 nack pli\na=fmtp:98 profile-id=0\na=rtpmap:99 rtx/90000\na=fmtp:99 apt=98\na=rtpmap:100 VP9/90000\na=rtcp-fb:100 goog-remb\na=rtcp-fb:100 transport-cc\na=rtcp-fb:100 ccm fir\na=rtcp-fb:100 nack\na=rtcp-fb:100 nack pli\na=fmtp:100 profile-id=2\na=rtpmap:101 rtx/90000\na=fmtp:101 apt=100\na=rtpmap:102 H264/90000\na=rtcp-fb:102 goog-remb\na=rtcp-fb:102 transport-cc\na=rtcp-fb:102 ccm fir\na=rtcp-fb:102 nack\na=rtcp-fb:102 nack pli\na=fmtp:102 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=42001f\na=rtpmap:122 rtx/90000\na=fmtp:122 apt=102\na=rtpmap:127 H264/90000\na=rtcp-fb:127 goog-remb\na=rtcp-fb:127 transport-cc\na=rtcp-fb:127 ccm fir\na=rtcp-fb:127 nack\na=rtcp-fb:127 nack pli\na=fmtp:127 level-asymmetry-allowed=1;packetization-mode=0;profile-level-id=42001f\na=rtpmap:121 rtx/90000\na=fmtp:121 apt=127\na=rtpmap:125 H264/90000\na=rtcp-fb:125 goog-remb\na=rtcp-fb:125 transport-cc\na=rtcp-fb:125 ccm fir\na=rtcp-fb:125 nack\na=rtcp-fb:125 nack pli\na=fmtp:125 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=42e01f\na=rtpmap:107 rtx/90000\na=fmtp:107 apt=125\na=rtpmap:108 H264/90000\na=rtcp-fb:108 goog-remb\na=rtcp-fb:108 transport-cc\na=rtcp-fb:108 ccm fir\na=rtcp-fb:108 nack\na=rtcp-fb:108 nack pli\na=fmtp:108 level-asymmetry-allowed=1;packetization-mode=0;profile-level-id=42e01f\na=rtpmap:109 rtx/90000\na=fmtp:109 apt=108\na=rtpmap:124 H264/90000\na=rtcp-fb:124 goog-remb\na=rtcp-fb:124 transport-cc\na=rtcp-fb:124 ccm fir\na=rtcp-fb:124 nack\na=rtcp-fb:124 nack pli\na=fmtp:124 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=4d0032\na=rtpmap:120 rtx/90000\na=fmtp:120 apt=124\na=rtpmap:123 H264/90000\na=rtcp-fb:123 goog-remb\na=rtcp-fb:123 transport-cc\na=rtcp-fb:123 ccm fir\na=rtcp-fb:123 nack\na=rtcp-fb:123 nack pli\na=fmtp:123 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=640032\na=rtpmap:119 rtx/90000\na=fmtp:119 apt=123\na=rtpmap:114 red/90000\na=rtpmap:115 rtx/90000\na=fmtp:115 apt=114\na=rtpmap:116 ulpfec/90000\na=ssrc-group:FID 3846131911 1678392173\na=ssrc:3846131911 cname:sGc/DQJoz89H9hYR\na=ssrc:3846131911 msid:4E854lS6BxzK2zhgB7MkR2jDXVe6aYqOlgy2 9f0e66ab-ef70-4997-ae38-48c727b4f5ee\na=ssrc:3846131911 mslabel:4E854lS6BxzK2zhgB7MkR2jDXVe6aYqOlgy2\na=ssrc:3846131911 label:9f0e66ab-ef70-4997-ae38-48c727b4f5ee\na=ssrc:1678392173 cname:sGc/DQJoz89H9hYR\na=ssrc:1678392173 msid:4E854lS6BxzK2zhgB7MkR2jDXVe6aYqOlgy2 9f0e66ab-ef70-4997-ae38-48c727b4f5ee\na=ssrc:1678392173 mslabel:4E854lS6BxzK2zhgB7MkR2jDXVe6aYqOlgy2\na=ssrc:1678392173 label:9f0e66ab-ef70-4997-ae38-48c727b4f5ee\n";

    // chrome datachannel only sdp
    String chromeDataChannelOnly = "v=0\no=- 8554465656018336221 2 IN IP4 127.0.0.1\ns=-\nt=0 0\na=group:BUNDLE data\na=msid-semantic: WMS\nm=application 1 DTLS/SCTP 5000\nc=IN IP4 0.0.0.0\na=ice-ufrag:Vw+winZTN4ejhvQJ\na=ice-pwd:ufBTUw/iszvCbL53dmPHQAYK\na=ice-options:google-ice\na=fingerprint:sha-256 5C:C6:19:38:4D:54:57:71:16:3F:67:A6:C8:21:CC:29:88:85:22:86:53:E5:7B:3F:3D:A4:5C:E5:BC:29:D8:B5\na=setup:actpass\na=mid:data\na=sctpmap:5000 webrtc-datachannel 1024\n";

    String chromeDataChannelOnly2 = "v=0\no=- 2515726126916136913 2 IN IP4 127.0.0.1\ns=-\nt=0 0\na=group:BUNDLE 0\na=msid-semantic: WMS\nm=application 9 UDP/DTLS/SCTP webrtc-datachannel\nc=IN IP4 0.0.0.0\nb=AS:30\na=ice-ufrag:Hvek\na=ice-pwd:Uep8OG90b1PWVgex6ubBNLFw\na=ice-options:trickle\na=fingerprint:sha-256 37:D5:54:0A:C9:8A:4A:31:CF:04:27:24:FE:24:7C:1C:3F:10:41:6D:13:74:A2:F0:29:14:BA:CF:EA:A5:A1:31\na=setup:active\na=mid:0\na=sctp-port:5000\na=max-message-size:262144\n";

    String androidMobileSDK = "v=0\ns=Media Presentation\nc=IN IP4 0.0.0.0\nb=as:256000\na=metadata:orientation=270;v-height=640;v-width=360;r5probuild=6.0.0.RC6;resolution=640,360;\nt=0 0\na=control:*\nm=audio 0 RTP/AVP/TCP 96 \na=rtpmap:96 AAC/44100/1\na=fmtp:96 mode=AAC-hbr; sizelength=13; profile-level-id=1; indexdeltalength=3; indexlength=3;\na=control:audio\nm=video 0 RTP/AVP 97\na=rtpmap:97 H264/90000\na=fmtp:97 sprop-parameter-sets=Z0KACtoCgL/lgG0KE1A=,aM4G8g==; packetization-mode=1;\na=control:video\n";

    String chromeConfOffer = "v=0\no=- 5436823209281967603 2 IN IP4 127.0.0.1\ns=-\nt=0 0\na=group:BUNDLE audio video data\na=msid-semantic: WMS\nm=audio 9 UDP/TLS/RTP/SAVPF 111\nc=IN IP4 0.0.0.0\na=rtcp:9 IN IP4 0.0.0.0\na=ice-ufrag:D759\na=ice-pwd:zadjPOhoIIwJ2BtboQplAhxI\na=ice-options:trickle\na=fingerprint:sha-256 11:D7:93:1E:7B:1E:48:7E:67:CD:78:84:2A:54:5F:AD:0E:86:96:56:17:65:CD:B6:5A:C7:20:66:3D:23:0B:5D\na=setup:actpass\na=mid:audio\na=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\na=extmap:2 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\na=recvonly\na=rtcp-mux\na=rtpmap:111 opus/48000/2\na=fmtp:111 minptime=10;useinbandfec=1\nm=video 9 UDP/TLS/RTP/SAVPF 96 102\nc=IN IP4 0.0.0.0\na=rtcp:9 IN IP4 0.0.0.0\na=ice-ufrag:D759\na=ice-pwd:zadjPOhoIIwJ2BtboQplAhxI\na=ice-options:trickle\na=fingerprint:sha-256 11:D7:93:1E:7B:1E:48:7E:67:CD:78:84:2A:54:5F:AD:0E:86:96:56:17:65:CD:B6:5A:C7:20:66:3D:23:0B:5D\na=setup:actpass\na=mid:video\na=extmap:14 urn:ietf:params:rtp-hdrext:toffset\na=extmap:2 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\na=extmap:13 urn:3gpp:video-orientation\na=extmap:3 http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01\na=extmap:5 http://www.webrtc.org/experiments/rtp-hdrext/playout-delay\na=extmap:6 http://www.webrtc.org/experiments/rtp-hdrext/video-content-type\na=extmap:7 http://www.webrtc.org/experiments/rtp-hdrext/video-timing\na=extmap:8 http://www.webrtc.org/experiments/rtp-hdrext/color-space\na=recvonly\na=rtcp-mux\na=rtcp-rsize\na=rtpmap:96 VP8/90000\na=rtcp-fb:96 goog-remb\na=rtcp-fb:96 nack\na=rtcp-fb:96 nack pli\na=rtpmap:102 H264/90000\na=rtcp-fb:102 goog-remb\na=rtcp-fb:102 nack\na=rtcp-fb:102 nack pli\na=fmtp:102 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=42001f\nm=application 9 UDP/DTLS/SCTP webrtc-datachannel\nc=IN IP4 0.0.0.0\na=ice-ufrag:D759\na=ice-pwd:zadjPOhoIIwJ2BtboQplAhxI\na=ice-options:trickle\na=fingerprint:sha-256 11:D7:93:1E:7B:1E:48:7E:67:CD:78:84:2A:54:5F:AD:0E:86:96:56:17:65:CD:B6:5A:C7:20:66:3D:23:0B:5D\na=setup:actpass\na=mid:data\na=sctp-port:5000\na=max-message-size:262144\n";

    String chromeConfAnswer = "v=0\no=- 938508758749976 2 IN IP4 127.0.0.1\ns=-\nt=0 0\na=group:BUNDLE audio video data\na=ice-options:trickle\na=msid-semantic:WMS *\nm=audio 9 UDP/TLS/RTP/SAVPF 111\nc=IN IP4 0.0.0.0\na=rtcp:9 IN IP4 0.0.0.0\na=rtcp-mux\na=mid:audio\na=sendrecv\na=x-google-flag:conference\na=ice-ufrag:W8oVBC0TAWS1AC8z\na=ice-pwd:lyp1KvOi+n02woy6q/lUjB+P\na=fingerprint:sha-256 81:9F:2B:E5:3F:DA:85:40:87:73:B4:B2:6F:20:32:7C:2A:7E:DC:CB:D1:F5:0A:95:BF:21:36:67:9E:0F:1D:80\na=setup:active\na=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\na=extmap:2 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\na=rtpmap:111 opus/48000/2\na=fmtp:111 minptime=10; useinbandfec=1\na=ssrc:6666 cname:6666\na=ssrc:6666 msid:6666 6666\na=ssrc:6667 cname:6667\na=ssrc:6667 msid:6667 6667\na=ssrc:6668 cname:6668\na=ssrc:6668 msid:6668 6668\nm=video 9 UDP/TLS/RTP/SAVPF 102\nc=IN IP4 0.0.0.0\na=rtcp:9 IN IP4 0.0.0.0\na=rtcp-mux\na=mid:video\na=sendrecv\na=x-google-flag:conference\na=ice-ufrag:W8oVBC0TAWS1AC8z\na=ice-pwd:lyp1KvOi+n02woy6q/lUjB+P\na=fingerprint:sha-256 81:9F:2B:E5:3F:DA:85:40:87:73:B4:B2:6F:20:32:7C:2A:7E:DC:CB:D1:F5:0A:95:BF:21:36:67:9E:0F:1D:80\na=setup:active\na=extmap:2 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\na=extmap:13 urn:3gpp:video-orientation\na=rtpmap:102 H264/90000\na=rtcp-fb:102 goog-remb\na=rtcp-fb:102 nack\na=rtcp-fb:102 nack pli\nm=application 9 DTLS/SCTP 5000\nc=IN IP4 0.0.0.0\na=rtcp:9 IN IP4 0.0.0.0\na=rtcp-mux\na=mid:data\na=sendrecv\na=x-google-flag:conference\na=ice-ufrag:W8oVBC0TAWS1AC8z\na=ice-pwd:lyp1KvOi+n02woy6q/lUjB+P\na=fingerprint:sha-256 81:9F:2B:E5:3F:DA:85:40:87:73:B4:B2:6F:20:32:7C:2A:7E:DC:CB:D1:F5:0A:95:BF:21:36:67:9E:0F:1D:80\na=setup:active\na=sctpmap:5000 webrtc-datachannel 256\n";

    // chrome enhanced sdp with lcevc
    String chromeEnhancedOffer = "v=0\no=- 469656218519424161 2 IN IP4 127.0.0.1\ns=-\nt=0 0\na=msid-semantic:WMS *\na=group:BUNDLE 0 1 2\na=extmapallowmixed\nm=audio 9 UDP/TLS/RTP/SAVPF 111 63 103 104 9 0 8 106 105 13 110 112 113 126\nc=IN IP4 0.0.0.0\nb=AS:56\na=rtcp:9 IN IP4 0.0.0.0\na=ice-ufrag:X6Fg\na=ice-pwd:93KmLVN8mLrbF97Ec1VesUEz\na=ice-options:trickle\na=fingerprint:sha-256 97:4A:95:96:C9:61:D9:CA:A2:64:72:56:25:34:45:58:C0:2B:9E:80:1E:34:F6:40:74:F2:6D:48:B4:9F:FA:59\na=setup:actpass\na=mid:0\na=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\na=extmap:2 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\na=extmap:3 http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01\na=extmap:4 urn:ietf:params:rtp-hdrext:sdes:mid\na=extmap:5 urn:ietf:params:rtp-hdrext:sdes:rtp-stream-id\na=extmap:6 urn:ietf:params:rtp-hdrext:sdes:repaired-rtp-stream-id\na=sendrecv\na=msid:FeCJBlD4hGKgTb7cU9n8cMNK6JV5wgoxMJJc 772030df-3485-4f83-bef7-69679906ad20\na=rtcp-mux\na=rtpmap:111 opus/48000/2\na=rtcp-fb:111 transport-cc\na=fmtp:111 minptime=10;useinbandfec=1\na=rtpmap:63 red/48000/2\na=rtpmap:103 ISAC/16000\na=rtpmap:104 ISAC/32000\na=rtpmap:9 G722/8000\na=rtpmap:8 PCMA/8000\na=rtpmap:106 CN/32000\na=rtpmap:105 CN/16000\na=rtpmap:13 CN/8000\na=rtpmap:110 telephone-event/48000\na=rtpmap:112 telephone-event/32000\na=rtpmap:113 telephone-event/16000\na=rtpmap:126 telephone-event/8000\na=ssrc:3554725415 cname:ok+GGReoOnQ2Rydt\na=ssrc:3554725415 msid:FeCJBlD4hGKgTb7cU9n8cMNK6JV5wgoxMJJc 772030df-3485-4f83-bef7-69679906ad20\na=ssrc:3554725415 mslabel:FeCJBlD4hGKgTb7cU9n8cMNK6JV5wgoxMJJc\na=ssrc:3554725415 label:772030df-3485-4f83-bef7-69679906ad20\nm=video 9 UDP/TLS/RTP/SAVPF 96 97 98 99 100 101 102 109 127 107 125 62 108 61 124 60 123 59 114 58 116 57 118 56 120 55 122 54 35 36 121 53 119 37 38 39 40\nc=IN IP4 0.0.0.0\nb=AS:750\na=rtcp:9 IN IP4 0.0.0.0\na=ice-ufrag:X6Fg\na=ice-pwd:93KmLVN8mLrbF97Ec1VesUEz\na=ice-options:trickle\na=fingerprint:sha-256 97:4A:95:96:C9:61:D9:CA:A2:64:72:56:25:34:45:58:C0:2B:9E:80:1E:34:F6:40:74:F2:6D:48:B4:9F:FA:59\na=setup:actpass\na=mid:1\na=extmap:14 urn:ietf:params:rtp-hdrext:toffset\na=extmap:2 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\na=extmap:13 urn:3gpp:video-orientation\na=extmap:3 http://www.ietf.org/id/draft-holmer-rmcat-transport-wide-cc-extensions-01\na=extmap:12 http://www.webrtc.org/experiments/rtp-hdrext/playout-delay\na=extmap:11 http://www.webrtc.org/experiments/rtp-hdrext/video-content-type\na=extmap:7 http://www.webrtc.org/experiments/rtp-hdrext/video-timing\na=extmap:8 http://www.webrtc.org/experiments/rtp-hdrext/color-space\na=extmap:4 urn:ietf:params:rtp-hdrext:sdes:mid\na=extmap:5 urn:ietf:params:rtp-hdrext:sdes:rtp-stream-id\na=extmap:6 urn:ietf:params:rtp-hdrext:sdes:repaired-rtp-stream-id\na=sendrecv\na=msid:FeCJBlD4hGKgTb7cU9n8cMNK6JV5wgoxMJJc fd1fdb0c-4cc3-419a-9d69-cfe1763ff2b6\na=rtcp-mux\na=rtcp-rsize\na=rtpmap:96 LCEVCVP8/90000\na=rtcp-fb:96 goog-remb\na=rtcp-fb:96 transport-cc\na=rtcp-fb:96 ccm fir\na=rtcp-fb:96 nack\na=rtcp-fb:96 nack pli\na=rtpmap:97 rtx/90000\na=fmtp:97 apt=96\na=rtpmap:98 VP8/90000\na=rtcp-fb:98 goog-remb\na=rtcp-fb:98 transport-cc\na=rtcp-fb:98 ccm fir\na=rtcp-fb:98 nack\na=rtcp-fb:98 nack pli\na=rtpmap:99 rtx/90000\na=fmtp:99 apt=98\na=rtpmap:100 LCEVCVP9/90000\na=rtcp-fb:100 goog-remb\na=rtcp-fb:100 transport-cc\na=rtcp-fb:100 ccm fir\na=rtcp-fb:100 nack\na=rtcp-fb:100 nack pli\na=fmtp:100 profile-id=0\na=rtpmap:101 rtx/90000\na=fmtp:101 apt=100\na=rtpmap:102 LCEVCVP9/90000\na=rtcp-fb:102 goog-remb\na=rtcp-fb:102 transport-cc\na=rtcp-fb:102 ccm fir\na=rtcp-fb:102 nack\na=rtcp-fb:102 nack pli\na=fmtp:102 profile-id=2\na=rtpmap:109 rtx/90000\na=fmtp:109 apt=102\na=rtpmap:127 VP9/90000\na=rtcp-fb:127 goog-remb\na=rtcp-fb:127 transport-cc\na=rtcp-fb:127 ccm fir\na=rtcp-fb:127 nack\na=rtcp-fb:127 nack pli\na=fmtp:127 profile-id=0\na=rtpmap:107 rtx/90000\na=fmtp:107 apt=127\na=rtpmap:125 VP9/90000\na=rtcp-fb:125 goog-remb\na=rtcp-fb:125 transport-cc\na=rtcp-fb:125 ccm fir\na=rtcp-fb:125 nack\na=rtcp-fb:125 nack pli\na=fmtp:125 profile-id=2\na=rtpmap:108 LCEVCH264/90000\na=rtcp-fb:108 goog-remb\na=rtcp-fb:108 transport-cc\na=rtcp-fb:108 ccm fir\na=rtcp-fb:108 nack\na=rtcp-fb:108 nack pli\na=fmtp:108 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=42001f\na=rtpmap:124 LCEVCH264/90000\na=rtcp-fb:124 goog-remb\na=rtcp-fb:124 transport-cc\na=rtcp-fb:124 ccm fir\na=rtcp-fb:124 nack\na=rtcp-fb:124 nack pli\na=fmtp:124 level-asymmetry-allowed=1;packetization-mode=0;profile-level-id=42001f\na=rtpmap:123 LCEVCH264/90000\na=rtcp-fb:123 goog-remb\na=rtcp-fb:123 transport-cc\na=rtcp-fb:123 ccm fir\na=rtcp-fb:123 nack\na=rtcp-fb:123 nack pli\na=fmtp:123 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=42e01f\na=rtpmap:114 LCEVCH264/90000\na=rtcp-fb:114 goog-remb\na=rtcp-fb:114 transport-cc\na=rtcp-fb:114 ccm fir\na=rtcp-fb:114 nack\na=rtcp-fb:114 nack pli\na=fmtp:114 level-asymmetry-allowed=1;packetization-mode=0;profile-level-id=42e01f\na=rtpmap:116 H264/90000\na=rtcp-fb:116 goog-remb\na=rtcp-fb:116 transport-cc\na=rtcp-fb:116 ccm fir\na=rtcp-fb:116 nack\na=rtcp-fb:116 nack pli\na=fmtp:116 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=42001f\na=rtpmap:118 H264/90000\na=rtcp-fb:118 goog-remb\na=rtcp-fb:118 transport-cc\na=rtcp-fb:118 ccm fir\na=rtcp-fb:118 nack\na=rtcp-fb:118 nack pli\na=fmtp:118 level-asymmetry-allowed=1;packetization-mode=0;profile-level-id=42001f\na=rtpmap:120 H264/90000\na=rtcp-fb:120 goog-remb\na=rtcp-fb:120 transport-cc\na=rtcp-fb:120 ccm fir\na=rtcp-fb:120 nack\na=rtcp-fb:120 nack pli\na=fmtp:120 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=42e01f\na=rtpmap:122 H264/90000\na=rtcp-fb:122 goog-remb\na=rtcp-fb:122 transport-cc\na=rtcp-fb:122 ccm fir\na=rtcp-fb:122 nack\na=rtcp-fb:122 nack pli\na=fmtp:122 level-asymmetry-allowed=1;packetization-mode=0;profile-level-id=42e01f\na=rtpmap:121 H264/90000\na=rtcp-fb:121 goog-remb\na=rtcp-fb:121 transport-cc\na=rtcp-fb:121 ccm fir\na=rtcp-fb:121 nack\na=rtcp-fb:121 nack pli\na=fmtp:121 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=4d001f\na=rtpmap:119 H264/90000\na=rtcp-fb:119 goog-remb\na=rtcp-fb:119 transport-cc\na=rtcp-fb:119 ccm fir\na=rtcp-fb:119 nack\na=rtcp-fb:119 nack pli\na=fmtp:119 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=64001f\na=ssrc-group:FID 3706460331 629708086\na=ssrc:3706460331 cname:ok+GGReoOnQ2Rydt\na=ssrc:3706460331 msid:FeCJBlD4hGKgTb7cU9n8cMNK6JV5wgoxMJJc fd1fdb0c-4cc3-419a-9d69-cfe1763ff2b6\na=ssrc:3706460331 mslabel:FeCJBlD4hGKgTb7cU9n8cMNK6JV5wgoxMJJc\na=ssrc:3706460331 label:fd1fdb0c-4cc3-419a-9d69-cfe1763ff2b6\na=ssrc:629708086 cname:ok+GGReoOnQ2Rydt\na=ssrc:629708086 msid:FeCJBlD4hGKgTb7cU9n8cMNK6JV5wgoxMJJc fd1fdb0c-4cc3-419a-9d69-cfe1763ff2b6\na=ssrc:629708086 mslabel:FeCJBlD4hGKgTb7cU9n8cMNK6JV5wgoxMJJc\na=ssrc:629708086 label:fd1fdb0c-4cc3-419a-9d69-cfe1763ff2b6\nm=application 5000 UDP/DTLS/SCTP webrtc-datachannel\nc=IN IP4 0.0.0.0\na=sctp-port:5000\na=max-message-size:262144\na=ice-ufrag:X6Fg\na=ice-pwd:93KmLVN8mLrbF97Ec1VesUEz\na=ice-options:trickle\na=fingerprint:sha-256 97:4A:95:96:C9:61:D9:CA:A2:64:72:56:25:34:45:58:C0:2B:9E:80:1E:34:F6:40:74:F2:6D:48:B4:9F:FA:59\na=setup:actpass\na=mid:2\na=sctp-port:5000\na=max-message-size:262144\n";

    enum H264Profile {
        None, ConstrainedBaseline, Baseline, Main, High;

        public static H264Profile valueOf(int val) {
            switch (val) {
                case 1:
                    return ConstrainedBaseline;
                case 2:
                    return Baseline;
                case 3:
                    return Main;
                case 4:
                    return High;
            }
            return None;
        }
    };

    // the available video codecs which could be modified due to setup failure or
    // configuration
    // EnumSet<RTPCodecEnum> availableVideoCodecs =
    // EnumSet.of(RTPCodecEnum.H264_PMODE1, RTPCodecEnum.VP8);
    ArrayList<RTPCodecEnum> availableVideoCodecs = new ArrayList<>(2);
    {
        availableVideoCodecs.add(RTPCodecEnum.H264_PMODE1);
        availableVideoCodecs.add(RTPCodecEnum.VP8);
    }

    static String publicIPAddress;

    {
        try {
            publicIPAddress = NetworkManager.getPublicAddress();
        } catch (Exception e) {
            log.warn("Exception getting public IP address", e);
        }
    }

    @Test
    public void testConference() throws SDPException {
        log.info("\n testConference");
        // offer
        SessionDescription sdp = SDPFactory.createSessionDescription(chromeConfOffer);
        MediaField a = sdp.getMediaDescription(SDPMediaType.audio);
        assertNotNull(a);
        // answer
        SessionDescription sdp2 = SDPFactory.createSessionDescription(chromeConfAnswer);
        a = sdp2.getMediaDescription(SDPMediaType.audio);
        assertNotNull(a);
        MediaField ap = sdp2.getMediaDescription(SDPMediaType.application);
        assertNotNull(ap);
        int ssrc = 0;
        for (AttributeField attr : a.getAttributes(AttributeKey.ssrc)) {
            switch (attr.getValue()) {
                case "6666 cname:6666":
                    ssrc++;
                    break;
                case "6667 cname:6667":
                    ssrc++;
                    break;
                case "6668 cname:6668":
                    ssrc++;
                    break;
                default:
                    log.info("Key: {} value: {}", attr.getAttribute(), attr.getValue());
            }
        }
        assertEquals(3, ssrc);
    }

    @Test
    public void testDataChannel() throws SDPException {
        log.info("\n testDataChannel");
        SessionDescription sdp = SDPFactory.createSessionDescription(chromeDataChannelOnly);
        // application / data channel
        MediaField app = sdp.getMediaDescription(SDPMediaType.application);
        assertNotNull(app);
        log.debug("{}", app);
        // a=sctpmap:5000 webrtc-datachannel 1024
        // assertEquals("5000 webrtc-datachannel 1024",
        // app.getAttribute(AttributeKey.sctpmap).getValue());

        SessionDescription sdp2 = SDPFactory.createSessionDescription(chromeDataChannelOnly2);
        // application / data channel
        MediaField app2 = sdp2.getMediaDescription(SDPMediaType.application);
        assertNotNull(app2);
        log.debug("{}", app2);
        // a=sctpmap:5000 webrtc-datachannel 1024
        // assertEquals("5000 webrtc-datachannel 1024",
        // app2.getAttribute(AttributeKey.sctpmap).getValue());
    }

    // returns a priority score based on the codecs index in the available codecs
    // array
    int getCodecPriority(RTPCodecEnum codec) {
        int index = availableVideoCodecs.indexOf(codec);
        return index == -1 ? index : (100 - index);
    }

    int acceptProfile(H264Profile current, String profileSent) {
        int profile = Integer.parseInt(profileSent.substring(0, 2), 16);// 0,1
        int flags = Integer.parseInt(profileSent.substring(2, 4), 16);// 2,3
        boolean contraint0 = ((flags >> 7) & 0x1) == 1;
        H264Profile eval = H264Profile.None;
        if (profile == 100) {
            eval = H264Profile.High;
        } else if (profile == 77) {
            eval = H264Profile.Main;
        } else if (profile == 66) {
            if (contraint0) {
                eval = H264Profile.ConstrainedBaseline;
            } else {
                eval = H264Profile.Baseline;
            }
        }
        if (eval.ordinal() > 0 && current.compareTo(eval) < 0 && eval.compareTo(maximumH264Profile) <= 0) {
            return eval.ordinal();
        }
        return current.ordinal();
    }

    private H264Profile maximumH264Profile = H264Profile.ConstrainedBaseline;

    @Test
    public void testChromeEnhanced() throws SDPException {
        log.info("\n testChromeEnhanced");
        EnumSet<RTPCodecEnum> preferredVideoCodecs = EnumSet.of(RTPCodecEnum.H264_PMODE1, RTPCodecEnum.VP8);
        RTPCodecEnum selectedVideoCodec = RTPCodecEnum.NONE;
        int videoPayloadType = -1;
        // create local sdp
        SessionDescription localSdp = new SessionDescription();

        SessionDescription sdp = SDPFactory.createSessionDescription(chromeEnhancedOffer);
        MediaField[] medias = sdp.getMediaDescriptions();
        log.info("Media length: {}", medias.length);
        for (MediaField media : medias) {
            SDPMediaType mediaType = media.getMediaType();
            if (SDPMediaType.video.equals(mediaType)) {
                // look over all incoming rtpmap for our server preferred codec(s)
                // this set includes mode_0 items.
                List<AttributeField> videoCodecs = media.getAttributeSelections(AttributeKey.rtpmap, preferredVideoCodecs);
                log.info("videoCodecs: {}", videoCodecs);
                H264Profile selected = H264Profile.None;
                String videoCodec = null;
                for (AttributeField videoAtt : videoCodecs) {
                    videoCodec = videoAtt.getValue();
                    String[] videoCodecParts = videoCodec.split("[\\s|\\/]");
                    int pt = Integer.valueOf(videoCodecParts[0]);
                    String videoCodecName = videoCodecParts[1];
                    log.info("CODEC_SELECTION: {} current {}", videoCodecName, selectedVideoCodec.encodingName);
                    String offeredFmtp = media.getAttribute(AttributeKey.fmtp, pt).getValue();
                    String[] fmtpParts = offeredFmtp.split("[\\s|\\;]");
                    int current = getCodecPriority(selectedVideoCodec);
                    int next = getCodecPriority(RTPCodecEnum.getByEncodingName(videoCodecName));
                    if (next < current) {
                        log.warn("skipping codec by priority, current:{} next:{}", current, next);
                        continue;
                    } else {
                        log.warn("eval codec variety, current:{} next:{}", current, next);
                    }
                    selectedVideoCodec = RTPCodecEnum.getByEncodingName(videoCodecName);
                    if (selectedVideoCodec != RTPCodecEnum.VP8) {
                        if ((offeredFmtp.indexOf("packetization-mode=1")) == -1) {
                            continue;
                        }
                        String profileSent = null;
                        for (String part : fmtpParts) {
                            int idx = -1;
                            if ((idx = part.indexOf("profile-level-id")) != -1) {
                                profileSent = part.substring(idx + 17);
                                // used for non-android flow
                                int results = acceptProfile(selected, profileSent);
                                if (results > selected.ordinal()) {
                                    log.info("Overriding profile via sdp offer: {}", profileSent);
                                    selected = H264Profile.valueOf(results);
                                    videoPayloadType = pt;
                                    break;
                                } else {
                                    log.info("Skipping profile via sdp offer: {}", profileSent);
                                }
                            }
                        }
                    }
                }
                log.info("Last checked video codec: {}", videoCodec);
                // add props to the fmtp if h264 and profile is ok
                AttributeField fmtp = null;
                if (selectedVideoCodec == RTPCodecEnum.H264_PMODE1) {
                    // default profile BP 3.1
                    String profile = "42801f";
                    // see if one was offered and use it if it was
                    MediaField offeredVideo = sdp.getMediaDescription(SDPMediaType.video);
                    String offeredFmtp = offeredVideo.getAttribute(AttributeKey.fmtp, videoPayloadType).getValue();
                    log.info("Offered fmtp: {}", offeredFmtp);
                    // parse that sucka
                    String[] parts = offeredFmtp.split("[\\s|\\;]");
                    if (log.isTraceEnabled()) {
                        log.info("Split fmtp: {}", Arrays.toString(parts));
                    }
                    // publisher offered h264 profile
                    String profileSent = null;
                    for (String part : parts) {
                        int idx = -1;
                        if ((idx = part.indexOf("profile-level-id")) != -1) {
                            // profile was specified in sdp (used for android logic below)
                            profileSent = part.substring(idx + 17);
                            // used for non-android flow
                            if (acceptProfile(selected, profileSent) > 0) {
                                profile = profileSent;
                                log.info("Overriding profile via sdp offer: {}", profile);
                                break;
                            }
                        }
                    }
                    // pmode-1 only here
                    fmtp = new AttributeField(AttributeKey.fmtp, String.format("%d profile-level-id=%s;level-asymmetry-allowed=1;packetization-mode=1", videoPayloadType, profile));
                }
                // add a media field to our local sdp
                MediaField video = new MediaField(mediaType, 9, MediaField.PROTOCOL_UDP, 1);
                localSdp.addMediaDescription(video);
                // add fmtp if appropriate
                if (fmtp != null) {
                    video.addAttributeField(fmtp);
                }
                String rtpmap = RTPCodecEnum.getRTPMapString(selectedVideoCodec);
                if (selectedVideoCodec.payloadType != videoPayloadType) {
                    rtpmap = rtpmap.replace(String.valueOf(selectedVideoCodec.payloadType), String.valueOf(videoPayloadType));
                }
                video.addAttributeField(new AttributeField(AttributeKey.rtpmap, rtpmap));
                video.setFormats(new int[] { videoPayloadType });
                log.info("Video in local sdp:\n{}", video);
            }
        }
        assertEquals(120, videoPayloadType);
    }

    @Test
    public void testRPRO6991() {
        log.info("\n testRPRO6991");
        boolean isAndroid = false, isEdge = false, isOpera = false;

        EnumSet<RTPCodecEnum> preferredVideoCodecs = EnumSet.of(RTPCodecEnum.H264_PMODE1, RTPCodecEnum.VP8);
        RTPCodecEnum selectedVideoCodec = RTPCodecEnum.NONE;
        int videoPayloadType = -1;

        // create local sdp
        SessionDescription localSdp = new SessionDescription();

        SessionDescription sdp = SDPFactory.createSessionDescription(macMultiCodecs);
        MediaField[] medias = sdp.getMediaDescriptions();
        log.info("Media length: {}", medias.length);
        for (MediaField media : medias) {
            SDPMediaType mediaType = media.getMediaType();
            if (SDPMediaType.video.equals(mediaType)) {
                // look over all incoming rtpmap for our server preferred codec(s)
                // this set includes mode_0 items.
                List<AttributeField> videoCodecs = media.getAttributeSelections(AttributeKey.rtpmap, preferredVideoCodecs);
                log.info("videoCodecs: {}", videoCodecs);
                H264Profile selected = H264Profile.None;
                String videoCodec = null;
                for (AttributeField videoAtt : videoCodecs) {
                    videoCodec = videoAtt.getValue();
                    String[] videoCodecParts = videoCodec.split("[\\s|\\/]");
                    int pt = Integer.valueOf(videoCodecParts[0]);
                    String videoCodecName = videoCodecParts[1];
                    log.info("CODEC_SELECTION: {} current {}", videoCodecName, selectedVideoCodec.encodingName);
                    String offeredFmtp = media.getAttribute(AttributeKey.fmtp, pt).getValue();
                    String[] fmtpParts = offeredFmtp.split("[\\s|\\;]");
                    int current = getCodecPriority(selectedVideoCodec);
                    int next = getCodecPriority(RTPCodecEnum.getByEncodingName(videoCodecName));
                    if (next < current) {
                        log.warn("skipping codec by priority, current:{} next:{}", current, next);
                        continue;
                    } else {
                        log.warn("eval codec variety, current:{} next:{}", current, next);
                    }
                    selectedVideoCodec = RTPCodecEnum.getByEncodingName(videoCodecName);
                    if (selectedVideoCodec != RTPCodecEnum.VP8) {
                        if ((offeredFmtp.indexOf("packetization-mode=1")) == -1) {
                            continue;
                        }
                        String profileSent = null;
                        for (String part : fmtpParts) {
                            int idx = -1;
                            if ((idx = part.indexOf("profile-level-id")) != -1) {
                                profileSent = part.substring(idx + 17);
                                // used for non-android flow
                                int results = acceptProfile(selected, profileSent);
                                if (results > selected.ordinal()) {
                                    log.info("Overriding profile via sdp offer: {}", profileSent);
                                    selected = H264Profile.valueOf(results);
                                    videoPayloadType = pt;
                                    break;
                                } else {
                                    log.info("Skipping profile via sdp offer: {}", profileSent);
                                }
                            }
                        }
                    }
                }
                log.info("Last checked video codec: {}", videoCodec);
                // add props to the fmtp if h264 and profile is ok
                AttributeField fmtp = null;
                if (selectedVideoCodec == RTPCodecEnum.H264_PMODE1) {
                    // default profile BP 3.1
                    String profile = "42801f";
                    // see if one was offered and use it if it was
                    MediaField offeredVideo = sdp.getMediaDescription(SDPMediaType.video);
                    String offeredFmtp = offeredVideo.getAttribute(AttributeKey.fmtp, videoPayloadType).getValue();
                    log.info("Offered fmtp: {}", offeredFmtp);
                    // parse that sucka
                    String[] parts = offeredFmtp.split("[\\s|\\;]");
                    if (log.isTraceEnabled()) {
                        log.info("Split fmtp: {}", Arrays.toString(parts));
                    }
                    // publisher offered h264 profile
                    String profileSent = null;
                    for (String part : parts) {
                        int idx = -1;
                        if ((idx = part.indexOf("profile-level-id")) != -1) {
                            // profile was specified in sdp (used for android logic below)
                            profileSent = part.substring(idx + 17);
                            // used for non-android flow
                            if (acceptProfile(selected, profileSent) > 0) {
                                profile = profileSent;
                                log.info("Overriding profile via sdp offer: {}", profile);
                                break;
                            }
                        }
                    }
                    // check for Android Chrome publishers and adjust the profile as needed
                    if (isAndroid) {
                        // android didn't send a profile so give them CBP
                        if (profileSent == null) {
                            profile = "42801f";
                        } else {
                            // prevent the configured override from overriding the android provided profile
                            profile = profileSent;
                        }
                        log.info("Overriding profile for Android: {}", profile);
                    }
                    // if opera and offering h264 ensure the profile is acceptable
                    if (isOpera && profileSent != null) {
                        // only accepting baseline 3.1 or 3.0 from opera
                        if (profileSent.startsWith("42") && (profileSent.endsWith("1f") || profileSent.endsWith("1e"))) {
                            log.info("Opera sent a supported profile: {}", profileSent);
                        } else {
                            log.info("Opera sent an unsupported profile: {} switching to VP8", profileSent);
                            // we don't like what was offered, switch to vp8
                            selectedVideoCodec = RTPCodecEnum.VP8;
                            // continue with codecs
                            continue;
                        }
                    }
                    // if edge and offering h264 ensure the profile is acceptable
                    if (isEdge && profileSent != null) {
                        // only accepting baseline from Edge browser
                        if (profileSent.startsWith("42")) {
                            log.info("Edge sent a supported profile: {}", profileSent);
                        }
                    }
                    // pmode-1 only here
                    fmtp = new AttributeField(AttributeKey.fmtp, String.format("%d profile-level-id=%s;level-asymmetry-allowed=1;packetization-mode=1", videoPayloadType, profile));
                }
                // add a media field to our local sdp
                MediaField video = new MediaField(mediaType, 9, MediaField.PROTOCOL_UDP, 1);
                localSdp.addMediaDescription(video);
                // add fmtp if appropriate
                if (fmtp != null) {
                    video.addAttributeField(fmtp);
                }
                String rtpmap = RTPCodecEnum.getRTPMapString(selectedVideoCodec);
                if (selectedVideoCodec.payloadType != videoPayloadType) {
                    rtpmap = rtpmap.replace(String.valueOf(selectedVideoCodec.payloadType), String.valueOf(videoPayloadType));
                }
                video.addAttributeField(new AttributeField(AttributeKey.rtpmap, rtpmap));
                video.setFormats(new int[] { videoPayloadType });
                log.info("Video in local sdp:\n{}", video);
            }
        }
    }

    /**
     * Tests Safari sending the audio after the video in the SDP.
     * 
     * @throws Exception
     */
    @Test
    public void testSafariBadOrder() throws Exception {
        log.info("\n testSafariBadOrder");
        SessionDescription sdp = SDPFactory.createSessionDescription(safariBadOrder);
        assertTrue(sdp.getMediaDescriptions().length == 2);
        MediaField video = sdp.getMediaDescriptions()[0];
        assertTrue(video.getMediaType() == SDPMediaType.video);
        int[] vfmts = video.getFormats();
        log.info("Video formats: {}", Arrays.toString(vfmts));
        assertTrue(Arrays.toString(vfmts).equals("[96, 97, 98, 99, 100, 101, 127, 125, 104]"));
        MediaField audio = sdp.getMediaDescriptions()[1];
        assertTrue(audio.getMediaType() == SDPMediaType.audio);
        int[] afmts = audio.getFormats();
        log.info("Audio formats: {}", Arrays.toString(afmts));
        assertTrue(Arrays.toString(afmts).equals("[111, 103, 9, 102, 0, 8, 105, 13, 110, 113, 126]"));
    }

    @Test
    public void testSRTPOfferIOS() throws Exception {
        log.info("\n testSRTPOfferIOS");
        SessionDescription sdp = SDPFactory.createSessionDescription(srtpOfferIOS);
        assertTrue(sdp.getMediaDescriptions().length == 2);
    }

    @Test
    public void testOfferAndroid() throws Exception {
        log.info("\n testOfferAndroid");
        SessionDescription sdp = SDPFactory.createSessionDescription(androidMobileSDK);
        assertTrue(sdp.getMediaDescriptions().length == 2);
    }

    @Test
    public void testOrtcCaps() throws Exception {
        log.info("\n testORTCCaps");
        // Audio and Video streaming
        JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        JSONObject senderCaps = (JSONObject) parser.parse(edgeSenderCapabilities);
        SessionDescription sdp = SDPFactory.senderCapabilitiesToSdp(senderCaps);
        log.info("Sender caps sdp: {}", sdp);
        log.info("Receiver caps sdp: {}", SDPFactory.receiverCapabilitiesFromSdp(sdp));
    }

    @Test
    public void testRawAnswer() throws Exception {
        log.info("\n testRawAnswer");
        SessionDescription sdp = SDPFactory.createSessionDescription(rawAnswer);
        assertTrue(sdp.getMediaDescriptions().length == 2);
    }

    @Test
    public void testChromeOffer() throws SDPException {
        log.info("\n testChromeOffer");
        // Audio and Video streaming
        SessionDescription sdp = SDPFactory.createSessionDescription(chromeOffer);
        MediaField audio = sdp.getMediaDescription(SDPMediaType.audio);
        assertNotNull(audio);
        assertEquals("111 opus/48000/2", audio.getAttribute(AttributeKey.rtpmap).getValue());
        MediaField video = sdp.getMediaDescription(SDPMediaType.video);
        assertNotNull(video);
        // if parsing fails, we'll get video for audio
        assertEquals("100 VP8/90000", video.getAttribute(AttributeKey.rtpmap).getValue());
    }

    @Test
    public void testMozillaOffer() throws SDPException {
        log.info("\n testMozillaOffer");
        // Audio and Video streaming
        SessionDescription sdp = SDPFactory.createSessionDescription(mozillaOffer);
        MediaField audio = sdp.getMediaDescription(SDPMediaType.audio);
        assertNotNull(audio);
        assertEquals("109 opus/48000/2", audio.getAttribute(AttributeKey.rtpmap).getValue());
        MediaField video = sdp.getMediaDescription(SDPMediaType.video);
        assertNotNull(video);
        // if parsing fails, we'll get video for audio
        assertEquals("120 VP8/90000", video.getAttribute(AttributeKey.rtpmap).getValue());
    }

    @Test
    public void testEdgeOffer() throws SDPException {
        log.info("\n testEdgeOffer");
        // Audio and Video streaming
        SessionDescription sdp = SDPFactory.createSessionDescription(edgeOffer);
        // edge doesnt signal plan-b, but we expect support as of right now
        assertFalse(sdp.isUnified());
        MediaField[] medias = sdp.getMediaDescriptions();
        assertNotNull(medias);
        assertTrue(medias.length > 1);
        log.info("Media descriptions: {}", medias.length);
        for (MediaField mf : medias) {
            AttributeField af = null;
            if (mf.getMediaType().equals(SDPMediaType.audio)) {
                af = mf.getAttribute(AttributeKey.rtpmap, EnumSet.of(RTPCodecEnum.OPUS));
                log.debug("RTPMap: {}", af.getValue());
                if (af.getValue().contains("opus")) {
                    assertEquals("102 opus/48000/2", af.getValue());
                } else {
                    fail();
                }
            } else if (mf.getMediaType().equals(SDPMediaType.video)) {
                af = mf.getAttribute(AttributeKey.rtpmap, EnumSet.of(RTPCodecEnum.H264_PMODE1, RTPCodecEnum.VP8));
                log.debug("RTPMap: {}", af.getValue());
                if (af.getValue().contains("H264")) {
                    assertEquals("107 H264/90000", af.getValue());
                } else {
                    fail();
                }
            }
        }
    }

    @Test
    public void testMozillaOffer2() throws SDPException {
        log.info("\n testMozillaOffer2");
        // Audio and Video streaming
        SessionDescription offerSdp = SDPFactory.createSessionDescription(mozillaOffer2);
        // create local sdp
        SessionDescription localSdp = SDPFactory.createSessionDescription("junit", publicIPAddress);
        // set rtcp-mux
        localSdp.setRtcpMux(false);
        log.trace("Initial (local) sdp: {}", localSdp);
        // handle our audio / video descriptions to select our codecs
        RTPCodecEnum selectedAudioCodec = RTPCodecEnum.NONE;
        MediaField audio = offerSdp.getMediaDescription(SDPMediaType.audio);
        if (audio != null) {
            String audioCodecName = audio.getAttribute(AttributeKey.rtpmap).getValue().split("[\\s|\\/]")[1];
            for (RTPCodecEnum codec : RTPCodecEnum.values()) {
                if (codec.encodingName.equals(audioCodecName)) {
                    selectedAudioCodec = codec;
                    audio = localSdp.getMediaDescription(SDPMediaType.audio);
                    if (audio == null) {
                        audio = new MediaField(SDPMediaType.audio, 9, 1);
                        localSdp.addMediaDescription(audio);
                    }
                    audio.addAttributeField(new AttributeField(AttributeKey.rtpmap, RTPCodecEnum.getRTPMapString(selectedAudioCodec)));
                    if (localSdp.isFirefox()) {
                        audio.addAttributeField(new AttributeField(AttributeKey.mid, "sdparta_0"));
                        audio.addAttributeField(new AttributeField(AttributeKey.extmap, "1 urn:ietf:params:rtp-hdrext:ssrc-audio-level"));
                    } else {
                        audio.addAttributeField(new AttributeField(AttributeKey.mid, "audio"));
                        audio.addAttributeField(new AttributeField(AttributeKey.extmap, "1 urn:ietf:params:rtp-hdrext:ssrc-audio-level"));
                        audio.addAttributeField(new AttributeField(AttributeKey.extmap, "3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time"));
                    }
                    audio.addAttributeField(new AttributeField(AttributeKey.recvonly, null));
                    break;
                }
            }
        }
        RTPCodecEnum selectedVideoCodec = RTPCodecEnum.NONE;
        MediaField video = offerSdp.getMediaDescription(SDPMediaType.video);
        if (video != null) {
            String videoCodecName = video.getAttribute(AttributeKey.rtpmap).getValue().split("[\\s|\\/]")[1];
            for (RTPCodecEnum codec : RTPCodecEnum.values()) {
                if (codec.encodingName.equals(videoCodecName)) {
                    selectedVideoCodec = codec;
                    video = localSdp.getMediaDescription(SDPMediaType.video);
                    if (video == null) {
                        video = new MediaField(SDPMediaType.video, 9, 1);
                        localSdp.addMediaDescription(video);
                    }
                    video.addAttributeField(new AttributeField(AttributeKey.rtpmap, RTPCodecEnum.getRTPMapString(selectedVideoCodec)));
                    if (localSdp.isFirefox()) {
                        if (selectedAudioCodec == null) {
                            video.addAttributeField(new AttributeField(AttributeKey.mid, "sdparta_0"));
                        } else {
                            video.addAttributeField(new AttributeField(AttributeKey.mid, "sdparta_1"));
                        }
                    } else {
                        video.addAttributeField(new AttributeField(AttributeKey.mid, "video"));
                        video.addAttributeField(new AttributeField(AttributeKey.extmap, "2 urn:ietf:params:rtp-hdrext:toffset"));
                        video.addAttributeField(new AttributeField(AttributeKey.extmap, "3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time"));
                        video.addAttributeField(new AttributeField(AttributeKey.extmap, "4 urn:3gpp:video-orientation"));
                    }
                    video.addAttributeField(new AttributeField(AttributeKey.recvonly, null));
                    break;
                }
            }
        }
        // create the answer based on the offer
        // SessionDescription ansSdp = SdpUtils.makeMediaPayloadsNegotiation(localSdp,
        // offerSdp);
        // log.trace("Answer sdp (before negotiation): {}", ansSdp);
        // create a map for our answer with details from the offer map
        // SdpMap answerMap = new SdpMap(offerMap);
        // answerMap.setAttribute(SdpMap.AttributeKey.direction, "recvonly");
        // answerMap.setAttribute(SdpMap.AttributeKey.ufrag, "ffff");
        // answerMap.setAttribute(SdpMap.AttributeKey.passwd, "notapassword");
        // answerMap.setAttribute(SdpMap.AttributeKey.setup, "passive");
        // complete the sdp negotiation
        // SdpUtils.completeSdpNegotiation(ansSdp, localSdp, offerSdp, answerMap);
        log.trace("Answer sdp (after negotiation): {}", localSdp);
    }

    @Test
    public void testMozillaOffer50() throws SDPException {
        log.info("\n mozillaOffer50");
        // Audio and Video streaming
        SessionDescription offerSdp = SDPFactory.createSessionDescription(mozillaOffer50_audio_only);
        // create local sdp
        SessionDescription localSdp = SDPFactory.createSessionDescription("junit", publicIPAddress);
        // set rtcp-mux
        localSdp.setRtcpMux(false);
        log.trace("Initial (local) sdp: {}", localSdp);
        // handle our audio / video descriptions to select our codecs
        RTPCodecEnum selectedAudioCodec = RTPCodecEnum.NONE;
        MediaField audio = offerSdp.getMediaDescription(SDPMediaType.audio);
        if (audio != null) {
            String audioCodecName = audio.getAttribute(AttributeKey.rtpmap).getValue().split("[\\s|\\/]")[1];
            for (RTPCodecEnum codec : RTPCodecEnum.values()) {
                if (codec.encodingName.equals(audioCodecName)) {
                    selectedAudioCodec = codec;
                    audio = localSdp.getMediaDescription(SDPMediaType.audio);
                    if (audio == null) {
                        audio = new MediaField(SDPMediaType.audio, 9, 1);
                        localSdp.addMediaDescription(audio);
                    }
                    audio.addAttributeField(new AttributeField(AttributeKey.rtpmap, RTPCodecEnum.getRTPMapString(selectedAudioCodec)));
                    // get media id from offer
                    audio.addAttributeField(new AttributeField(AttributeKey.mid, offerSdp.getMediaDescription(SDPMediaType.audio).getMediaId()));
                    if (localSdp.isFirefox()) {
                        audio.addAttributeField(new AttributeField(AttributeKey.extmap, "1 urn:ietf:params:rtp-hdrext:ssrc-audio-level"));
                    } else {
                        audio.addAttributeField(new AttributeField(AttributeKey.extmap, "1 urn:ietf:params:rtp-hdrext:ssrc-audio-level"));
                        audio.addAttributeField(new AttributeField(AttributeKey.extmap, "3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time"));
                    }
                    audio.addAttributeField(new AttributeField(AttributeKey.recvonly, null));
                    break;
                }
            }
            assertEquals("sdparta_0", audio.getAttribute(AttributeKey.mid).getValue());
            // assertNotNull(audio.getAttribute(AttributeKey.fingerprint));
        }
        RTPCodecEnum selectedVideoCodec = RTPCodecEnum.NONE;
        MediaField video = offerSdp.getMediaDescription(SDPMediaType.video);
        if (video != null) {
            String videoCodecName = video.getAttribute(AttributeKey.rtpmap).getValue().split("[\\s|\\/]")[1];
            for (RTPCodecEnum codec : RTPCodecEnum.values()) {
                if (codec.encodingName.equals(videoCodecName)) {
                    selectedVideoCodec = codec;
                    video = localSdp.getMediaDescription(SDPMediaType.video);
                    if (video == null) {
                        video = new MediaField(SDPMediaType.video, 9, 1);
                        localSdp.addMediaDescription(video);
                    }
                    video.addAttributeField(new AttributeField(AttributeKey.rtpmap, RTPCodecEnum.getRTPMapString(selectedVideoCodec)));
                    // get media id from offer
                    video.addAttributeField(new AttributeField(AttributeKey.mid, offerSdp.getMediaDescription(SDPMediaType.video).getMediaId()));
                    video.addAttributeField(new AttributeField(AttributeKey.mid, "video"));
                    video.addAttributeField(new AttributeField(AttributeKey.extmap, "2 urn:ietf:params:rtp-hdrext:toffset"));
                    video.addAttributeField(new AttributeField(AttributeKey.extmap, "3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time"));
                    video.addAttributeField(new AttributeField(AttributeKey.extmap, "4 urn:3gpp:video-orientation"));
                    video.addAttributeField(new AttributeField(AttributeKey.recvonly, null));
                    break;
                }
            }
            assertEquals("sdparta_1", video.getAttribute(AttributeKey.mid).getValue());
            // assertNotNull(video.getAttribute(AttributeKey.fingerprint));
        }
        // create the answer based on the offer
        // SessionDescription ansSdp = SdpUtils.makeMediaPayloadsNegotiation(localSdp,
        // offerSdp);
        // log.trace("Answer sdp (before negotiation): {}", ansSdp);
        // create a map for our answer with details from the offer map
        // SdpMap answerMap = new SdpMap(offerMap);
        // answerMap.setAttribute(SdpMap.AttributeKey.direction, "recvonly");
        // answerMap.setAttribute(SdpMap.AttributeKey.ufrag, "ffff");
        // answerMap.setAttribute(SdpMap.AttributeKey.passwd, "notapassword");
        // answerMap.setAttribute(SdpMap.AttributeKey.setup, "passive");
        // complete the sdp negotiation
        // SdpUtils.completeSdpNegotiation(ansSdp, localSdp, offerSdp, answerMap);
        log.trace("Answer sdp (after negotiation): {}", localSdp);
    }

    // simulation for RTCSourceStream process
    @Test
    public void testAnswerMozilla() throws SDPException {
        log.info("\n testAnswerMozilla");
        SessionDescription offerSdp = SDPFactory.createSessionDescription(mozillaOffer);
        // create local sdp
        SessionDescription localSdp = SDPFactory.createSessionDescription("junit", publicIPAddress);
        // set rtcp-mux
        localSdp.setRtcpMux(false);
        // handle our audio / video descriptions to select our codecs
        RTPCodecEnum selectedAudioCodec = RTPCodecEnum.NONE;
        MediaField audio = offerSdp.getMediaDescription(SDPMediaType.audio);
        if (audio != null) {
            String audioCodecName = audio.getAttribute(AttributeKey.rtpmap).getValue().split("[\\s|\\/]")[1];
            for (RTPCodecEnum codec : RTPCodecEnum.values()) {
                if (codec.encodingName.equals(audioCodecName)) {
                    selectedAudioCodec = codec;
                    audio = localSdp.getMediaDescription(SDPMediaType.audio);
                    if (audio == null) {
                        audio = new MediaField(SDPMediaType.audio, 9, 1);
                        localSdp.addMediaDescription(audio);
                    }
                    audio.addAttributeField(new AttributeField(AttributeKey.rtpmap, RTPCodecEnum.getRTPMapString(selectedAudioCodec)));
                    if (localSdp.isFirefox()) {
                        audio.addAttributeField(new AttributeField(AttributeKey.mid, "sdparta_0"));
                        audio.addAttributeField(new AttributeField(AttributeKey.extmap, "1 urn:ietf:params:rtp-hdrext:ssrc-audio-level"));
                    } else {
                        audio.addAttributeField(new AttributeField(AttributeKey.mid, "audio"));
                        audio.addAttributeField(new AttributeField(AttributeKey.extmap, "1 urn:ietf:params:rtp-hdrext:ssrc-audio-level"));
                        audio.addAttributeField(new AttributeField(AttributeKey.extmap, "3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time"));
                    }
                    audio.addAttributeField(new AttributeField(AttributeKey.recvonly, null));
                    break;
                }
            }
        }
        RTPCodecEnum selectedVideoCodec = RTPCodecEnum.NONE;
        MediaField video = offerSdp.getMediaDescription(SDPMediaType.video);
        if (video != null) {
            String videoCodecName = video.getAttribute(AttributeKey.rtpmap).getValue().split("[\\s|\\/]")[1];
            for (RTPCodecEnum codec : RTPCodecEnum.values()) {
                if (codec.encodingName.equals(videoCodecName)) {
                    selectedVideoCodec = codec;
                    video = localSdp.getMediaDescription(SDPMediaType.video);
                    if (video == null) {
                        video = new MediaField(SDPMediaType.video, 9, 1);
                        localSdp.addMediaDescription(video);
                    }
                    video.addAttributeField(new AttributeField(AttributeKey.rtpmap, RTPCodecEnum.getRTPMapString(selectedVideoCodec)));
                    if (localSdp.isFirefox()) {
                        if (selectedAudioCodec == null) {
                            video.addAttributeField(new AttributeField(AttributeKey.mid, "sdparta_0"));
                        } else {
                            video.addAttributeField(new AttributeField(AttributeKey.mid, "sdparta_1"));
                        }
                    } else {
                        video.addAttributeField(new AttributeField(AttributeKey.mid, "video"));
                        video.addAttributeField(new AttributeField(AttributeKey.extmap, "2 urn:ietf:params:rtp-hdrext:toffset"));
                        video.addAttributeField(new AttributeField(AttributeKey.extmap, "3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time"));
                        video.addAttributeField(new AttributeField(AttributeKey.extmap, "4 urn:3gpp:video-orientation"));
                    }
                    video.addAttributeField(new AttributeField(AttributeKey.recvonly, null));
                    break;
                }
            }
        }
        // create the answer based on the offer
        // SessionDescription ansSdp = SdpUtils.makeMediaPayloadsNegotiation(localSdp,
        // offerSdp);
        // log.info("Answer: {}", ansSdp.toString());
        System.out.println("Answer: " + localSdp.toString());
    }

    @Test
    public void testAnswers() throws SDPException {
        log.info("\n testAnswers");
        String answer = "v=0\no=- 6155322022377231013 2 IN IP4 127.0.0.1\ns=-\nt=0 0\na=msid-semantic: WMS\nm=audio 9 UDP/TLS/RTP/SAVPF 111\nc=IN IP4 0.0.0.0\na=rtcp:9 IN IP4 0.0.0.0\na=ice-ufrag:RedwRBQ7sdcD3EFw\na=ice-pwd:2pDdlO/4KVkmtCIvBFhbk9gt\na=fingerprint:sha-256 79:8D:D9:D3:98:F9:B9:C8:B7:08:AF:31:AB:B6:35:06:E4:57:AF:62:01:DD:1A:AD:28:2D:45:C2:C5:70:5E:7A\na=setup:active\na=mid:audio\na=extmap:1 urn:ietf:params:rtp-hdrext:ssrc-audio-level\na=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\na=recvonly\na=rtpmap:111 opus/48000/2\na=fmtp:111 minptime=10; useinbandfec=1\na=maxptime:60\nm=video 9 UDP/TLS/RTP/SAVPF 100\nc=IN IP4 0.0.0.0\na=rtcp:9 IN IP4 0.0.0.0\na=ice-ufrag:hYptEaaLKamHNQxC\na=ice-pwd:eC5k2weoCXiTRl7wa5On/bno\na=fingerprint:sha-256 79:8D:D9:D3:98:F9:B9:C8:B7:08:AF:31:AB:B6:35:06:E4:57:AF:62:01:DD:1A:AD:28:2D:45:C2:C5:70:5E:7A\na=setup:active\na=mid:video\na=extmap:2 urn:ietf:params:rtp-hdrext:toffset\na=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\na=extmap:4 urn:3gpp:video-orientation\na=recvonly\na=rtpmap:100 VP8/90000\na=rtcp-fb:100 ccm fir\na=rtcp-fb:100 nack\na=rtcp-fb:100 nack pli\n";
        String answerVideoOnly = "v=0\no=- 6786597016641029119 2 IN IP4 127.0.0.1\ns=-\nt=0 0\na=msid-semantic: WMS\nm=video 9 UDP/TLS/RTP/SAVPF 100\nc=IN IP4 0.0.0.0\na=rtcp:9 IN IP4 0.0.0.0\na=ice-ufrag:bKvTqWA+ouL21Ecd\na=ice-pwd:fEjwtFydK69c+4KTtwIl/c15\na=fingerprint:sha-256 79:8D:D9:D3:98:F9:B9:C8:B7:08:AF:31:AB:B6:35:06:E4:57:AF:62:01:DD:1A:AD:28:2D:45:C2:C5:70:5E:7A\na=setup:active\na=mid:video\na=extmap:2 urn:ietf:params:rtp-hdrext:toffset\na=extmap:3 http://www.webrtc.org/experiments/rtp-hdrext/abs-send-time\na=extmap:4 urn:3gpp:video-orientation\na=recvonly\na=rtpmap:100 VP8/90000\na=rtcp-fb:100 ccm fir\na=rtcp-fb:100 nack\na=rtcp-fb:100 nack pli\n";
        // Audio and Video streaming
        SessionDescription answerSdp = SDPFactory.createSessionDescription(answer);
        MediaField audio = answerSdp.getMediaDescription(SDPMediaType.audio);
        assertNotNull(audio);
        assertEquals("111 opus/48000/2", audio.getAttribute(AttributeKey.rtpmap).getValue());
        // Video only streaming
        answerSdp = SDPFactory.createSessionDescription(answerVideoOnly);
        MediaField video = answerSdp.getMediaDescription(SDPMediaType.video);
        assertNotNull(video);
        // if parsing fails, we'll get video for audio
        assertEquals("100 VP8/90000", video.getAttribute(AttributeKey.rtpmap).getValue());
    }

    @Test
    public void testAnswerChromeH264VideoOnly() throws SDPException {
        log.info("\n testAnswerChromeH264VideoOnly");
        // Video-only in H264 streaming
        SessionDescription answerSdp = SDPFactory.createSessionDescription(chromeH264Answer);
        MediaField video = answerSdp.getMediaDescription(SDPMediaType.video);
        assertNotNull(video);
        // if parsing fails, we'll get video for audio
        assertEquals("126 H264/90000", video.getAttribute(AttributeKey.rtpmap).getValue());
        // log.info("FMTP: {}", video.getAttribute(AttributeKey.fmtp).getValue());
        assertEquals("126 level-asymmetry-allowed=1;packetization-mode=1;profile-level-id=42e01f", video.getAttribute(AttributeKey.fmtp).getValue());
    }

    @Test
    public void testGetBandwidth() throws SDPException {
        log.info("\n testGetBandwidth");
        SessionDescription offerSdp = SDPFactory.createSessionDescription(chromeOffer2);
        BandwidthField bw = offerSdp.getBandwidth();
        if (bw != null) {
            System.out.printf("Bandwidth - type: %s value: %d%n", bw.getType(), bw.getBandwidth());
        } else {
            System.out.println("No session-level bandwidth found");
            MediaField[] medias = offerSdp.getMediaDescriptions();
            for (MediaField media : medias) {
                bw = media.getBandwidth();
                if (bw != null) {
                    System.out.printf("Bandwidth (%s) - type: %s value: %d%n", media.getMediaType(), bw.getType(), bw.getBandwidth());
                }
            }
        }
    }

    @Test
    public void testSetBandwidth() throws SDPException {
        log.info("\n testSetBandwidth");
        SessionDescription sdp = SDPFactory.createSessionDescription(answer);

        int audioBR = 50, videoBR = 2000;
        MediaField audio = sdp.getMediaDescription(SDPMediaType.audio);
        BandwidthField ab = audio.getBandwidth();
        if (ab == null) {
            audio.setBandwidth(new BandwidthField(audioBR));
        } else {
            ab.setBandwidth(audioBR);
        }
        MediaField video = sdp.getMediaDescription(SDPMediaType.video);
        BandwidthField vb = video.getBandwidth();
        if (vb == null) {
            video.setBandwidth(new BandwidthField(videoBR));
        } else {
            vb.setBandwidth(videoBR);
        }
        log.info("Check bandwidth changed: {}", sdp);
    }

    @Test
    public void testGetRemoteSSRC() throws SDPException {
        log.info("\n testGetRemoteSSRC");
        SessionDescription offerSdp = SDPFactory.createSessionDescription(chromeOffer2);
        MediaField mf = offerSdp.getMediaDescription(SDPMediaType.video);
        if (mf != null) {
            AttributeField af = mf.getAttribute(AttributeKey.ssrc);
            System.out.printf("Video SSRC attribute: %s%n", af);
            String[] parts = af.getValue().split("\\s");
            long ssrc = Long.valueOf(parts[0]);
            System.out.println("SSRC: " + ssrc);
            assertEquals(837217914L, ssrc);
        } else {
            fail("No video media");
        }
    }

}
