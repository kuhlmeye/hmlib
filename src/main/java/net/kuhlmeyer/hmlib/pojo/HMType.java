package net.kuhlmeyer.hmlib.pojo;

public enum HMType {
	
	AlarmControl(0x01),
	OutputUnit(0x12),
	Switch(0x10),
	Dimmer(0x20),
	BlindActuator(0x30),
	ClimateControl(0x39),
	Remote(0x40),
	Sensor(0x41),
	SWI(0x42),
	PushButton(0x43),
	Thermostat(0x58),
	KFM100(0x60),
	THSensor(0x70),
	ThreeStateSensor(0x80),
	MotionDetector(0x81),
	KeyMatic(0xC0),
	WinMatic(0xC1),
	SmokeDetector(0xCD);

	int type;
	
	private HMType(int type) {
		this.type = type;
	}
	
	public static HMType parseFromType(int type) {
		for(HMType value : values()) {
			if(value.type == type) {
				return value;
			}
		}
		return null;
	}
}
