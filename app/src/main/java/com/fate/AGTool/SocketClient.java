package com.fate.AGTool;


import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.Executors;


/** SocketClient.java: 对Socket进行简单接口封装，便于使用。
 *
 * 用法：
 * client = new SocketClient(print, ipString, port);	// 创建客户端Socket操作对象
 * client.start();										// 连接服务器
 * client.Send(data);									// 发送信息
 * client.disconnect();									// 断开连接
 *
 * ----- 2019-6-18 下午5:36:25 scimence */
public class SocketClient
{
    public String ipString = "localhost";   // 服务器端ip
    public int port = 8688;                // 服务器端口

    public Socket socket;
    public SocketCallBack call;				// 数据接收回调方法

    public SocketClient(SocketCallBack print, String ipString, int port)
    {
        this.call = print;
        if (ipString != null) this.ipString = ipString;
        if (port >= 0) this.port = port;
    }

    /** 创建Socket并连接 */
    public void start()
    {
        if (socket != null && socket.isConnected()) return;

        Executors.newCachedThreadPool().execute(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if (socket == null)
                    {
                        InetAddress ip = InetAddress.getByName(ipString);
                        socket = new Socket(ip, port);

                        if (call != null) call.Print("启动成功 -> " + ip + ":" + port);
                    }
                }
                catch (Exception ex)
                {
                    if (call != null) call.Print("启动失败 " + ex.toString()); // 连接失败
                }

                // Socket接收数据
                try
                {
                    if (socket != null)
                    {
                        InputStream inputStream = socket.getInputStream();

                        // 1024 * 1024 * 3 = 3145728
                        byte[] buffer = new byte[3145728];		// 3M缓存
                        int len = -1;
                        while (socket.isConnected() && (len = inputStream.read(buffer)) != -1)
                        {
                            String data = new String(buffer, 0, len);

                            // 通过回调接口将获取到的数据推送出去
                            if (call != null)
                            {
                                call.Print(data);
                            }
                        }

                    }
                }
                catch (Exception ex)
                {
                    if (call != null) call.Print("启动失败，无法使用此功能" + ex.toString()); // 连接失败
                    socket = null;
                }
            }
        });

    }

    /** 发送信息 */
    public void Send(String data)
    {
        try
        {
            if(socket != null && socket.isConnected())
            {
                byte[] bytes = data.getBytes();
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(bytes);
                outputStream.flush();

                if (call != null) call.Print("发送信息 -> " + data);
            }
            else
            {
                if (call != null) call.Print("启动失败，无法使用此功能。");
            }
        }
        catch (Exception ex)
        {
            if (call != null) call.Print("发送socket信息失败！"+ex.getMessage());
        }
    }

    /** 断开Socket */
    public void disconnect()
    {
        try
        {
            if (socket != null && socket.isConnected())
            {
                socket.close();
                socket = null;

                if (call != null) call.Print("服务器已断开！ ");

            }
        }
        catch (Exception ex)
        {
            if (call != null) call.Print("断开socket失败!");
        }
    }
}
