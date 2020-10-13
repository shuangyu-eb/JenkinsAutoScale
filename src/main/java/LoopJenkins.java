import Jenkins.JenkinsApi;
import java.io.IOException;
import java.util.Date;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class LoopJenkins implements Job{

  private boolean ansibleExcuting;

  public void execute(JobExecutionContext arg0) throws JobExecutionException {
    JenkinsApi jenkinsApi = new JenkinsApi();
    Date date = new Date();
    try {
      System.out.println(date.toString());
      System.out.print(jenkinsApi.jenkinsServer.getQueue().getItems().size());
      if (jenkinsApi.jenkinsServer.getQueue().getItems().size() == 1 ) {
        System.out.println("--------------ansible playbook---------");
        System.out.println("need to use ansible auto scale ec2 instance");
        ansibleExcuting = true;
        Thread.sleep(30000);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


}
