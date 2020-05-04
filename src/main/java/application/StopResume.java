package application;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StopResume {

    private boolean stop = false;
    private Lock lock = new ReentrantLock();
    public Condition condition = lock.newCondition();

    public void setStopResume(boolean s) {
        if (!s) {
            try {
                lock.lock();
                condition.signalAll();
                System.out.println("Boton reanudar");
            } finally {
                lock.unlock();
            }
        }
        this.stop = s;
    }

    public void stopResume() throws InterruptedException {
        try {
            lock.lock();
            while (stop) {
                try {
                    condition.await();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean getStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

}
