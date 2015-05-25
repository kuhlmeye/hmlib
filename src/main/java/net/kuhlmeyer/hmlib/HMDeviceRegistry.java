package net.kuhlmeyer.hmlib;

import net.kuhlmeyer.hmlib.event.HMEventCallback;
import net.kuhlmeyer.hmlib.pojo.HMDeviceInfo;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;

/**
 * Registry for the homematic devices.
 */
public abstract class HMDeviceRegistry {

    private final AbstractCollection<HMEventCallback> listeners = new ArrayList<>();
    private final List<HMDevice> hmDevices = new ArrayList<HMDevice>();

    /*
     *
     * Request
     * SB6DCE581,00,00000000,01,B6DCE581,0D8401123ABC000000010A4c455130313238373731
     * HMLAN1 S:RB6DCE581 stat:0002 t:00000000 d:FF r:7FFFm:0D8401123ABC000000010A4C455130313238373731
     * => CUL_SimpleWrite($hash, sprintf("As15%02x8401%s000000010A%s", $hash->{HM_CMDNR}, $id, unpack('H*', $arg)));
      *
     * HM_CMDNR => 0x0D (hmPairSerial)
     * id 	 => 123ABC
     * arg	 => 4c455130313238373731  => (LEQ0128771)
     * L  E  Q  0  1  2  8  7  7  1
     * 4c 45 51 30 31 32 38 37 37 31
     */
    public abstract HMDeviceInfo getDeviceInfo(String deviceId);

    public void registerHMDevice(AbstractHMDevice hmDevice) {
        hmDevices.add(hmDevice);
        hmDevice.init(getGateway());
    }

    public void addHomematicEventListener(HMEventCallback listener) {
        listeners.add(listener);
    }

    public void removeHomematicEventListener(HMEventCallback listener) {
        listeners.remove(listener);
    }

    public AbstractCollection<HMEventCallback> getListeners() {
        return listeners;
    }

    public List<HMDevice> getHmDevices() {
        return hmDevices;
    }

    protected abstract HMGateway getGateway();
}
