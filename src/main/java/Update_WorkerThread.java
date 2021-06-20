import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Update_WorkerThread implements Runnable {
    BufferInterface bufferInterface;
    ArrayList<CanBusPackage> initPackages = new ArrayList<>();
    Flag flag;
    boolean mode;

    Update_WorkerThread(BufferInterface bufferInterface, Flag flag, boolean mode) {
        this.bufferInterface = bufferInterface;
        this.flag = flag;
        this.mode = mode;
    }

    @Override
    public void run() {
        CanBusPackage canBusPackage;
        while (!flag.interrupt) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if ((canBusPackage = bufferInterface.get()) != null) {
                if (canBusPackage.getMode() == 1 && mode) {
                    initPackages.add(canBusPackage);
                    System.out.println(initPackages.size());
                } else if (mode) {
                    if (isInInitArray(canBusPackage)) {
                        printPackage(canBusPackage);
                    }
                } else {
                    printPackage(canBusPackage);
                }
            }
        }
    }

    private void printPackage(CanBusPackage canBusPackage) {
        Date date = new Date(canBusPackage.getTimestamp() * 1000L); // convert seconds to milliseconds
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss"); // the format of your date
        String formattedDate = dateFormat.format(date);
        System.out.print(formattedDate+"   Package: {"  + " id:[" + canBusPackage.getId() + "]   size:[" + canBusPackage.getSize() + "]   data:[ ");

        StringBuilder sb = new StringBuilder();
        for (byte b : canBusPackage.getData()) {
            sb.append(String.format("%02X ", b));
        }
        System.out.print(sb + "]\n");
    }

    private boolean isInInitArray(CanBusPackage canBusPackage) {
        for (int packageIdx = 0; packageIdx < initPackages.size(); packageIdx++) {
            if (canBusPackage.getId() == initPackages.get(packageIdx).getId()) {
                if (canBusPackage.getSize() == initPackages.get(packageIdx).getSize()) {
                    for (int dataIdx = 0; dataIdx < canBusPackage.getSize(); dataIdx++) {
                        if (canBusPackage.getData()[dataIdx] != initPackages.get(packageIdx).getData()[dataIdx]) break;
                    }
                }
            }
        }
        return true;
    }
}
