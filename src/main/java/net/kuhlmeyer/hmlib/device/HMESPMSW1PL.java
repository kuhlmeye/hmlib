package net.kuhlmeyer.hmlib.device;

import net.kuhlmeyer.hmlib.HMSwitch;
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
public class HMESPMSW1PL extends HMSwitch {

    private static final Logger LOG = Logger.getLogger(HMESPMSW1PL.class);



    private double consumedWattHours = -1d;
    private double currentWatt = -1d;
    private double currentMilliAmp = -1d;
    private double currentVoltage = -1d;
    private double currentHertz = -1d;

    public HMESPMSW1PL(String hmId, String hmCode, String name) {
        super(hmId, hmCode, name, 1);
    }

    @Override
    public boolean eventReceived(HMDeviceNotification data) {
        return handleData(data);
    }

    private boolean handleData(HMDeviceNotification data) {
        final String payload = data.getPayload();

        // Seems to be Channel 1 Handling - Set ON/OFF
        if (24 == payload.length()) {
            //12A4102C8A2A29A4E80601 00 00  - 00 set OFF
            //14A4102C8A2A29A4E80601 C8 00  - C8 set ON
            return super.eventReceived(data);
        }

        else if (40 == payload.length()) {
/*
            $eCnt = ($eCnt&0x7fffff)/10;          #0.0  ..838860.7  Wh    - 3 BYTE
            $P = $P   /100;                       #0.0  ..167772.15 W     - 3 BYTE
            $I = $I   /1;                         #0.0  ..65535.0   mA    - 2 BYTE
            $U = $U   /10;                        #0.0  ..6553.5    mV    - 2 BYTE
            $F -= 256 if ($F > 127);                                      - 1 BYTE
            $F = $F/100+50;                       # 48.72..51.27     Hz   - 1 BYTE

              0  2  4  6  8  10 12 14 16    18 20 22 24 26 28 30 32 34 36 38
              3D 84 5E 2C 8A 2A 00 00 00    80 00 56 00 00 11 00 07 08 BF FE
                                            -------- -------- ----- ----- --
                                            eCnt     P        I     U     F
*/
            consumedWattHours = ((Integer.valueOf(payload.substring(18, 24), 16)) & 0x7fffff) / 10.0;
            currentWatt = Integer.valueOf(payload.substring(24, 30), 16) / 100.00;
            currentMilliAmp = Integer.valueOf(payload.substring(30, 34), 16);
            currentVoltage =  Integer.valueOf(payload.substring(34, 38), 16) / 10.0 ;
            currentHertz = Integer.valueOf(payload.substring(38), 16) / 100.00 + 50;

            LOG.debug(String.format("PowerMeter measuring for '%S': Watt %s, Voltage %s, MilliAmp %s, Hertz %s, WattHours %s", getName(), currentWatt, currentVoltage, currentMilliAmp, currentHertz, consumedWattHours));
            return true;
        }
        return false;
    }
    public double getConsumedWattHours() {
        return consumedWattHours;
    }

    public double getCurrentWatt() {
        return currentWatt;
    }

    public double getCurrentMilliAmp() {
        return currentMilliAmp;
    }

    public double getCurrentVoltage() {
        return currentVoltage;
    }

    public double getCurrentHertz() {
        return currentHertz;
    }
}
