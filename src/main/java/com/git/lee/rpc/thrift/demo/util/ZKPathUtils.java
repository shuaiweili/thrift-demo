package com.git.lee.rpc.thrift.demo.util;

/**
 * @author LISHUAIWEI
 * @date 2018/2/11 10:48
 */
public class ZKPathUtils {
    public static String CLUSTER_PATH = "/cluster";

    public static String getNodePath(String nodePath) {
        return CLUSTER_PATH + "/" + nodePath;
    }

    public static String splitToName(String path) {
        String[] paths = path.split("/");
        return paths[paths.length - 1];
    }
}
