package Jenkins;
import Utils.JenkinsConnect;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.*;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

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

  public List<String> getSubUtil(String soap,String rgex){
    List<String> list = new ArrayList<String>();
    Pattern pattern = Pattern.compile(rgex);// 匹配的模式
    Matcher m = pattern.matcher(soap);
    while (m.find()) {
      int i = 1;
      list.add(m.group(i));
      i++;
    }
    return list;
  }
  public static void main(String[] args) throws IOException {
  }

}
