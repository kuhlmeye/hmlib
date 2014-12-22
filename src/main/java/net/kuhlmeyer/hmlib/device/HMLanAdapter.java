package net.kuhlmeyer.hmlib.device;


import net.kuhlmeyer.hmlib.HMDevice;
import net.kuhlmeyer.hmlib.HMDeviceRegistry;
import net.kuhlmeyer.hmlib.HMGateway;
import net.kuhlmeyer.hmlib.event.HMEventCallback;
import net.kuhlmeyer.hmlib.pojo.*;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.function.Consumer;


public class HMLanAdapter extends HMDeviceRegistry implements HMGateway {

    private final static Logger LOG = Logger.getLogger(HMLanAdapter.class);
    private Socket hmSocket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private HMLanGWStatus status;

    private int cmdCounter = 0;

    private Map<Integer, HMDeviceInfo> devInfoMap = new HashMap<Integer, HMDeviceInfo>();
    private String ip;
    private Integer port;

    public void startInBackground(final String ip, final Integer port) throws SocketException, IOException {

        new Thread(() -> startHomematicAdapter(ip, port), "HM-Adapter").start();

        new Timer("HM-AliveTimer").scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendKeepAlive();
            }
        }, 10 * 1000, 10000);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                hmSocket.close();
            } catch (IOException e) {
                LOG.error("Error closing homematic socket", e);
            }
        }));
    }

    public HMLanGWStatus getStatus() {
        return status;
    }

    private void openPort() throws IOException {
        LOG.debug("Opening port: " + ip + ":" + port);

        hmSocket = new Socket(ip, port);

        reader = new BufferedReader(new InputStreamReader(hmSocket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(hmSocket.getOutputStream()));

        writer.write("A123ABC\n");
    }

    private void closePort() throws IOException {
        LOG.debug("Closing port...");
        if (hmSocket != null) {
            hmSocket.close();
        }
    }

    private void startHomematicAdapter(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
        while (true) {
            try {

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }

                openPort();


                while (true) {
                    String line = reader.readLine();
                    LOG.trace("Received Line: " + line);
                    if(line == null || line.isEmpty()) {
                        LOG.warn(String.format("Received strange empty event line from adapter.. %s", line));
                        continue;
                    }

                    if (line.startsWith("H")) {
                        status = parseHMLanGWStatus(line);
                        LOG.trace("Status received: " + status);
                    } else if (line.startsWith("E")) {
                        HMDeviceNotification event = parseEvent(line);
                        LOG.debug("Event received: " + event);

                        boolean processed = false;
                        for (HMDevice hmDevice : getHmDevices()) {
                            if (hmDevice.getHmId().equals(event.getSource())) {
                                processed |= hmDevice.eventReceived(event);
                            }
                        }
                        if (!processed) {
                            LOG.debug("Event for unknown device received: " + event.toString());
                            String msgType = event.getPayload().substring(4, 6);
                            if ("00".equals(msgType)) {
                                // Device Info event..
                                int msgCounter = Integer.parseInt(event.getPayload().substring(0, 2), 16);
                                String messagePayload = event.getPayload().substring(18);
                                String source = event.getSource();
                                String model = messagePayload.substring(2, 6);
                                String type = messagePayload.substring(26, 28);
                                String serialNumberHex = messagePayload.substring(6, 26);

                                StringBuffer serialNumber = new StringBuffer();
                                for (int i = 0; i < 20; i += 2) {
                                    serialNumber.append((char) Integer.parseInt(serialNumberHex.substring(i, i + 2), 16));
                                }

                                HMDeviceInfo devInfo = new HMDeviceInfo();
                                devInfo.setHmId(source);
                                devInfo.setHmSerialNumber(serialNumber.toString());
                                devInfo.setModel(HMModel.parseFromModel(model));
                                devInfo.setType(HMType.parseFromType(Integer.valueOf(type, 16)));

                                devInfoMap.put(msgCounter, devInfo);
                            }

                        }
                    } else if (line.startsWith("R")) {
                        HMDeviceResponse response = parseResponse(line);
                        LOG.debug("Response received: " + response);

                        boolean processed = false;
                        for (HMDevice hmDevice : getHmDevices()) {
                            if (hmDevice.getHmId().equals(response.getSource())) {
                                processed |= hmDevice.responseReceived(response);
                            }
                        }

                        if (!processed) {
                            LOG.debug("Response for unknown device received: " + response.toString());
                        }
                    } else {
                        LOG.warn("Undefined line received: " + line);
                    }
                }

            } catch (IOException e) {
                LOG.error("Error reading from homematic socket", e);
            } catch (Throwable e) {
                LOG.error("Exception caught while reading from homematic socket", e);
            } finally {
                LOG.debug("Exiting adapter..");
                try {
                    closePort();
                } catch (IOException ex) {
                    LOG.error("Error closing homematic socket", ex);
                }

                LOG.debug("Restart adapter..");
            }
        }
    }


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
    @Override
    public HMDeviceInfo getDeviceInfo(String deviceId) {

        deviceId = deviceId.toUpperCase();
        String serialNumber = "";
        for (int i = 0; i < deviceId.length(); i++) {
            serialNumber += String.format("%02X", (int) deviceId.charAt(i));
        }

        String command = String.format("8401%s000000010A%s", getStatus().getOwner(), serialNumber);
        LOG.debug("Send pair command for " + deviceId + ": " + command);
        int cmdNumber = sendCommand(command);

        int i = 0;

        while (i < 100) {

            if (devInfoMap.containsKey(cmdNumber)) {
                HMDeviceInfo hmDeviceInfo = devInfoMap.remove(cmdNumber);
                return hmDeviceInfo;
            }

            i++;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }

        return null;
    }

    @Override
    protected HMGateway getGateway() {
        return this;
    }

    @Override
    public String getGatewayId() {
        return getStatus().getOwner();
    }


    public void notifyCallback(final Consumer<HMEventCallback> callback) {
        getListeners().parallelStream().forEach(callback);
    }

    /*
         * $msg = sprintf("S%08X,00,00000000,01,%08X,%s", $tm, $tm, substr($msg, 4));
         * S5F09C51C,00,00000000,01,5F09C51C,73A011784FB11ABCD40201C80000
         * 5F09C51C => int(gettimeofday()*1000) % 0xffffffff;
         *
         * S5F085233,00,00000000,01,5F085233,71A011784FB11ABCD40201C80000
         * SND L:0E N:71 CMD:A011 SRC:784FB1 DST:1ABCD4 0201C80000
         * (SET CHANNEL:01 VALUE:C8 RAMPTIME:00 ONTIME:00)
         */
    public int sendCommand(String payload) {

        try {
            long time = System.currentTimeMillis();
            time = time % 0xffffffffl;

            cmdCounter++;
            cmdCounter %= 0xff;
            String command = String.format("S%08X,00,00000000,01,%08X,%02X%s", time, time, cmdCounter, payload);
            LOG.debug("Sending command " + command);
            writeToHomematic(command);

            return cmdCounter;
        } catch (IOException e) {
            LOG.error("Error writing to homematic socket", e);
            try {
                closePort();
                openPort();
            } catch (IOException ex) {
                LOG.error("Error reopening homematic socket", e);
            }
        }
        return -1;
    }

    private void sendKeepAlive() {
        try {
            LOG.trace("Sending Keep Alive 'K'");
            writeToHomematic("K");
        } catch (IOException e) {
            LOG.error("Error writing to homematic socket", e);
            try {
                closePort();
                openPort();
            } catch (IOException ex) {
                LOG.error("Error reopening homematic socket", e);
            }
        }
    }

    private synchronized void writeToHomematic(String message) throws IOException {
        writer.write(message + "\r\n");
        writer.flush();
    }

    /*
     * E(......),(....),(........),(..),(....),(.*)
     * E199C50,0000,0072E1AB,FF,FFB4,AF8440199C500000000146
     *
     * <ol>
     * <li>Src</li>
     * <li>Status</li>
     * <li>Uptime in msec</li>
     * <li>Undefined</li>
     * <li>RSSI</li>
     * <li>Payload</li>
     * <ol>
     */
    private HMDeviceNotification parseEvent(String line) {
        if (line.startsWith("E")) {

            String[] array = line.split(",");
            if (array.length == 6) {
                HMDeviceNotification event = new HMDeviceNotification();
                event.setSource(array[0].substring(1));
                event.setStatus(array[1]);
                event.setUptime(parseUptime(array[2]));
                event.setUndefined(array[3]);
                event.setRssi(array[4]);
                event.setPayload(array[5]);
                return event;
            }
        }
        return null;
    }

    /*
     * R(........),(....),(........),(..),(....),(.*)
     * RD7F6D717,0001,01E8E801,FF,FFB3,02800216B341123ABC010100004B
     * <ol>
     * <li>Src</li>
     * <li>Status - 1-ack,8=nack,21=?,02=? 81=open</li>
     * <li>Uptime in msec</li>
     * <li>Undefined</li>
     * <li>RSSI</li>
     * <li>Payload</li>
     * <ol>
     */
    public HMDeviceResponse parseResponse(String line) {
        if (line.startsWith("R")) {
            String[] array = line.split(",");
            if (array.length == 6) {
                HMDeviceResponse response = new HMDeviceResponse();
                response.setSource(array[5].substring(6, 12));
                response.setStatus(array[1]);
                response.setUptime(parseUptime(array[2]));
                response.setUndefined(array[3]);
                response.setRssi(array[4]);
                response.setPayload(array[5]);
                return response;
            }
        }
        return null;
    }

    /*
     * HHM-LAN-IF,03C1,IEQ0062049,139927,784FB1,003B9676,0000 | | | | | | +-
     * Undefined | | | | | +- uptime in msec | | | | +- owner | | | +- undefined
     * | | +- seriennummer | +- version +- init kennung
     */
    private HMLanGWStatus parseHMLanGWStatus(String line) {
        if (line.startsWith("HHM-LAN-IF")) {

            String[] statusArr = line.split(",");
            if (statusArr.length == 7) {
                HMLanGWStatus result = new HMLanGWStatus();
                result.setVersion(statusArr[1]);
                result.setSerialNumber(statusArr[2]);
                result.setUndefined1(statusArr[3]);
                result.setOwner(statusArr[4]);
                result.setUptime(parseUptime(statusArr[5]));
                return result;
            }
        }

        return null;
    }

    private String parseUptime(String uptimeInMsAsHexStr) {
        long uptimeInMs = Long.parseLong(uptimeInMsAsHexStr, 16);
        return String.format("%ddays %02d:%02d:%02d", uptimeInMs / (1000 * 60 * 60 * 24), uptimeInMs / (1000 * 60 * 60) % 24, uptimeInMs / (1000 * 60) % 60,
                uptimeInMs / (1000) % 60);
    }

    // Rauchmelder... status_CUL_HM_HM_SEC_SD_234768

    // TODO PAIR
    // Pair Message E1FD2B9,0000,148A8865,FF,FFAC,0884001FD2B90000001C00094B45513032353432353510020100 for 1FD2B9
    //              E1FD2CA,0000,1492221C,FF,FFAD,0484001FD2CA0000001C00094B45513032353432343010020100 for 1FD2CA
    //              R7C085F96,0001,149A5DB0,FF,FFBC,058002234768123ABC00 for KEQ0706612

}
