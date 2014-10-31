package net.kuhlmeyer.hmlib.device;

import net.kuhlmeyer.hmlib.pojo.HMEvent;
import net.kuhlmeyer.hmlib.pojo.HMResponse;

public abstract class HMDevice {
	
	private String id;
    private String hmId;
    private String hmCode;
	private String name;

	
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
    
	public abstract boolean responseReceived(HMResponse data);

	public abstract boolean eventReceived(HMEvent data);
}
