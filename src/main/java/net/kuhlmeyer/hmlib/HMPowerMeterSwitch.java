package net.kuhlmeyer.hmlib;

import org.apache.log4j.Logger;

/**
 * Created by steffen on 4/12/15.
 */
public abstract class HMPowerMeterSwitch extends HMSwitch {

    private double consumedWattHours = -1d;
    private double currentWatt = -1d;
    private double currentMilliAmp = -1d;
    private double currentVoltage = -1d;
    private double currentHertz = -1d;

    private static final Logger LOG = Logger.getLogger(HMPowerMeterSwitch.class);

    public HMPowerMeterSwitch(String hmId, String hmCode, String name, int channel) {
        super(hmId, hmCode, name, channel);
    }

    protected void parseAndSetValues (final String payload) {
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
