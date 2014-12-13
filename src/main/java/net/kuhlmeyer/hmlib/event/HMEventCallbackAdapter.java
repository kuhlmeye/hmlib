package net.kuhlmeyer.hmlib.event;

import net.kuhlmeyer.hmlib.HMSwitch;
import net.kuhlmeyer.hmlib.device.HMPB4WM;
import net.kuhlmeyer.hmlib.device.HMTCITWMWEU;

import java.util.Date;

/**
 * Contains the callback methods for the homematic events
 */
public class HMEventCallbackAdapter implements HMEventCallback {

    @Override
    public void switchStateChanged(HMSwitch aSwitch) {}

    @Override
    public void temperatureSensorDataChanged(HMTCITWMWEU temperatureSensor, Double lastTemperature, Double lastHumidity, Date prevLastUpdate) {}

    @Override
    public void temperatureSensorDataReceived(HMTCITWMWEU temperatureSensor) {}

    @Override
    public void buttonPressed(HMPB4WM button, int pressedButton) {}
}
