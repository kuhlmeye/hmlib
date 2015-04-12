package net.kuhlmeyer.hmlib.device;

import net.kuhlmeyer.hmlib.HMPowerMeterSwitch;
import net.kuhlmeyer.hmlib.pojo.HMDeviceNotification;
import org.apache.log4j.Logger;

/**
 * @author steffen
 *
 * Implementation of the Power Meter Switch Actuator
 * with Channels:
 *
 CH1 Toggle on/ff
 CH2 Strom-/Spannungs-/Leistungs- und Frequenzmesser - sendet aktuelle Verbrauchs/Zustandswerte bei über/unterschreiten von definierten Schwellwerten
 -- nur für PEER-TO-PEER
 CH3 Leistungs-Sensor - sendet Event wenn definierte Leistung über/unterschritten - UC: Waschmaschine oder Trockner fertig - Push-Nachricht
 CH4 Strom-Sensor - Analog CH3
 CH5 Spannungs-Sensor - Analog CH3
 CH6 Frequenz-Sensor - Analog CH3

 */
public class HMESPMSW1PL extends HMPowerMeterSwitch {

    private static final Logger LOG = Logger.getLogger(HMESPMSW1PL.class);

    public HMESPMSW1PL(String hmId, String hmCode, String name) {
        super(hmId, hmCode, name, 1);
    }

    @Override
    public boolean eventReceived(HMDeviceNotification data) {
        return handleData(data);
    }

    private boolean handleData(HMDeviceNotification data) {
        final String payload = data.getPayload();

        if (40 == payload.length()) {
            parseAndSetValues(payload);
            return true;
        }
        return super.eventReceived(data);
    }
}
