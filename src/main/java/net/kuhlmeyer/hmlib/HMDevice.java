package net.kuhlmeyer.hmlib;

import net.kuhlmeyer.hmlib.pojo.HMDeviceNotification;
import net.kuhlmeyer.hmlib.pojo.HMDeviceResponse;

/**
 * Created by christof on 25.05.15.
 */
public interface HMDevice {

    String getHmId();

    String getHmCode();

    String getName();

    boolean responseReceived(HMDeviceResponse data);

    boolean eventReceived(HMDeviceNotification data);

    HMGateway getHMGateway();
}
