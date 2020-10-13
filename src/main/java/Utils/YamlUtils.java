package Utils;

import Jenkins.ViewApi;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class YamlUtils {

  private YamlUtils(){}

  public void readYaml() {
//    Yaml yaml = new Yaml();
//    InputStream inputStream = this.getClass()
//        .getClassLoader()
//        .getResourceAsStream("jenkins.yaml");
//    Map<String, Map<String, List<String>>> resultMap = (Map<String, Map<String, List<String>>>) yaml.load(inputStream);
////    Map<String, Object> obj = yaml.load(inputStream);
//    System.out.println("test:" + resultMap.toString());
  }

  public static void main(String[] args) {
    // 创建视图
    //viewApi.createView();
    // 获取视图信息
    //viewApi.getView();
    // 获取视图配置xml信息
    //viewApi.getViewConfig();
    // 更新视图信息
    //viewApi.updateView();
    // 删除视图
    //viewApi.deleteView();
    new YamlUtils().readYaml();
  }

}
