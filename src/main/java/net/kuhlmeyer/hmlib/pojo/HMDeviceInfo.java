package net.kuhlmeyer.hmlib.pojo;

public class HMDeviceInfo {

    private String hmId;
    private String hmSerialNumber;
    private HMModel model;
    private HMType type;

    public String getHmId() {
        return hmId;
    }

    public void setHmId(String hmId) {
        this.hmId = hmId;
    }

    public String getHmSerialNumber() {
        return hmSerialNumber;
    }

    public void setHmSerialNumber(String hmSerialNumber) {
        this.hmSerialNumber = hmSerialNumber;
    }

    public HMModel getModel() {
        return model;
    }

    public void setModel(HMModel model) {
        this.model = model;
    }

    public HMType getType() {
        return type;
    }

    public void setType(HMType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "HMDeviceInfo [hmId=" + hmId + ", hmSerialNumber=" + hmSerialNumber + ", model=" + model + ", type=" + type + "]";
    }
}
