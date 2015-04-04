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