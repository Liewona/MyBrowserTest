package com.example.myviabrowser.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

public class URLUtils {

    public static String doGet(String urlStr) {
        HttpURLConnection connection = null;
        InputStream inStream = null;
        BufferedReader reader = null;
        String result = null;


        try {
            //创建远程url连接对象
            URL url = new URL(urlStr);
            //通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) url.openConnection();
            //设置User-Agent
            connection.setRequestProperty("User-agent", "BiLiBiLi Test Client/1.0.0 (718402096@qq.com)");
            //设置连接方式
            connection.setRequestMethod("GET");
            //设置连接主机服务器超时时间
            connection.setConnectTimeout(15000);
            //设置读取远程返回的数据时间
            connection.setReadTimeout(60000);
            //发送请求
            connection.connect();
            //通过connection连接，验证是否请求成功
            if(connection.getResponseCode() == 200) {
                //获取输入流
                inStream = connection.getInputStream();
                //封装输入流inStream，并指定字符集
                reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"));
                //存放数据
                StringBuffer strBuf = new StringBuffer();
                String temp = null;

                while ((temp = reader.readLine()) != null) {
                    strBuf.append(temp);
                }
                result = strBuf.toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //关闭流
            if(inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            connection.disconnect();

        }
        return result;
    }


    /**

     * 判断一个字符串是否为url

     * @param str String 字符串

     * @return boolean 是否为url

     * @author peng1 chen

     * **/

    public static boolean isURL(String str){

        //转换为小写

        str = str.toLowerCase();

        String regex = "^((https|http|ftp|rtsp|mms)?://)"  //https、http、ftp、rtsp、mms

                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@

                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 例如：199.194.52.184

                + "|" // 允许IP和DOMAIN（域名）

                + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.

                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名

                + "[a-z]{2,6})" // first level domain- .com or .museum

                + "(:[0-9]{1,5})?" // 端口号最大为65535,5位数

                + "((/?)|" // a slash isn't required if there is no file name

                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";

        return  str.matches(regex);

    }

    public static String getSchemeUrl(String urlStr) {
        String schemeUrl = null;
        if(urlStr.toLowerCase().startsWith("http://") ||  urlStr.toLowerCase().startsWith("https://")) {
            schemeUrl = urlStr;
        } else {
            schemeUrl = "http://" + urlStr;
        }
        return schemeUrl;
    }


    public static boolean canConnectionByHttp(String urlStr) {
        if(! isURL(urlStr)) {
            return false;
        }
        HttpURLConnection connection;
        String schemeUrl;
        try {
            schemeUrl = getSchemeUrl(urlStr);

            ThreadUtils.setInUIThread(() -> {
                Log.d("mylog", "canConnectionByHttp: " + schemeUrl);
            });

            URL url = new URL(schemeUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            int code = connection.getResponseCode();
            if(code >= 100 && code <= 400) {
                return true;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static boolean canConnectionBySocket(String urlStr) {
        URL url;
        String host;
        int port;
        Socket socket = null;
        try {
            url = new URL(urlStr);
            host = url.getHost();
            port = url.getPort();
            if (port == -1) {
                port = 80;
            }

            socket = new Socket();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getByName(host), port);
            socket.connect(inetSocketAddress, 5000);
            if(socket.isConnected()) {
                return true;
            } else {
                return false;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (socket != null && ! socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
