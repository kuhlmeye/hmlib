package net.kuhlmeyer.hmlib.pojo;


public class HMLanGWStatus {

    private String serialNumber;
    private String version;
    private String uptime;
    private String undefined1;
    private String undefined2;
    private String owner;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getUndefined1() {
        return undefined1;
    }

    public void setUndefined1(String undefined1) {
        this.undefined1 = undefined1;
    }

    public String getUndefined2() {
        return undefined2;
    }

    public void setUndefined2(String undefined2) {
        this.undefined2 = undefined2;
    }

    @Override
    public String toString() {
        return "Status HM-LAN-CFG [owner=" + owner + ", serialNumber=" + serialNumber + ", version=" + version + ", uptime=" + uptime + "]";
    }
}
