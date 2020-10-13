import Jenkins.JenkinsApi;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.yaml.snakeyaml.Yaml;

public class Application {
  public static void main(String[] args) {
//    //创建任务
//    JobDetail jobDetail = JobBuilder.newJob(LoopJenkins.class).withIdentity("job1", "group1").build();
//    //创建触发器 每10秒钟执行一次
//    Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger1", "group3")
//        .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).repeatForever())
//        .build();
//    //创建调度器
//    SchedulerFactory schedulerFactory = new StdSchedulerFactory();
//    Scheduler scheduler = schedulerFactory.getScheduler();
//    //将任务及其触发器放入调度器
//    scheduler.scheduleJob(jobDetail, trigger);
//    //调度器开始调度任务
//    scheduler.start();
//
//    Thread thread = new Thread(new ThreadB());
//    thread.start();

    String Jenkins_Crumb = null;
    String newapiTokenValue = null;
    try {
      String[] cmds = new String[]{"/bin/sh", "-c", "curl 3.112.71.60:8080/crumbIssuer/api/xml?xpath=concat\\(//crumbRequestField,%22:%22,//crumb\\) -c cookies.txt --user 'admin:Zsy950108'"};


      Process ps = Runtime.getRuntime().exec(cmds);
      System.out.println("=====get jenkins_Crumb===");

      BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
      StringBuffer sb = new StringBuffer();
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
      Jenkins_Crumb = sb.toString();

      System.out.println(Jenkins_Crumb);


      String[] generateFreshToken = new String[]{"/bin/sh", "-c", "curl '3.112.71.60:8080/user/admin/descriptorByName/jenkins.security.ApiTokenProperty/generateNewToken'  --data 'newTokenName=fresh-reload-token'  --user 'admin:Zsy950108' -b cookies.txt  -H " + Jenkins_Crumb + ""};
      System.out.println("=====generate new api token===");
      Process gengerateNewToken = Runtime.getRuntime().exec(generateFreshToken);
      BufferedReader gengerateNewTokenData = new BufferedReader(new InputStreamReader(gengerateNewToken.getInputStream()));
      StringBuffer apiToken = new StringBuffer();
      String apiTokens;
      while ((apiTokens = gengerateNewTokenData.readLine()) != null) {
        apiToken.append(apiTokens);
      }
      JSONObject apitokenJsonObject = JSONObject.fromObject(apiToken.toString());
      newapiTokenValue = getFieldListFromJsonStr(apitokenJsonObject.get("data").toString(),"tokenValue").get(0);


      System.out.println("newApiToken:" + newapiTokenValue);

    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      String[] cmd = new String[] { "/bin/sh", "-c", "curl -X POST http://admin:"+ newapiTokenValue + "@3.112.71.60:8080/configuration-as-code/export -H " + Jenkins_Crumb  +  "> jenkins.yaml"};
      System.out.println("exec configuration as code");

      Process ps = Runtime.getRuntime().exec(cmd);
      BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
      StringBuffer sb = new StringBuffer();
      String line;
      while ((line = br.readLine()) != null) {
//        sb.append(line).append("\n");
        sb.append(line);

      }

      System.out.println(sb.toString());



    } catch (Exception e) {
      e.printStackTrace();
    }

      // export current casc_jenkins_yaml
//    File file = new File("");
//    try {
//      String filePath = file.getCanonicalPath();
//      System.out.println("filePath" + filePath);
//      File jenkinsConfiguration = new File(filePath + "/"+ "jenkins.yaml");
//
//      if (!jenkinsConfiguration.exists()) {
//        System.out.println("创建Yaml 文件" + jenkinsConfiguration);
//        jenkinsConfiguration.createNewFile();
//      }
//
//      String[] cmds = { "/bin/bash", "-c", String.format("scp -i %s ubuntu@3.112.71.60:/var/jenkins_home/casc_configs/jenkins.yaml %s",filePath + "/"+ "eastbay-aws-key", filePath + "/"+ "jenkins.yaml") };
//
//      ProcessBuilder process = new ProcessBuilder(cmds);
//      Process p;
//      try {
//        p = process.start();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//        StringBuilder builder = new StringBuilder();
//        String line = null;
//        while ((line = reader.readLine()) != null) {
//          builder.append(line);
//          builder.append(System.getProperty("line.separator"));
//        }
//
//        System.out.print(builder.toString());
//
//      } catch (IOException e) {
//        System.out.print("error");
//        e.printStackTrace();
//      }
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
    // create the new ec2 through template id
    String instance_id = null;
    try {
      String[] createEc2 = new String[] { "/bin/sh", "-c", "aws ec2 run-instances \\\n"
          + "    --launch-template LaunchTemplateId=lt-0a285a0e2349f4228,Version=11"};

      Process ps = Runtime.getRuntime().exec(createEc2);
      System.out.println("====launch ec2 through template====");

      BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
      StringBuffer sb = new StringBuffer();
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
//      instance_id = sb.toString();
      JSONObject instanceInfoJson = JSONObject.fromObject(sb.toString());

      instance_id = getFieldListFromJsonStr(instanceInfoJson.getJSONArray("Instances").get(0).toString(),"InstanceId").get(0);

      System.out.println("instance_id:" + instance_id);

      String[] waitInitial = new String[] { "/bin/sh", "-c", "aws ec2 wait instance-running --instance-ids" + instance_id};

      Process ps2 = Runtime.getRuntime().exec(waitInitial);

      System.out.println("====waiting finished====");
      BufferedReader br2 = new BufferedReader(new InputStreamReader(ps2.getInputStream()));
      StringBuffer sb2 = new StringBuffer();
      String line2;
      while ((line2 = br2.readLine()) != null) {
        sb2.append(line2);
      }
      System.out.println(sb2.toString());
      Thread.sleep(1000);


    } catch (Exception e) {
      e.printStackTrace();
    }

    // get public ip for the created ec2
    String resultIp = null;
    try {
      System.out.println("instance_id before get public ip" + instance_id);
      String[] cmd = new String[] { "/bin/sh", "-c", "aws ec2 describe-instances --instance-ids" + " " + instance_id +  " --query 'Reservations[*].Instances[*].PublicIpAddress' --output text"};

      System.out.println("cmd:" + cmd.toString());

      Process ps = Runtime.getRuntime().exec(cmd);
      System.out.println("=====fetch Public Ip Address of new ec2===");

      BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
      StringBuffer sb = new StringBuffer();
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
      resultIp = sb.toString();

      System.out.println("resultIp:" + resultIp);

    } catch (Exception e) {
      e.printStackTrace();
    }

    // update new jenkins.yaml
    Map m1 = null;
    Map m2 = null;
    List m3 = null;
    LinkedHashMap<String,LinkedHashMap<String,LinkedHashMap<String,LinkedHashMap<String,String>>>> newDockerCloud = new LinkedHashMap<String,LinkedHashMap<String,LinkedHashMap<String,LinkedHashMap<String,String>>>>();
    Map m4;
    Yaml yaml = new Yaml();
    File f=new File("jenkins.yaml");
    Object result= null;
    try {
      m1 = (Map) yaml.load(new FileInputStream(f));
      //获取第一级键中的“details”键作为对象，进一步获取下级的键和值
      m2 = (Map) m1.get("jenkins");
      m3 =  (List) m2.get("clouds");
//      m31 = (LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>>>) m3.get(0);
      newDockerCloud = (LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>>>) deepClone(m3.get(0));
      System.out.println(" new docker cloud:" + newDockerCloud.get("docker").get("dockerApi").get("dockerHost").get("uri"));
//      LinkedHashMap<String,String> updatedUrl = newDockerCloud.get("docker").get("dockerApi").get("dockerHost");
      LinkedHashMap<String,LinkedHashMap<String,String>> updatedUrl = newDockerCloud.get("docker").get("dockerApi");

      LinkedHashMap<String,String> uri = new LinkedHashMap<>();
      uri.put("uri","tcp://" + resultIp + ":4243");
//      updatedUrl.put("uri", resultIp);
      updatedUrl.put("dockerHost", uri);

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    m3.add(newDockerCloud);


    FileWriter fw;
    try {
      fw = new FileWriter(f);
      //用snakeyaml的dump方法将map类解析成yaml内容
      fw.write(yaml.dump(m1));
      //写入到文件中
      fw.flush();
      fw.close();

    } catch (IOException e) {
      e.printStackTrace();
    }

     // upload new yaml
    try {
      String[] uploadNewYaml = new String[]{"/bin/sh", "-c", "scp -i ~/.ssh/eastbay-aws-eb jenkins.yaml ubuntu@3.112.71.60:/var/jenkins_home/casc_configs/jenkins.yaml"};


      Process ps = Runtime.getRuntime().exec(uploadNewYaml);
      System.out.println("=====upload your new jenkins yaml===");

      BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
      StringBuffer sb = new StringBuffer();
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
      resultIp = sb.toString();

      System.out.println("upload finsihed:");

    } catch (Exception e) {
      e.printStackTrace();
    }

    // trigger configuration yaml reload.
    try {
      String[] cmd = new String[] { "/bin/sh", "-c", "curl -X POST http://admin:"+ newapiTokenValue + "@3.112.71.60:8080/configuration-as-code/reload -H" + Jenkins_Crumb + ""};


      Process ps = Runtime.getRuntime().exec(cmd);

    } catch (Exception e) {
      e.printStackTrace();
    }


//    try {
//      YamlConfig yamlConfig = new YamlConfig();
//      yamlConfig.setAllowDuplicates(false); // default value is true
//      YamlReader reader = new YamlReader(new FileReader("jenkins.yaml"), yamlConfig);
//      Object object = reader.read();
//      System.out.println(object);
////      Map map = (Map)object;
//      Map<String, Map<String, List<String>>> resultMap = (Map<String, Map<String, List<String>>>) object;
////      Map<String, Map<String, Map<String, String>>> resultMap = (Map<String, Map<String, Map<String, String>>>) object;
//      System.out.println(resultMap.get("jenkins").get("clouds").getClass());
//
////      LinkedHashMap<String,String> lists = (LinkedHashMap<String, String>) resultMap.get("jenkins").get("clouds");
////      System.out.println(resultMap.get("jenkins").get("clouds").toArray()[0]);
//      Object[] yamlReadFile = resultMap.get("jenkins").get("clouds").toArray();
//      for (int i = 0 ; i <  yamlReadFile.length; i ++) {
//        System.out.println("docker" + i + ":" + yamlReadFile[i] + '\n');
//      }
//    } catch (YamlException ex) {
//      ex.printStackTrace();
//      // or handle duplicate key case here according to your business logic
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//    }

//    String[] cmds = {"scp -i ~/.ssh/eastbay-aws-eb ubuntu@3.112.71.60:/var/jenkins_home/casc_configs/jenkins.yaml jenkins.yaml"};

//    ProcessBuilder process = new ProcessBuilder(cmds);
//    Process p;
//    try {
//      p = process.start();
//      BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//      StringBuilder builder = new StringBuilder();
//      String line = null;
//      while ((line = reader.readLine()) != null) {
//        builder.append(line);
//        builder.append(System.getProperty("line.separator"));
//      }
//
//      System.out.print(builder.toString());
//
//    } catch (IOException e) {
//      System.out.print("error");
//      e.printStackTrace();
//    }

    ThreadBoy boy = new ThreadBoy();
    boy.start();
  }

  static class ThreadBoy extends Thread{
    @Override
    public void run() {

      while (true) {
        try {
          Thread.sleep(10000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        System.out.println("thread is running taskNumber");


        JenkinsApi jenkinsApi = new JenkinsApi();
        Date date = new Date();
        try {
          System.out.println(date.toString());
          System.out.print(jenkinsApi.jenkinsServer.getQueue().getItems().size());
          if (jenkinsApi.jenkinsServer.getQueue().getItems().size() == 1 ) {
            System.out.println("--------------ansible playbook---------");
            System.out.println("need to use ansible auto scale ec2 instance");
//            System.out.println("男孩和女孩准备出去逛街");

            ThreadGirl girl = new ThreadGirl();
            girl.start();

            try {
              girl.join();
            } catch (InterruptedException e) {
              e.printStackTrace();
            }

            System.out.println("Jenkins loop again");
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  static class ThreadGirl extends Thread{

    @Override
    public void run() {
      int time = 15000;

      System.out.println("auto scale ec2 instance and auto configure。。。");

      try {
        Thread.sleep(time);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      System.out.println("auto scale ec2 instance and auto configure finish" + time);

    }
  }


  public static Object deepClone(Object obj) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(obj);

      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      ObjectInputStream ois = new ObjectInputStream(bais);
      return ois.readObject();
    } catch (IOException e) {
      return null;
    } catch (ClassNotFoundException e) {
      return null;
    }
  }


  public static List<String> getFieldListFromJsonStr(String jsonStr, String fieldName) {
    List<String> fieldValues = new ArrayList<>();
    String regex = "(?<=(\"" + fieldName + "\":\")).*?(?=(\"))";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(jsonStr);
    while (matcher.find()) {
      if (StringUtils.isNotEmpty(matcher.group().trim())) {
        fieldValues.add(matcher.group().trim());
      }
    }
    return fieldValues;
  }


}
