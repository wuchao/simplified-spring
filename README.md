
Forked from Spring Framework On 2018-12-23.


编译：

切换工作目录到 spring 源码工程的根目录下，分别执行 `gradle objenesisRepackJar` 和 `gradle cglibRepackJar` 命令。

如果报 kotlin.reflect 相关类找不到，则进入：

```aidl
settings -> Languages & Frameworks -> kotlin
```

升级 kotlin。



