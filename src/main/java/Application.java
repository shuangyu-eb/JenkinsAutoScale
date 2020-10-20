import static DomXml.DomXml.addNode;
import static DomXml.DomXml.createXml;

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


    scaleOn();
    ThreadBoy boy = new ThreadBoy();
    boy.start();
  }

  public static void scaleOn() {
    File file=new File("ec2_creation_history.xml");
    if(!file.exists()){
      createXml();
      System.out.println("文件已创建");
    }else{
      System.out.println("文件已存在");
    }

    String Jenkins_Crumb = null;
    String newapiTokenValue = null;
    try {
      String[] generateJenkinsCrumb = new String[]{"/bin/sh", "-c", "curl 3.112.71.60:8080/crumbIssuer/api/xml?xpath=concat\\(//crumbRequestField,%22:%22,//crumb\\) -c cookies.txt --user 'admin:Zsy950108'"};
      System.out.println("=====get jenkins_Crumb===");
      Jenkins_Crumb = excuteCmmandLine(generateJenkinsCrumb);

      System.out.println(Jenkins_Crumb);


//      String[] generateFreshToken = new String[]{"/bin/sh", "-c", "curl '3.112.71.60:8080/user/admin/descriptorByName/jenkins.security.ApiTokenProperty/generateNewToken'  --data 'newTokenName=fresh-reload-token'  --user 'admin:Zsy950108' -b cookies.txt  -H " + Jenkins_Crumb + ""};
      String[] generateFreshToken = new String[]{"/bin/sh", "-c", "curl '3.112.71.60:8080/user/admin/descriptorByName/jenkins.security.ApiTokenProperty/generateNewToken'  --data 'newTokenName=fresh-reload-token'  --user 'admin:Zsy950108' -b cookies.txt  -H " + Jenkins_Crumb + ""};

      System.out.println("=====generate new api token===");

      newapiTokenValue = getFieldListFromJsonStr(JSONObject.fromObject(excuteCmmandLine(generateFreshToken)).get("data").toString(),"tokenValue").get(0);

      System.out.println("newApiToken:" + newapiTokenValue);

    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      String[] exportJenkinsConfigurationYaml = new String[] { "/bin/sh", "-c", "curl -X POST http://admin:"+ newapiTokenValue + "@3.112.71.60:8080/configuration-as-code/export -H " + Jenkins_Crumb  +  "> jenkins.yaml"};
      System.out.println("======exec configuration as code.yaml form jenkins master=====");

      excuteCmmandLine(exportJenkinsConfigurationYaml);
    } catch (Exception e) {
      e.printStackTrace();
    }

    // create the new ec2 through template id
    String instance_id = null;
    try {
      String[] createEc2 = new String[] { "/bin/sh", "-c", "aws ec2 run-instances \\\n"
          + "    --launch-template LaunchTemplateId=lt-00b39ee5f6d0fd996,Version=7"};

      JSONObject instanceInfoJson = JSONObject.fromObject(excuteCmmandLine(createEc2));

      instance_id = getFieldListFromJsonStr(instanceInfoJson.getJSONArray("Instances").get(0).toString(),"InstanceId").get(0);

      System.out.println("instance_id:" + instance_id);

      String[] waitInitial = new String[] { "/bin/sh", "-c", "aws ec2 wait instance-running --instance-ids" + instance_id};


      System.out.println("====waiting finished====");
      excuteCmmandLine(waitInitial);
      Thread.sleep(1000);


    } catch (Exception e) {
      e.printStackTrace();
    }

    // get public ip for the created ec2
    String resultIp = null;
    try {
      System.out.println("instance_id before get public ip" + instance_id);
      String[] getPublicId = new String[] { "/bin/sh", "-c", "aws ec2 describe-instances --instance-ids" + " " + instance_id +  " --query 'Reservations[*].Instances[*].PublicIpAddress' --output text"};

      String[] getLaunchTime = new String[] { "/bin/sh", "-c", "aws ec2 describe-instances --instance-ids" + " " + instance_id +  " --query 'Reservations[*].Instances[*].LaunchTime' --output text"};


      System.out.println("resultIp:" + excuteCmmandLine(getPublicId));
      resultIp = excuteCmmandLine(getPublicId);
      addNode("ec2_creation_history.xml",instance_id,excuteCmmandLine(getPublicId),excuteCmmandLine(getLaunchTime));

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


      System.out.println("=====upload your new jenkins yaml===");

      excuteCmmandLine(uploadNewYaml);
      System.out.println("upload finsihed:");

    } catch (Exception e) {
      e.printStackTrace();
    }

    // trigger configuration yaml reload.
    try {
      System.out.println("=====trigger configuration yaml reload===");

      String[] triggerReload = new String[] { "/bin/sh", "-c", "curl -X POST http://admin:"+ newapiTokenValue + "@3.112.71.60:8080/configuration-as-code/reload -H" + Jenkins_Crumb + ""};

      excuteCmmandLine(triggerReload);

    } catch (Exception e) {
      e.printStackTrace();
    }
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

  public static String excuteCmmandLine(String[] cmmandLine) {
    Process ps = null;
    StringBuffer sb = new StringBuffer();

    try {
      ps = Runtime.getRuntime().exec(cmmandLine);

      BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
      System.out.println("excuteCommandLineResult:" + sb);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return String.valueOf(sb);
  }


}
