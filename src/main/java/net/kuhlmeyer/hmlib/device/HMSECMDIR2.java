package net.kuhlmeyer.hmlib.device;

import net.kuhlmeyer.hmlib.HMDevice;
import net.kuhlmeyer.hmlib.pojo.HMDeviceNotification;
import net.kuhlmeyer.hmlib.pojo.HMDeviceResponse;
import org.apache.log4j.Logger;

import java.util.Date;


/**
 * HM-SEC-MDIR-2
 *
 * Samples...
 *   - 0C 84 41 2F2A22 000000 01094B80
 *   - 0D 84 41 2F2A22 000000 010A4B80
 *   - 0E 84 41 2F2A22 000000 010B4B80
 *   - 12 84 41 2F2A22 000000 010E4D80
 *
 * Kopplung..
 * E2F2EDE,0000,1E9C09BB,FF,FFB8,0384002F2EDE0000001600C04C45513037393834303881810100
 * E2F2EDE,0000,1E9E75D6,FF,FFC3,0484412F2EDE0000000102A980
 */
public class HMSECMDIR2 extends HMDevice {

    private static final Logger LOG = Logger.getLogger(HMSECMDIR2.class);
    private static final int MOTION_DETECTED = 0x41;
    private long stateChangeDate;

    public HMSECMDIR2(String hmId, String hmCode, String name) {
        super(hmId, hmCode, name);
    }

    @Override
    public boolean responseReceived(HMDeviceResponse data) {
        return false;
    }

    @Override
    public boolean eventReceived(HMDeviceNotification data) {
        if(Integer.valueOf(data.getPayload().substring(4, 6), 16) == MOTION_DETECTED) {
            String payloadData = data.getPayload().substring(18);

            int count = Integer.valueOf(payloadData.substring(2, 4), 16);
            int brightness = Integer.valueOf(payloadData.substring(4, 6), 16);
            double nextTr = payloadData.length() >= 6 ? ((Integer.valueOf(payloadData.substring(6, 8), 16) >> 4) -1 / 1.1) : -1;

            LOG.debug(String.format("Motion Detected: Count: %d, Brightness: %d, NextTr: %fs\n", count, brightness, nextTr));
            getHMGateway().notifyCallback((callback) -> callback.motionDetected(this));
            stateChangeDate = System.currentTimeMillis();
        }
        return true;
    }

    public long getStateChangeDate() {
        return stateChangeDate;
    }
}

/*
2F2E54 leq0798270
2015.04.12 16:56:13 5: HMLAN/RAW: /E2F2E54,0000,1EA772AE,FF,FFB6,0084102F2E540000000601F900

2015.04.12 16:56:13 5: HMLAN_Parse: HMLAN1 R:E2F2E54   stat:0000 t:1EA772AE d:FF r:FFB6     m:00 8410 2F2E54 000000 0601F900
2015.04.12 16:56:13 5: HMLAN1 dispatch A0D0084102F2E540000000601F900::-74:HMLAN1
2015.04.12 16:56:13 3: HMLAN1: Unknown code A0D0084102F2E540000000601F900::-74:HMLAN1, help me!
2015.04.12 16:56:15 5: HMLAN/RAW: /E2615B7,0000,1EA7781A,FF,FFA1,DE84702615B700000000B632

2015.04.12 16:56:15 5: HMLAN_Parse: HMLAN1 R:E2615B7   stat:0000 t:1EA7781A d:FF r:FFA1     m:DE 8470 2615B7 000000 00B632
2015.04.12 16:56:15 5: HMLAN1 dispatch A0CDE84702615B700000000B632::-95:HMLAN1
2015.04.12 16:56:15 3: HMLAN1: Unknown code A0CDE84702615B700000000B632::-95:HMLAN1, help me!
2015.04.12 16:56:16 5: HMLAN_Send:  HMLAN1 I:K
2015.04.12 16:56:16 5: HMLAN/RAW: /HHM-LAN-IF,03C1,IEQ0062049,139927,123ABC,1EA77BD1,0004

2015.04.12 16:56:16 5: HMLAN_Parse: HMLAN1 V:03C1 sNo:IEQ0062049 d:139927 O:123ABC t:1EA77BD1 IDcnt:0004
2015.04.12 16:56:18 5: HMLAN/RAW: /E2615B9,0000,1EA7830F,FF,FFD8,88865A2615B900000098D12B

2015.04.12 16:56:18 5: HMLAN_Parse: HMLAN1 R:E2615B9   stat:0000 t:1EA7830F d:FF r:FFD8     m:88 865A 2615B9 000000 98D12B
2015.04.12 16:56:18 5: HMLAN1 dispatch A0C88865A2615B900000098D12B::-40:HMLAN1
2015.04.12 16:56:18 3: HMLAN1: Unknown code A0C88865A2615B900000098D12B::-40:HMLAN1, help me!
2015.04.12 16:56:19 5: HMLAN/RAW: /E261600,0000,1EA78760,FF,FFB6,AC847026160000000000C829

2015.04.12 16:56:19 5: HMLAN_Parse: HMLAN1 R:E261600   stat:0000 t:1EA78760 d:FF r:FFB6     m:AC 8470 261600 000000 00C829
2015.04.12 16:56:19 5: HMLAN1 dispatch A0CAC847026160000000000C829::-74:HMLAN1
2015.04.12 16:56:19 3: HMLAN1: Unknown code A0CAC847026160000000000C829::-74:HMLAN1, help me!
2015.04.12 16:56:20 5: HMLAN/RAW: /E2615DD,0000,1EA78C73,FF,FFBA,16865A2615DD000000B0DD26

2015.04.12 16:56:20 5: HMLAN_Parse: HMLAN1 R:E2615DD   stat:0000 t:1EA78C73 d:FF r:FFBA     m:16 865A 2615DD 000000 B0DD26
2015.04.12 16:56:20 5: HMLAN1 dispatch A0C16865A2615DD000000B0DD26::-70:HMLAN1
2015.04.12 16:56:20 3: HMLAN1: Unknown code A0C16865A2615DD000000B0DD26::-70:HMLAN1, help me!
2015.04.12 16:56:22 5: HMLAN/RAW: /E260476,0000,1EA79364,FF,FFBE,BE865A26047600000088B72C

2015.04.12 16:56:22 5: HMLAN_Parse: HMLAN1 R:E260476   stat:0000 t:1EA79364 d:FF r:FFBE     m:BE 865A 260476 000000 88B72C
2015.04.12 16:56:22 5: HMLAN1 dispatch A0CBE865A26047600000088B72C::-66:HMLAN1
2015.04.12 16:56:22 3: HMLAN1: Unknown code A0CBE865A26047600000088B72C::-66:HMLAN1, help me!
2015.04.12 16:56:23 5: HMLAN/RAW: /E2F2E54,0000,1EA79855,FF,FFB9,0184002F2E540000001600C04C45513037393832373081810100

2015.04.12 16:56:23 5: HMLAN_Parse: HMLAN1 R:E2F2E54   stat:0000 t:1EA79855 d:FF r:FFB9     m:01 8400 2F2E54 000000 1600C04C45513037393832373081810100
2015.04.12 16:56:23 5: HMLAN1 dispatch A1A0184002F2E540000001600C04C45513037393832373081810100::-71:HMLAN1
2015.04.12 16:56:23 2: CUL_HM Unknown device CUL_HM_HM_SEC_MDIR_2_2F2E54 is now defined
2015.04.12 16:56:23 2: autocreate: define CUL_HM_HM_SEC_MDIR_2_2F2E54 CUL_HM 2F2E54
2015.04.12 16:56:23 2: autocreate: define FileLog_CUL_HM_HM_SEC_MDIR_2_2F2E54 FileLog ./log/CUL_HM_HM_SEC_MDIR_2_2F2E54-%Y.log CUL_HM_HM_SEC_MDIR_2_2F2E54
2015.04.12 16:56:23 3: Device CUL_HM_HM_SEC_MDIR_2_2F2E54 added to ActionDetector with 000:20 time
2015.04.12 16:56:28 3: Device CUL_HM_HM_SEC_MDIR_2_2F2E54 added to ActionDetector with 000:20 time
2015.04.12 16:56:28 5: HMLAN_Send:  HMLAN1 I:+2F2E54,00,01,1E
2015.04.12 16:56:38 5: HMLAN/RAW: /E2615B9,0000,1EA7D131,FF,FFD8,8884702615B900000000D12B

2015.04.12 16:56:38 5: HMLAN_Parse: HMLAN1 R:E2615B9   stat:0000 t:1EA7D131 d:FF r:FFD8     m:88 8470 2615B9 000000 00D12B
2015.04.12 16:56:38 5: HMLAN1 dispatch A0C8884702615B900000000D12B::-40:HMLAN1
2015.04.12 16:56:38 3: HMLAN1: Unknown code A0C8884702615B900000000D12B::-40:HMLAN1, help me!
2015.04.12 16:56:38 5: HMLAN/RAW: /E260489,0000,1EA7D291,FF,FFC1,D5865A26048900000090C62B

2015.04.12 16:56:38 5: HMLAN_Parse: HMLAN1 R:E260489   stat:0000 t:1EA7D291 d:FF r:FFC1     m:D5 865A 260489 000000 90C62B
2015.04.12 16:56:38 5: HMLAN1 dispatch A0CD5865A26048900000090C62B::-63:HMLAN1
2015.04.12 16:56:38 3: HMLAN1: Unknown code A0CD5865A26048900000090C62B::-63:HMLAN1, help me!
2015.04.12 16:56:40 5: HMLAN/RAW: /E2615DD,0000,1EA7DA96,FF,FFBC,1684702615DD00000000DD26

2015.04.12 16:56:40 5: HMLAN_Parse: HMLAN1 R:E2615DD   stat:0000 t:1EA7DA96 d:FF r:FFBC     m:16 8470 2615DD 000000 00DD26
2015.04.12 16:56:40 5: HMLAN1 dispatch A0C1684702615DD00000000DD26::-68:HMLAN1



Kopplung: 2F2BC5 leq0797615
2015.04.12 16:52:08 5: HMLAN/RAW: /E261627,0000,1EA3B442,FF,FFAE,2F847026162700000000C32C

2015.04.12 16:52:08 5: HMLAN_Parse: HMLAN1 R:E261627   stat:0000 t:1EA3B442 d:FF r:FFAE     m:2F 8470 261627 000000 00C32C
2015.04.12 16:52:08 5: HMLAN1 dispatch A0C2F847026162700000000C32C::-82:HMLAN1
2015.04.12 16:52:08 3: HMLAN1: Unknown code A0C2F847026162700000000C32C::-82:HMLAN1, help me!
2015.04.12 16:52:22 5: HMLAN/RAW: /E2F2BC5,0000,1EA3EB08,FF,FFC2,0084102F2BC50000000601D300

2015.04.12 16:52:22 5: HMLAN_Parse: HMLAN1 R:E2F2BC5   stat:0000 t:1EA3EB08 d:FF r:FFC2     m:00 8410 2F2BC5 000000 0601D300
2015.04.12 16:52:22 5: HMLAN1 dispatch A0D0084102F2BC50000000601D300::-62:HMLAN1
2015.04.12 16:52:22 3: HMLAN1: Unknown code A0D0084102F2BC50000000601D300::-62:HMLAN1, help me!
2015.04.12 16:52:27 5: HMLAN/RAW: /E261607,0000,1EA3FEF6,FF,FFBA,25847026160700000000D025

2015.04.12 16:52:27 5: HMLAN_Parse: HMLAN1 R:E261607   stat:0000 t:1EA3FEF6 d:FF r:FFBA     m:25 8470 261607 000000 00D025
2015.04.12 16:52:27 5: HMLAN1 dispatch A0C25847026160700000000D025::-70:HMLAN1
2015.04.12 16:52:27 3: HMLAN1: Unknown code A0C25847026160700000000D025::-70:HMLAN1, help me!
2015.04.12 16:52:31 5: HMLAN_Send:  HMLAN1 I:K
2015.04.12 16:52:31 5: HMLAN/RAW: /HHM-LAN-IF,03C1,IEQ0062049,139927,123ABC,1EA40CB6,0003

2015.04.12 16:52:31 5: HMLAN_Parse: HMLAN1 V:03C1 sNo:IEQ0062049 d:139927 O:123ABC t:1EA40CB6 IDcnt:0003
2015.04.12 16:52:32 5: HMLAN/RAW: /E2615FE,0000,1EA4114C,FF,FFAF,6E865A2615FE000000A0D92A

2015.04.12 16:52:32 5: HMLAN_Parse: HMLAN1 R:E2615FE   stat:0000 t:1EA4114C d:FF r:FFAF     m:6E 865A 2615FE 000000 A0D92A
2015.04.12 16:52:32 5: HMLAN1 dispatch A0C6E865A2615FE000000A0D92A::-81:HMLAN1
2015.04.12 16:52:32 3: HMLAN1: Unknown code A0C6E865A2615FE000000A0D92A::-81:HMLAN1, help me!
2015.04.12 16:52:40 5: HMLAN/RAW: /E2604A0,0000,1EA42FE6,FF,FFCE,14865A2604A0000000A0D127

2015.04.12 16:52:40 5: HMLAN_Parse: HMLAN1 R:E2604A0   stat:0000 t:1EA42FE6 d:FF r:FFCE     m:14 865A 2604A0 000000 A0D127
2015.04.12 16:52:40 5: HMLAN1 dispatch A0C14865A2604A0000000A0D127::-50:HMLAN1
2015.04.12 16:52:40 3: HMLAN1: Unknown code A0C14865A2604A0000000A0D127::-50:HMLAN1, help me!
2015.04.12 16:52:51 5: HMLAN/RAW: /E2F2BC5,0000,1EA45B03,FF,FFB1,0184002F2BC50000001600C04C45513037393736313581810100

2015.04.12 16:52:51 5: HMLAN_Parse: HMLAN1 R:E2F2BC5   stat:0000 t:1EA45B03 d:FF r:FFB1     m:01 8400 2F2BC5 000000 1600C04C45513037393736313581810100
2015.04.12 16:52:51 5: HMLAN1 dispatch A1A0184002F2BC50000001600C04C45513037393736313581810100::-79:HMLAN1
2015.04.12 16:52:51 2: CUL_HM Unknown device CUL_HM_HM_SEC_MDIR_2_2F2BC5 is now defined
2015.04.12 16:52:51 2: autocreate: define CUL_HM_HM_SEC_MDIR_2_2F2BC5 CUL_HM 2F2BC5
2015.04.12 16:52:51 2: autocreate: define FileLog_CUL_HM_HM_SEC_MDIR_2_2F2BC5 FileLog ./log/CUL_HM_HM_SEC_MDIR_2_2F2BC5-%Y.log CUL_HM_HM_SEC_MDIR_2_2F2BC5
2015.04.12 16:52:51 3: Device CUL_HM_HM_SEC_MDIR_2_2F2BC5 added to ActionDetector with 000:20 time
2015.04.12 16:52:52 5: HMLAN/RAW: /E2615FE,0000,1EA45F6F,FF,FFB1,6E84702615FE00000000D92A

2015.04.12 16:52:52 5: HMLAN_Parse: HMLAN1 R:E2615FE   stat:0000 t:1EA45F6F d:FF r:FFB1     m:6E 8470 2615FE 000000 00D92A
2015.04.12 16:52:52 5: HMLAN1 dispatch A0C6E84702615FE00000000D92A::-79:HMLAN1
2015.04.12 16:52:52 3: HMLAN1: Unknown code A0C6E84702615FE00000000D92A::-79:HMLAN1, help me!
2015.04.12 16:52:56 5: HMLAN_Send:  HMLAN1 I:K
2015.04.12 16:52:56 5: HMLAN/RAW: /HHM-LAN-IF,03C1,IEQ0062049,139927,123ABC,1EA46E62,0003

2015.04.12 16:52:56 5: HMLAN_Parse: HMLAN1 V:03C1 sNo:IEQ0062049 d:139927 O:123ABC t:1EA46E62 IDcnt:0003
2015.04.12 16:52:56 3: Device CUL_HM_HM_SEC_MDIR_2_2F2BC5 added to ActionDetector with 000:20 time
2015.04.12 16:52:56 5: HMLAN_Send:  HMLAN1 I:+2F2BC5,00,01,1E
 */