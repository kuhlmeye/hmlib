package net.kuhlmeyer.hmlib.device;

import net.kuhlmeyer.hmlib.AbstractHMDevice;
import net.kuhlmeyer.hmlib.HMDevice;
import net.kuhlmeyer.hmlib.pojo.HMDeviceNotification;
import net.kuhlmeyer.hmlib.pojo.HMDeviceResponse;

/**
 * HM-SEC-SD-V1-2
 *
 * Examples
 *   -A1 84 00 234768 000000 1000424B455130373036363132CD000100
 *   -A2 84 00 234768 000000 1000424B455130373036363132CD000100
 */
public class HMSECSDV12 extends AbstractHMDevice {

    boolean detected = false;

    public HMSECSDV12(String hmId, String hmCode, String name) {
        super(hmId, hmCode, name);
    }

    @Override
    public boolean responseReceived(HMDeviceResponse data) {
        return false;
    }

    @Override
    public boolean eventReceived(HMDeviceNotification data) {
        if(Integer.valueOf(data.getPayload().substring(4, 6), 16) >= 0x02) {
            if(!detected) {
                getHMGateway().notifyCallback((callback) -> callback.smokeDetected(this));
            }
            detected = true;
        } else {
            detected = false;
        }
        return false;
    }

    public boolean smokeDetected() {
        return detected;
    }
}
