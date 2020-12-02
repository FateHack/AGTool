# AGTool

## 介绍

我们可以用AGTool干些什么？

- dump so(对于32位so自动修复，利于ida分析)，dump il2cpp 中的global-metadata.dat
- 扫描目标so中inlinehook 的地址(暂只支持32位)
- 动态修改目标so中指定偏移地址的指令码

### 使用方法

1. #### **权限**

   - root权限
   - 读写权限
   - 最上层显示权限

2. #### 注意事项

   **安装以后第一次启动，给予以上权限，然后退出一次，重新进入app（ps:以后不需要，仅第一次比较麻烦），点击开启出现悬浮图标。点击悬浮按钮，出现界面点击左上角图标选择进程，若未出现，重复点几次即可，部分手机需要在对AGTools取消省电策略，改为无限制，否则可能会在后台造成假死状态。**

3. #### 功能介绍

- 选择进程

  ![DIK2xe.png](https://s3.ax1x.com/2020/12/02/DIK2xe.png)

- 界面功能说明

![DIlIaR.png](https://s3.ax1x.com/2020/12/02/DIlIaR.png)](https://imgchr.com/i/DIlIaR)

- 扫描inlinehook 地址

![DI8jLF.png](https://s3.ax1x.com/2020/12/02/DI8jLF.png)

- dump so

  ![DIGIOO.png](https://s3.ax1x.com/2020/12/02/DIGIOO.png)

![DIJkhn.png](https://s3.ax1x.com/2020/12/02/DIJkhn.png)

- 修改目标so指令码

  arm、thumb需要自行分析

  ![DIJwAH.png](https://s3.ax1x.com/2020/12/02/DIJwAH.png)

![DIJLbF.png](https://s3.ax1x.com/2020/12/02/DIJLbF.png)

![DIYHJA.png](https://s3.ax1x.com/2020/12/02/DIYHJA.png)

### 声明

本软件仅供交流，切勿非法利用，造成的后果由使用者自行承担。
