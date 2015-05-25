package net.kuhlmeyer.hmlib.device;

import net.kuhlmeyer.hmlib.AbstractHMDevice;
import net.kuhlmeyer.hmlib.HMSwitch;
import net.kuhlmeyer.hmlib.pojo.HMDeviceNotification;
import net.kuhlmeyer.hmlib.pojo.HMDeviceResponse;

/**
 * Created by christof on 25.05.15.
 */
public class HMSECSFASM  extends HMSwitch {

    public static HMSECSFASM createSiren(String hmId, String hmCode, String name) {
        return new HMSECSFASM(hmId, hmCode, name, 0x01);
    }

    public static HMSECSFASM createFlash(String hmId, String hmCode, String name) {
        return new HMSECSFASM(hmId, hmCode, name, 0x02);
    }

    public HMSECSFASM(String hmId, String hmCode, String name, int channel) {
        super(hmId, hmCode, name, channel);
    }
}
