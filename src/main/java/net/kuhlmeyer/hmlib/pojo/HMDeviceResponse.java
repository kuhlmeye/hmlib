package net.kuhlmeyer.hmlib.pojo;


public class HMDeviceResponse {
    
    private String source;
    
    
	private String status;
	private String uptime;
	private String undefined;
	private String rssi;
	private String payload;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getUndefined() {
        return undefined;
    }

    public void setUndefined(String undefined) {
        this.undefined = undefined;
    }        

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "HMResponse{" + "source=" + source + ", status=" + status + ", uptime=" + uptime + ", undefined=" + undefined + ", rssi=" + rssi + ", payload=" + payload + '}';
    }
}
