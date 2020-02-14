package task;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class FileScanTask {
    private  final ExecutorService pool =
            Executors.newFixedThreadPool(4);

    //public static volatile int count = 0;
    private  AtomicInteger count = new AtomicInteger();
    private  final CountDownLatch latch = new CountDownLatch(1);

    private static FileScanCallback callback;

    public FileScanTask(FileScanCallback callback) {
        this.callback = callback;
    }


    //测试
   /* public static void main(String[] args) throws InterruptedException {
        FileScanTask task = new FileScanTask();
        task.startScan(new File("d://"));

        synchronized (task) {
            task.wait();
        }
        System.out.println("执行完毕");
    }*/


    public  void startScan(File root) {
//        synchronized (this) {
//            count++;
//        }
        count.incrementAndGet();
        pool.execute(new Runnable() {
            @Override
            public void run() {
                list(root);
            }
        });
    }

    public void waitFinish() throws InterruptedException {
//        try {
//            synchronized (this) {
//                this.wait();
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        try {
            latch.await();
        }finally {
            pool.shutdown();
        }
    }

    private  void list(File dir) {
        if (!Thread.interrupted()) {
            try {
                callback.execute(dir);
                //System.out.println(dir.getPath());
                if (dir.isDirectory()) {
                    File[] children = dir.listFiles();
                    if (children != null && children.length > 0) {
                        for (File child : children) {
                            if (child.isDirectory()) {
    //                            synchronized (this) {
    //                                count++;
    //                            }
                                count.incrementAndGet();
                                pool.execute(new Runnable() {
                                        @Override
                                    public void run() {
                                        list(child);
                                    }
                                });
                            }else {
                                callback.execute(child);
                                //System.out.println(child.getPath());
                            }
                        }
                    }
                }
            } finally {
    //            synchronized (this) {
    //                count--;
    //                if (count == 0) {
    //                    this.notifyAll();
    //                }
    //            }
                if (count.decrementAndGet() == 0) {
                    latch.countDown();
                }
            }
        }
    }
}
