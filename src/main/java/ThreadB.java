import Jenkins.JenkinsApi;
import java.io.IOException;
import java.util.Date;

public class ThreadB extends Thread {

  private int taskNumber = 1;

  //无意义
  private final Object lock = new Object();

  //标志线程阻塞情况
  private boolean pause = false;


  /**
   * 设置线程是否阻塞
   */
  public void pauseThread() {
    this.pause = true;
  }

  /**
   * 调用该方法实现恢复线程的运行
   */
  public void resumeThread() {
    this.pause = false;
    synchronized (lock) {
      //唤醒线程
      lock.notify();
    }
  }

  /**
   * 这个方法只能在run 方法中实现，不然会阻塞主线程，导致页面无响应
   */
  void onPause() {
    synchronized (lock) {
      try {
        //线程 等待/阻塞
        lock.wait();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }


  @Override
  public void run() {
    while (true) {
      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("thread is running taskNumber" +taskNumber);
      JenkinsApi jenkinsApi = new JenkinsApi();
      Date date = new Date();
      try {
        System.out.println(date.toString());
        System.out.print(jenkinsApi.jenkinsServer.getQueue().getItems().size());
        if (taskNumber == 1 ) {
          System.out.println("--------------ansible playbook---------");
          System.out.println("need to use ansible auto scale ec2 instance");
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

}
