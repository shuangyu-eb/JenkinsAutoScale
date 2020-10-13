package Jenkins;
import Utils.JenkinsConnect;
import java.io.BufferedReader;
import java.io.IOException;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.*;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

/**
 * 获取 Jenkins 相关信息
 *
 * 例如获取插件信息、获取Label信息、关闭Jenkins等
 */
public class JenkinsApi {

  // Jenkins 对象
  public JenkinsServer jenkinsServer;

  /**
   * 构造方法中调用连接 Jenkins 方法
   */
  public JenkinsApi(){
    jenkinsServer = JenkinsConnect.connection();
  }

  /**
   * 获取主机信息
   */
  public void getComputerInfo() {
    try {
      Map<String, Computer> map = jenkinsServer.getComputers();
      for (Computer computer : map.values()) {
        // 获取当前节点-节点名称
        System.out.println(computer.details().getDisplayName());
        // 获取当前节点-执行者数量
        System.out.println(computer.details().getNumExecutors());
        // 获取当前节点-执行者详细信息
        List<Executor> executorList = computer.details().getExecutors();
        // 查看当前节点-是否脱机
        System.out.println(computer.details().getOffline());
        // 获得节点的全部统计信息
        LoadStatistics loadStatistics = computer.details().getLoadStatistics();
        // 获取节点的-监控数据
        Map<String, Map> monitorData = computer.details().getMonitorData();
        //......
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 重启 Jenkins
   */
  public void restart() {
    try {
      jenkinsServer.restart(true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 安全重启 Jenkins
   */
  public void safeRestart() {
    try {
      jenkinsServer.safeRestart(true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 安全结束 Jenkins
   */
  public void safeExit() {
    try {
      jenkinsServer.safeExit(true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 关闭 Jenkins 连接
   */
  public void close() {
    jenkinsServer.close();
  }

  /**
   * 根据 Label 查找代理节点信息
   */
  public void getLabelNodeInfo() {
    try {
      LabelWithDetails labelWithDetails = jenkinsServer.getLabel("jnlp-agent");
      // 获取标签名称
      System.out.println(labelWithDetails.getName());
      // 获取 Cloud 信息
      System.out.println(labelWithDetails.getClouds());
      // 获取节点信息
      System.out.println(labelWithDetails.getNodeName());
      // 获取关联的 Job
      System.out.println(labelWithDetails.getTiedJobs());
      // 获取参数列表
      System.out.println(labelWithDetails.getPropertiesList());
      // 是否脱机
      System.out.println(labelWithDetails.getOffline());
      // 获取描述信息
      System.out.println(labelWithDetails.getDescription());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 判断 Jenkins 是否运行
   */
  public void isRunning() {
    boolean isRunning = jenkinsServer.isRunning();
    System.out.println(isRunning);
  }

  /**
   * 获取 Jenkins 插件信息
   */
  public void getPluginInfo(){
    try {
      PluginManager pluginManager =jenkinsServer.getPluginManager();
      // 获取插件列表
      List<Plugin> plugins = pluginManager.getPlugins();
      for (Plugin plugin:plugins){
        // 插件 wiki URL 地址
        System.out.println(plugin.getUrl());
        // 版本号
        System.out.println(plugin.getVersion());
        // 简称
        System.out.println(plugin.getShortName());
        // 完整名称
        System.out.println(plugin.getLongName());
        // 是否支持动态加载
        System.out.println(plugin.getSupportsDynamicLoad());
        // 插件依赖的组件
        System.out.println(plugin.getDependencies());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    // 创建 JenkinsApi 对象，并在构造方法中连接 Jenkins
    JenkinsApi jenkinsApi = new JenkinsApi();
    // 重启 Jenkins
    //jenkinsApi.restart();
    // 安全重启 Jenkins
    //jenkinsApi.safeRestart();
    // 获取节点信息
    //jenkinsApi.getComputerInfo();
    // 安全结束 Jenkins
    //jenkinsApi.safeExit();
    // 关闭 Jenkins 连接
    //jenkinsApi.close();
    // 获取 Label 节点信息
//    try {
//      String[] cmd = new String[] { "/bin/sh", "-c", "ls -l" };
//      Process ps = Runtime.getRuntime().exec(cmd);
//
//      BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
//      StringBuffer sb = new StringBuffer();
//      String line;
//      while ((line = br.readLine()) != null) {
//        sb.append(line).append("\n");
//      }
//      String result = sb.toString();
//      System.out.println("result:" + result);
//
//
//    } catch (Exception e) {
//      e.printStackTrace();
//    }

//    String[] cmds = {"curl", "http://3.112.71.60:8080/queue/api/json?pretty=true", "--user", "admin:Zsy950108"};
//    String[] cmds = {"curl", "-u admin:Zsy950108", "-d", "script=println Hudson.instance.queue.items.length"};
//    try {
//      System.out.print(jenkinsApi.jenkinsServer.getQueue().getItems().size());
//      System.out.print(jenkinsApi.jenkinsServer.getComputers().toString());
//
//    } catch (IOException e) {
//      e.printStackTrace();
//    }


//    String json = "{    \"Groups\": [],    \"Instances\": [        {            \"AmiLaunchIndex\": 0,            \"ImageId\": \"ami-02b658ac34935766f\",            \"InstanceId\": \"i-03c2a097424f42f22\",            \"InstanceType\": \"t2.micro\",            \"KeyName\": \"eastbay-aws-eb\",            \"LaunchTime\": \"2020-10-11T15:52:42.000Z\",            \"Monitoring\": {                \"State\": \"disabled\"            },            \"Placement\": {                \"AvailabilityZone\": \"ap-northeast-1a\",                \"GroupName\": \"\",                \"Tenancy\": \"default\"            },            \"PrivateDnsName\": \"ip-172-31-36-105.ap-northeast-1.compute.internal\",            \"PrivateIpAddress\": \"172.31.36.105\",            \"ProductCodes\": [],            \"PublicDnsName\": \"\",            \"State\": {                \"Code\": 0,                \"Name\": \"pending\"            },            \"StateTransitionReason\": \"\",            \"SubnetId\": \"subnet-296e2060\",            \"VpcId\": \"vpc-14933873\",            \"Architecture\": \"x86_64\",            \"BlockDeviceMappings\": [],            \"ClientToken\": \"0398e58e-5125-4f1a-86e5-f306ebdeb8bb\",            \"EbsOptimized\": false,            \"EnaSupport\": true,            \"Hypervisor\": \"xen\",            \"NetworkInterfaces\": [                {                    \"Attachment\": {                        \"AttachTime\": \"2020-10-11T15:52:42.000Z\",                        \"AttachmentId\": \"eni-attach-034de9eaa72c944ef\",                        \"DeleteOnTermination\": true,                        \"DeviceIndex\": 0,                        \"Status\": \"attaching\"                    },                    \"Description\": \"\",                    \"Groups\": [                        {                            \"GroupName\": \"default\",                            \"GroupId\": \"sg-9e1b65e7\"                        }                    ],                    \"Ipv6Addresses\": [],                    \"MacAddress\": \"06:25:08:1e:58:78\",                    \"NetworkInterfaceId\": \"eni-00bfb908560bea64f\",                    \"OwnerId\": \"533423936407\",                    \"PrivateDnsName\": \"ip-172-31-36-105.ap-northeast-1.compute.internal\",                    \"PrivateIpAddress\": \"172.31.36.105\",                    \"PrivateIpAddresses\": [                        {                            \"Primary\": true,                            \"PrivateDnsName\": \"ip-172-31-36-105.ap-northeast-1.compute.internal\",                            \"PrivateIpAddress\": \"172.31.36.105\"                        }                    ],                    \"SourceDestCheck\": true,                    \"Status\": \"in-use\",                    \"SubnetId\": \"subnet-296e2060\",                    \"VpcId\": \"vpc-14933873\",                    \"InterfaceType\": \"interface\"                }            ],            \"RootDeviceName\": \"/dev/sda1\",            \"RootDeviceType\": \"ebs\",            \"SecurityGroups\": [                {                    \"GroupName\": \"default\",                    \"GroupId\": \"sg-9e1b65e7\"                }            ],            \"SourceDestCheck\": true,            \"StateReason\": {                \"Code\": \"pending\",                \"Message\": \"pending\"            },            \"Tags\": [                {                    \"Key\": \"aws:ec2launchtemplate:version\",                    \"Value\": \"8\"                },                {                    \"Key\": \"aws:ec2launchtemplate:id\",                    \"Value\": \"lt-0a285a0e2349f4228\"                }            ],            \"VirtualizationType\": \"hvm\",            \"CpuOptions\": {                \"CoreCount\": 1,                \"ThreadsPerCore\": 1            },            \"CapacityReservationSpecification\": {                \"CapacityReservationPreference\": \"open\"            },            \"MetadataOptions\": {                \"State\": \"pending\",                \"HttpTokens\": \"optional\",                \"HttpPutResponseHopLimit\": 1,                \"HttpEndpoint\": \"enabled\"            }        }    ],    \"OwnerId\": \"533423936407\",    \"ReservationId\": \"r-03143134672b39c64\"}\n";
//    JSONObject json_test = JSONObject.fromObject(json);
//    System.out.println(json_test.getJSONArray("Instances").get(0));
////    json_test.getJSONObject("Instances");
//    JSONArray json_test2 = JSONArray.fromObject(json_test.get("Instances"));
//    System.out.println(json_test.get("json_test2"+ json_test2));
//    System.out.println(getFieldListFromJsonStr(json_test.getJSONArray("Instances").get(0).toString(),"InstanceId").get(0));
//    ProcessBuilder process = new ProcessBuilder(cmds);
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
////        System.out.print(JSONObject.fromObject(builder.toString()));
////        System.out.print(JSONObject.fromObject(builder.toString()).get("items"));
//
//        System.out.print(builder.toString());
//
//      } catch (IOException e) {
//        System.out.print("error");
//        e.printStackTrace();
//      }

    String Jenkins_Crumb = null;
    String newApiToken = null;
//    String instance_id = "i-09e3fedea74d5b255";
    try {
//      String[] cmd = new String[] { "/bin/sh", "-c", "aws ec2 describe-instances --instance-ids i-09e3fedea74d5b255 --query 'Reservations[*].Instances[*].PublicIpAddress' --output text"};
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
      newApiToken = apiToken.toString();

      JSONObject apitokenJsonObject = JSONObject.fromObject(apiToken.toString());

      System.out.println("apitokenValue:" + getFieldListFromJsonStr(apitokenJsonObject.get("data").toString(),"tokenValue").get(0));

      System.out.println("newApiToken:" + newApiToken);

    } catch (Exception e) {
      e.printStackTrace();
    }


//    jenkinsApi.getLabelNodeInfo();
    // 查看 Jenkins 是否允许
//    jenkinsApi.isRunning();
    // 获取 Jenkins 插件信息
//    jenkinsApi.getPluginInfo();
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
