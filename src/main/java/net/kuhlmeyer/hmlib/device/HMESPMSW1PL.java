package net.kuhlmeyer.hmlib.device;

import net.kuhlmeyer.hmlib.HMSwitch;
import net.kuhlmeyer.hmlib.pojo.HMDeviceNotification;
import org.apache.log4j.Logger;

/**
 * Created by steffen on 2/1/15.
 *
 * Implementation of the Power Meter Switch Actuator
 * with Channels:
 *
 CH1 Toggle on/ff
 CH2 Strom-/Spannungs-/Leistungs- und Frequenzmesser -> sendet aktuelle Verbrauchs/Zustandswerte bei über/unterschreiten von definierten Schwellwerten
 CH3 Leistungs-Sensor -> sendet Event wenn definierte Leistung über/unterschritten -> UC: Waschmaschine oder Trockner fertig -> Push-Nachricht
 CH4 Strom-Sensor -> Analog CH3
 CH5 Spannungs-Sensor -> Analog CH3
 CH6 Frequenz-Sensor -> Analog CH3

 */
public class HMESPMSW1PL extends HMSwitch {

    private static final int CHANNEL_1_TOGGLE_PWR = 1;
    private static final int CHANNEL_2_ALL_METERS = 2;
    private static final int CHANNEL_3_WATT_METER = 3;
    private static final int CHANNEL_4_AMP_METER  = 4;
    private static final int CHANNEL_5_VOLT_METER = 5;
    private static final int CHANNEL_6_FREQ_METER = 6;

    private static final Logger LOG = Logger.getLogger(HMESPMSW1PL.class);

    private int watt = 0;

    public HMESPMSW1PL(String hmId, String hmCode, String name) {
        super(hmId, hmCode, name, 1);
    }

    @Override
    public boolean eventReceived(HMDeviceNotification data) {
        return handleData(data);
    }

    private boolean handleData(HMDeviceNotification data) {
        final String payload = data.getPayload();
        System.out.println(payload);
        final String channelStr = payload.substring(20, 22);
        final String value = payload.substring(22, 24);

        switch (Integer.parseInt(channelStr)) {
            case CHANNEL_1_TOGGLE_PWR:
                super.eventReceived(data);
                break;
            case CHANNEL_2_ALL_METERS:
                break;

            // only for PEER-TO-PEER !
            case CHANNEL_3_WATT_METER:
                //watt = value;
                break;
            case CHANNEL_4_AMP_METER:
                break;
            case CHANNEL_5_VOLT_METER:
                break;
            case CHANNEL_6_FREQ_METER:
                break;
        }
        LOG.debug(String.format("Event for '%s' received. Channel: %s, State: %s, Value: %s", getName(), channelStr, data.getStatus(), value));

        return true;
    }

    public int getWatt() {
        return watt;
    }
}
