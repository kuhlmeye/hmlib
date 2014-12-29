package net.kuhlmeyer.hmlib;

import net.kuhlmeyer.hmlib.pojo.HMDeviceNotification;
import net.kuhlmeyer.hmlib.pojo.HMDeviceResponse;

public abstract class HMDevice {

    private final String hmId;
    private final String hmCode;
    private final String name;
    private transient HMGateway hmGateway;

    public HMDevice(String hmId, String hmCode, String name) {
        this.hmId = hmId;
        this.hmCode = hmCode;
        this.name = name;
    }

    public String getHmId() {
        return hmId;
    }

    public String getHmCode() {
        return hmCode;
    }

    public String getName() {
        return name;
    }

    public abstract boolean responseReceived(HMDeviceResponse data);

    public abstract boolean eventReceived(HMDeviceNotification data);

    protected void init(HMGateway hmGateway) {
        this.hmGateway = hmGateway;
    }

    public HMGateway getHMGateway() {
        return hmGateway;
    }
}
