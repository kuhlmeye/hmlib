package net.kuhlmeyer.hmlib.device;

import net.kuhlmeyer.hmlib.HMDevice;
import net.kuhlmeyer.hmlib.pojo.HMDeviceNotification;
import net.kuhlmeyer.hmlib.pojo.HMDeviceResponse;
import org.apache.log4j.Logger;


public class HMPB4WM extends HMDevice {

    private static final Logger LOG = Logger.getLogger(HMPB4WM.class);
    private long stateChangeDate = 0;
    private int channel;

    public HMPB4WM(String hmId, String hmCode, String name, int channel) {
        super(hmId, hmCode, name);
        this.channel = channel;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public long getStateChangeDate() {
        return stateChangeDate;
    }

    public boolean wasButtonPressed() {
        return System.currentTimeMillis() - stateChangeDate < 1 * 60 * 1000;
    }

    @Override
    public boolean eventReceived(HMDeviceNotification data) {
        if (data.getPayload().length() == 22) {
            String buttonStr = data.getPayload().substring(18, 20);
            int pressedButton = Integer.parseInt(buttonStr);
            if (pressedButton == channel) {
                stateChangeDate = System.currentTimeMillis();
                LOG.debug("Taste '" + pressedButton + "' an Schalter " + getName() + " gedrückt");
                getHMGateway().notifyCallback((callback) -> callback.buttonPressed(this, pressedButton));
            }
        }
        return true;
    }

    @Override
    public boolean responseReceived(HMDeviceResponse data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}