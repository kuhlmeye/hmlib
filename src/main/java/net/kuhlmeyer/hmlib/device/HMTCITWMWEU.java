package net.kuhlmeyer.hmlib.device;

import net.kuhlmeyer.hmlib.AbstractHMDevice;
import net.kuhlmeyer.hmlib.HMDevice;
import net.kuhlmeyer.hmlib.pojo.HMDeviceNotification;
import net.kuhlmeyer.hmlib.pojo.HMDeviceResponse;
import org.apache.log4j.Logger;

import java.util.Date;

/*
 * HM-TC-IT-WM-W-EU Temperature Sensor
 *
 *
 * Notification-Payload = B4865A26047600000094DC2A
 *
 * B4865A260476000000 94DC 2A => 18,5°C SetTemp =>  22°C Temp, 42%
 *                    ---- --
 *                      A   B
 * Selected Temperature = ((A >> 10) & 0x3f) / 2.0
 * Current Temperature  = ((A & 0x3ff) / 10.0
 * Current Humidity     = B
 *
 * @author christof
 */
public class HMTCITWMWEU extends AbstractHMDevice {

    private static final Logger LOG = Logger.getLogger(HMTCITWMWEU.class);

    private Double humidity;
    private Double temperature;
    private Date lastUpdate;
    private Double selTemperature;

    public HMTCITWMWEU(String hmId, String hmCode, String name) {
        super(hmId, hmCode, name);
    }

    public String getFormattedHumidity() {
        Double humidity = getHumidity();
        if (humidity == null) {
            return "-";
        } else {
            return String.format("%2.1f%%", humidity);
        }
    }

    public String getFormattedTemperature() {
        Double temperature = getTemperature();
        if (temperature == null) {
            return "-";
        } else {
            return String.format("%2.1f°C", temperature);
        }
    }

    public Double getHumidity() {
        return humidity;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Date getLastUpdateTime() {
        return lastUpdate;
    }

    public Double getSelectedTemperature() { return selTemperature; }

    @Override
    public boolean responseReceived(HMDeviceResponse data) {
        return true;
    }

    @Override
    public boolean eventReceived(HMDeviceNotification data) {
        String payload = data.getPayload();

        if (payload.length() == 24) {

            if (Integer.valueOf(payload.substring(4, 6), 16) == 0x5A) {
                Integer sensorData = Integer.valueOf(payload.substring(18, 22), 16);

                double selTemp = ((sensorData >> 10) & 0x3f) / 2.0;
                double temperature = (sensorData & 0x3ff) / 10.0;
                double humidity = Integer.valueOf(payload.substring(22), 16);

                LOG.debug(String.format("Received temperature for %s: Selected: %f, Current: %f, Humidity: %f", getName(), selTemp, temperature, humidity));

                Double lastTemperature = this.temperature;
                Double lastHumidity = this.humidity;
                Date prevLastUpdate = this.lastUpdate;

                this.temperature = temperature;
                this.humidity = humidity;
                this.lastUpdate = new Date();
                this.selTemperature = selTemp;


                getHMGateway().notifyCallback((callback) -> callback.temperatureSensorDataReceived(this));
                if (lastTemperature == null || lastHumidity == null || !lastTemperature.equals(temperature) || !lastHumidity.equals(humidity)) {
                    getHMGateway().notifyCallback((callback) -> callback.temperatureSensorDataChanged(this, lastTemperature, lastHumidity, prevLastUpdate));
                }
            }
        }
        return true;
    }
}

