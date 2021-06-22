import com.fazecast.jSerialComm.SerialPort;

import javax.swing.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.Date;

public class USB_WorkerThread implements Runnable {
    BufferInterface buffer;
    int port;
    Flag flag;
    int header_size = 6;
    float timeOutThreshold = 5000;
    int baudRate;
    int canSpeed;
    int initPackageCount;

    USB_WorkerThread(BufferInterface buffer, int baudRate, int port, Flag flag, int canSpeed, int initPackageCount) {
        this.buffer = buffer;
        this.port = port;
        this.flag = flag;
        this.baudRate = baudRate;
        this.canSpeed = canSpeed;
        this.initPackageCount = initPackageCount;
    }

    private SerialPort init() {
        SerialPort comPort = SerialPort.getCommPorts()[port];
        comPort.setComPortParameters(baudRate, 8, 1, 0);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        if (comPort.openPort()) {
            return comPort;
        } else return null;
    }

    @Override
    public void run() {
        SerialPort comPort;
        if ((comPort = init()) != null) {
            InputStream in = comPort.getInputStream();
            OutputStream out = comPort.getOutputStream();
            System.out.println("Connecting to " + SerialPort.getCommPorts()[port].getDescriptivePortName() + "       [RESETTING]");
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Connection to " + SerialPort.getCommPorts()[port].getDescriptivePortName() + "       [ESTABLISHED]");
            try {
                clearBuffer(in);
                out.write(canSpeed);
                out.write(initPackageCount);
                out.write(1);
                long t_lastPackage = System.currentTimeMillis();;
                while (!flag.interrupt) {

                    if (in.available() >= header_size) {
                        int mode = in.read();

                        long id = ByteBuffer.wrap(in.readNBytes(4))
                                .order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL;
                        int size = in.read();
                        byte[] data = new byte[size];

                        while (size > in.available()) {
                            if (flag.interrupt == true) break;
                        }
                        for (int data_byte = 0; data_byte < size; data_byte++) {
                            data[data_byte] = (byte) in.read();
                        }
                        buffer.push(new CanBusPackage(mode, id, size, data, System.currentTimeMillis() / 1000L));
                        t_lastPackage = System.currentTimeMillis();
                    }
                    if ((System.currentTimeMillis() -  t_lastPackage) > timeOutThreshold) {
                        flag.interrupt = true;
                        System.out.println("Connection to " + SerialPort.getCommPorts()[port].getDescriptivePortName() + "       [TIMEOUT]");
                    }

                }
                buffer.clear();
                in.close();
                out.close();
                comPort.closePort();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Connection to " + SerialPort.getCommPorts()[port].getDescriptivePortName() + "       [CLOSED]");
        } else {
            System.out.println("Connection to " + SerialPort.getCommPorts()[port].getDescriptivePortName() + "       [FAILED]");
        }
        flag.killedUSB = true;
    }

    private void clearBuffer(InputStream in) throws IOException {
        for (int idx = 0; idx < in.available(); idx++) {
            in.read();
        }
    }
}
