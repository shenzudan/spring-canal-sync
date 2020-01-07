## 写在前面
东西是从一个依赖canal同步mysql到es的服务里拿出来的  
用的时候canal刚推出同步es的方案，但是那个sql写复杂了就出问题(我的问题)  
那就直接监听binlog看表数据变动就好了，如果相关文档依赖的数据变动就更新这个文档为最新数据  

## 使用
#### 启动
```
@EnableSyncListener
public class XXApplication {  }
```

#### Trigger类中方法

- 当book_info发生更新时，触发此方法，默认传入参数为变更的记录中id字段值
```
@Service
public class BookInfoIndexSyncTrigger {
    @SyncListener(table = {"book_info"}, type = EventType.UPDATE)
    public void updateBookInfo(long bookId) {
        logger.info("更新同步book_info..." + bookId);
        syncService.syncBookInfoIndexById(bookId);
    }
}
```

- 当book_isbn_info发生增、删、改时，触发此方法，传入参数为变更表中book_id字段值
```
@Service
public class BookBaseInfoIndexSyncTrigger {
    @SyncListener(table = {"book_isbn_info"}, type = EventType.ALL, key = {"book_id"})
    public void updateBookInfoFromIsbn(long bookId) {
        logger.info("ALL 非主表批量同步book_isbn_info..." + bookId);
        syncService.syncBookInfoIndexById(bookId);
    }
}
```

## 配置
  - 是否连接canal，开启数据同步，默认true(因为canal客户端指定了clientId，所以连到同一集群的服务只有一个能fetch消息，但是有些是非主要的或者不用来做更新的机器，特别像扩容时候，可以关掉canal客户端)
  ```
  canal:
    sync: false
  ```

  - canal集群配置
  ```
  canal:
    # 是否使用dubbo的zookeeper集群配置，如果使用则下面的host,port节点配置将失效，会读取dubbo.registry.address信息,默认fasle
    enable: true
    host: 127.0.0.1
    port: 11111
    destination: example
    username:
    password:
  ```


## 相关
- 开启数据库binlog
```properties
[mysqld]
log-bin=mysql-bin # 开启 binlog
binlog-format=ROW # 选择 ROW 模式
server_id=1 # 配置 MySQL replaction 需要定义，不要和 canal 的 slaveId 重复
```

- canal 1.1.3

  download: https://github.com/alibaba/canal/releases
  - 自定义数据库配置 https://github.com/alibaba/canal/wiki/QuickStart
  - 阿里云配置 https://github.com/alibaba/canal/wiki/aliyun-RDS-QuickStart
  - 实例监控binlog过滤表

```
    canal.instance.filter.regex=test_lib\\..*
```
