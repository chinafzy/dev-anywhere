# dev anywhere
开发者可以在任何地方去连接线上环境。

**直接看samples更简单**

## 解决的问题
开发/测试环节分离，是大家都熟悉的基本规范。但是有时候，有些功能必须要去线上联调。
+ 连接线上的数据库、Redis、……；
+ 把`ide`上开发调试的服务接入到线上；

针对第一个需求，可能要把数据库和Redis的公网打开，这个原本就是很不安全的行为。
更好的做法是用`ssh -L`的方式把服务器上的服务转接到本地。

第二个需求稍微麻烦一点，`ssh -R`的方式把本地的服务器挂接到服务器上，
然后再去`nginx`里面做转发的工作。

现在有个更简单的办法：
+ 引入`dev-anywhere`的`spring-boot`依赖；
+ 增加一个`yaml`配置；
+ 在开发联调时候，启用这个配置文件；

其余的工作，`dev-anywhere`自己会来完成，自动查找连接配置、建立跳转连接并且替换，
网络中断后的自动续联。

`application-anywhere.yaml`
```yaml
tools:
  anywhere:
    session:
      session-model:
        host: 43.143.168.105
        user: anywhere
      #  pass: anywhere19781978
      l2r-binds:
        - ori-port: 8080
          bind-port: 18080
    engine-model:
      identify-file: ~/.ssh/id_rsa
```
比如上面这个配置文件，表示：
+ 远程连接到`43.143.168.105`这个机器，作为跳板，账号`anywhere`，
可以使用密码登录，也可以使用全局的`identify-file`免密登录。
+ 将本地的`8080`端口映射到跳板机的`18080`端口。

`spring-boot`启动后，`anywhere`如果能成功连接远程服务器，则自动扫描`redis``database`连接配置信息，
将他们映射到本地，并且修改连接到本地的隐射端口。

启动后，大概会出现这样的日志：
```text
Initializing session SessionModel(user=anywhere, pass=null, host=43.143.168.105, port=22, strictHost=false)
Exporting to remote 43.143.168.105, null:8080 => null:18080
Enhance datasource spring.datasource-org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
Make proxy localhost:3306 => 127.0.0.1:49355
Change jdbc url: jdbc:mysql://localhost:3306/whmc-80?useUnicode=true&characterEncoding=utf8&autoReconnect=true&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&serverTimezone=Asia/Shanghai => jdbc:mysql://127.0.0.1:49355/whmc-80?useUnicode=true&characterEncoding=utf8&autoReconnect=true&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&serverTimezone=Asia/Shanghai
```
## 具体做法
具体做法参考`anywhere-samples`

## 参数配置
参考 `conf.MD`

## 其它


