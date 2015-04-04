package net.kuhlmeyer.hmlib.event;

import net.kuhlmeyer.hmlib.HMSwitch;
import net.kuhlmeyer.hmlib.device.HMPB4WM;
import net.kuhlmeyer.hmlib.device.HMSECMDIR2;
import net.kuhlmeyer.hmlib.device.HMSECSDV12;
import net.kuhlmeyer.hmlib.device.HMTCITWMWEU;

import java.util.Date;

/**
 * Contains the callback methods for the homematic events
 */
public interface HMEventCallback {

    /*
     * Called after a status change of a homematic switch
     */
    void switchStateChanged(HMSwitch aSwitch);

    /*
     * Called after a temperature or humidity change was detected.
     */
    void temperatureSensorDataChanged(HMTCITWMWEU temperatureSensor, Double lastTemperature, Double lastHumidity, Date prevLastUpdate);

    /*
     * Called after new data was received from a temperature sensor
     */
    void temperatureSensorDataReceived(HMTCITWMWEU temperatureSensor);

    /*
     * Called after a wallmount button was pressed
     */
    void buttonPressed(HMPB4WM button, int pressedButton);

    /**
     * Called after motion was detected by HM-SEC-MDIR-2
     */
    void motionDetected(HMSECMDIR2 motionDetector);

    /**
     * Called after motion was detected.
     */
    void smokeDetected(HMSECSDV12 hmsecsdv12);
}
