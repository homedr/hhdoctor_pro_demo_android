## DoctorPro SDK接入文档 2.4.4.02111033

<p align="right">
北京和缓医疗科技有限公司<br/>
网址：https://www.hh-medic.com <br/>
地址：北京市东城区东直门来福士7层
</p>

### 一、SDK接入引用说明

#### 1. 建议接入环境

##### 1.1 建议接入使用IDE版本

Android Studio 3.x.x版本以上版本

##### 1.2 建议接入SDK版本以及最低支持设备系统版本

|配置项|版本|
|---|---|
|compileSdkVersion| 28及以上|
|minSdkVersion| 17及以上|
|targetSdkVersion| 28及以上|
|最低支持设备系统| >= 4.2 |


#### 2. 和缓视频医生Android SDK通过maven仓库引用来导入工程，如下

##### 2.1 在build.gradle文件中配置远程库地址，在respositories中添加相应配置

```
repositories {
    
    maven {
        credentials {
            username 'hh-public'
            password 'OFGB5wX0'
        }
        url 'http://develop.hh-medic.com/repository/maven-public'
    }
}
```

##### 2.2 在build.gradle文件中dependencies中配置库的引用

```
implementation 'com.hhmedic.android.sdk:pro:2.4.4.02111033'
```

<span style="color:red;">注：添加以上配置后需要进行gradle sync才能同步生效，配置maven库地址的时候不能省略用户名和密码，否则同步不下来。</span>

##### 2.3 配置NDK架构选择，必须进行对应配置

```
ndk {
    //设置支持的SO库架构
    abiFilters "armeabi-v7a"
}
```

##### 2.4 java8支持的配置，必须配置

```
compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
}
```

##### 2.5 packageingOptions配置，必须配置

```
packagingOptions {
   pickFirst 'lib/arm64-v8a/libsecsdk.so'
   pickFirst 'lib/armeabi-v7a/libsecsdk.so'
   pickFirst 'lib/armeabi/libsecsdk.so'
}
```


#### 3. 我们用到的常用第三方库以及库的版本
```
implementation 'com.google.code.gson:gson:2.8.6'
implementation 'com.orhanobut:logger:2.2.0'
implementation 'com.github.bumptech.glide:glide:4.9.0'
implementation 'com.zhihu.android:matisse:0.5.1'
implementation 'com.squareup.okhttp3:okhttp:3.x.x' //这个版本号只是一个代写
```

> 如果由于这些包引用出现冲突例如是duplicate某个jar包或文件有可能是某些库引用的版本和我们不一致，直接force一个合适的版本就行。具体写法可以参考[这里](#5-如果遇到库冲突也就是duplicate某个包这说明库冲突了这种问题可以用如下方法解决)。


### 二、SDK接入引用说明

#### 1. SDK初始化

##### 1.1 SDK配置选项 HHSDKOptions

```java
HHSDKOptions options = new HHSDKOptions("productId");
```

参数说明：

| 参数定义 | 说明 |
| --- | --- |
| productId | 和缓分配的产品ID |
| sDebug    |是否开启调试（开启会打印log）|
|mDeviceType| NORMAL表示手机|
|dev|是否开始测试服模式，开启后连接测试服|
|isOpenCamera|视频过程中是否开启拍照|
|mOrientation|屏幕方向 ActivityInfo.SCREEN_ORIENTATION_PORTRAIT 或 ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE|

##### 1.2 SDK初始化

>SDK初始化最好是放到自定义的Application中去初始化。

```java
HHSDKOptions options = ...;//这里可以自行初始化，可以是音箱默认配置获取也可以直接初始化
options.isDebug = true;
options.mDeviceType = DeviceType.NORMAL;
options.dev = true;
options.isOpenCamera = false;
options.mOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
HHProDoctor.init(getApplicationContext(), "这里传递需要在界面显示模块的名字，比如叫视频医生就传递视频医生", options);
```

##### 1.3 SDK使用到的主要权限说明

| 权限 | 说明 |
| --- | --- |
| android.permission.CALL_PHONE | 在视频通话过程中如果有电话进来我们会做挂断视频保证正常电话通话 |
|android.permission.CAMERA|保证正常使用设备的摄像设备|
|android.permission.RECORD_AUDIO|保证正常使用设备的音频设备|
|android.permission.WRITE_EXTERNAL_STORAGE android.permission.READ_EXTERNAL_STORAGE | 保证正常读取存储设备上的媒体文件 |

#### 2. SDK功能介绍

##### 2.1 进入视频医生界面

```java
public static void callDoctor(Context context, String userToken, HHLoginListener loginListener) 
```

参数说明：

| 参数定义 | 说明 |
| --- | --- |
|Context context|上下文，当前操作Activity|
|String userToken|与和缓对接得到的userToken|
|HHLoginListener loginListener|进入SDK登录回调一般不用处理|

##### 2.2 登出

```java
public static void loginOut(Context context)
```

参数说明：

| 参数定义 | 说明 |
| --- | --- |
|Context context|上下文，当前操作Activity|

### 三、常见问题

#### 1. AndroidManifest合并冲突问题

```
 <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="${applicationId}.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/provider_paths" />
</provider>
```

可以更替乘如下写法

```
<manifest package="cn.edu.fudan.rndrobot"
    xmlns:tools="http://schemas.android.com/tools"
          xmlns:android="http://schemas.android.com/apk/res/android">
    <provider
        tools:replace="android:authorities"
        android:name="android.support.v4.content.FileProvider"
        android:authorities="${applicationId}.provider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
            tools:replace="android:resource"
            android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_paths"/>
    </provider>
</manifest>
```

#### 2. error:style attribute '@android:attr/windowEnterAnimation' not found

在Project/gradle.properties中添加 android.enableAapt2=false


#### 3. SDK UTDID冲突解决方案

Android UTDID包命名形式为：utdid4all-x.x.x.jar，UTDID作为阿里集团移动端SDK通用组件，包括阿里云在内的许多平台产品移动端SDK对其有依赖，若同时集成多平台移动端SDK，可能发生UTDID冲突。解决重复的方案是手动删除重复的UTDID SDK，仅保留一个UTDID SDK，建议保留阿里云平台下载的UTDID SDK。如果是通过gradle引用通过关闭其他SDK包的utdid的引用即可如下：
```
compile ('com.xxx:xxx.xxx:1.0.1') {
  exclude (module: 'alicloud-android-utdid')
}
```

如果这种方式解决不了可以去阿里官网下载一个不带utdid的一个支付宝的包用就行，具体说明地址如下
https://help.aliyun.com/knowledge_detail/59152.html?spm=a2c4g.11186623.2.20.26d216ee1AMm0k

#### 4. 使用阿里云Utils SDK造成的冲突即这个moudlealicloud-android-utils的冲突可以以如下方式解决

造成冲突的原因有很多种，例如如果同时使用了阿里的 Utils库和友盟的库就会造成冲突，最好使用Utils库不要使用本地引用最好使用远程gradle引用。如果遇到了冲突可以先查看本地是否引用了阿里云的Utils SDK的包如果有可以删除即可或者使用gralde引用然后利用exclude排除Utils的module，即排除alicloud-android-utils。具体问题可以参照阿里的说明，地址如下
https://helpcdn.aliyun.com/knowledge_detail/66886.html?spm=a2c4g.11186631.2.1.8c0fb068qquUGZ

### 四、版本更新说明

|版本号|说明|
|---|---|
|2.4.4.01191014|首发版本|
|2.4.4.01191812|fix 未处理会员未开通的情况|
|2.4.4.02111033|fix file provider配置引起的视频中拍照崩溃的问题|
