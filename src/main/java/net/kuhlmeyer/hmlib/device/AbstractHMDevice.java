package net.kuhlmeyer.hmlib.device;

import net.kuhlmeyer.hmlib.HMLanAdapter;
import net.kuhlmeyer.hmlib.pojo.HMDeviceNotification;
import net.kuhlmeyer.hmlib.pojo.HMDeviceResponse;

public abstract class AbstractHMDevice {
	
	private String id;
    private String hmId;
    private String hmCode;
	private String name;
    private HMLanAdapter lanAdapter;

	
  	public String getId() {
		return id;
	}	

	public void setId(String id) {
		this.id = id;
	}	
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

    public String getHmCode() {
		return hmCode;
	}
    
    public void setHmCode(String hmCode) {
		this.hmCode = hmCode;
	}
    
    public String getHmId() {
		return hmId;
	}
    
    public void setHmId(String hmId) {
		this.hmId = hmId;
	}
    
	public abstract boolean responseReceived(HMDeviceResponse data);

	public abstract boolean eventReceived(HMDeviceNotification data);

    public void init(HMLanAdapter hmLanAdapter) {
        this.lanAdapter = hmLanAdapter;
    }

    public HMLanAdapter getLanAdapter() {
        return lanAdapter;
    }
}
