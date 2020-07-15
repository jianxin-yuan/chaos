### RocketMq  单机测试

---

[官方文档](https://github.com/apache/rocketmq/tree/master/docs/cn)

#### 安装rocketmq + console

* 修改`docker/broker/broker.conf`文件中的`brokerIP1`字段,改为宿主机的ip

* 进入`docker` 文件夹, 运行`docker-compose up -d`启动即可*
* 访问本地 `localhost:8180`即可访问console 控制台

