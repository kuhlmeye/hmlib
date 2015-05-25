package net.kuhlmeyer.hmlib.device;

import net.kuhlmeyer.hmlib.AbstractHMDevice;
import net.kuhlmeyer.hmlib.HMDevice;
import net.kuhlmeyer.hmlib.pojo.HMDeviceNotification;
import net.kuhlmeyer.hmlib.pojo.HMDeviceResponse;

/**
 * HM-SEC-SCO
 *
 * Samples
 *  -11 86 41 30B821 0000000 11000
 *  -12 86 41 30B821 0000000 111C8
 *  -13 86 41 30B821 0000000 11200
 *  -14 86 41 30B821 0000000 113C8
 *  -15 86 41 30B821 0000000 11400
 *  -16 86 41 30B821 0000000 115C8
 *  -17 86 41 30B821 0000000 11600
 */
public class HMSECSCO extends AbstractHMDevice {


    public HMSECSCO(String hmId, String hmCode, String name) {
        super(hmId, hmCode, name);
    }

    private boolean open = false;

    @Override
    public boolean responseReceived(HMDeviceResponse data) {
        return false;
    }

    @Override
    public boolean eventReceived(HMDeviceNotification data) {
        if(Integer.valueOf(data.getPayload().substring(4, 6), 16) == 0x41) {
            String state = data.getPayload().substring(22);
            open = state.equals("C8");
        }

        return true;
    }

    public boolean isOpen() {
        return open;
    }
}
