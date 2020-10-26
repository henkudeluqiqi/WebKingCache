# WebKingCache
# com.*包已经废弃 请使用org.*
这是一款数据安全的Web缓存框架，拥有自己的缓存回收机制，使用者不需要关心`单机模式`下的数据安全问题，只需要关心自己的业务逻辑
所有其他校验都由`WebKingCache`来帮你解决。
# 使用说明
1、导入WebKingCache请检查项目中是否存在com.*包，如果存在则删除;

2、如有BUG请加群646333504;

3、详细的使用文档请参照`版本更换`;

# API介绍
|API名称|返回值类型|参数列表(参数名:类型:功能:必须:默认)| 功能|  
|------|---|---|------|
|set| Object | key : String : 名称 : 是 <br/> value : Object : 值 : 是 <br /> saveFlag : boolean : 永久保存 : 否 <br/> timeout : int : 过期时间 : 否 : 2(小时) | 往缓存中添加一个key => value  
|cr| void |  无 | 唤醒缓存数据回收器 |
|get| Object | key : String : 名称 : 是 | 通过Key取出对应的value
|remove| Object | key : String : 名称 : 是 | 通过key删除对应的value
|size| int | 无 | 取出当前缓存容器的所有数据量

# 版本更换
V1.0 默认使用ConcurrentWebCache
    
    如果需要使用`默认使用ConcurrentWebCache` 请在`Spring项目中`使用注解`@Autowried` private WebKingCache concurrentCache;
    
V1.1 废除ConcurrentWebCache使用DefaultWebCache
    
    使用方法(spring、spring-boot)
    1、开启WebKingCache对Spring项目的支持@EnableWebKingCache
    2、@Autowried private WebKingCache defaultWebKingCache;
    使用方法(非Spring项目)
    DefaultWebKingCache defaultWebKingCache = new DefaultWebKingCache(可选: timeout);

V1.2 在V1.1的基础上新增了HTTP模块，可以远程调用搭建好的WebKingCache服务器(CacheServer.jar)
    
    更新内容
    1、实现了远程的CacheServer服务器(defaultPort: 7778)。
    2、提供了掉线自动连接。
    3、消息幂等性。
    4、数据百分百发送。
    
    CacheServer的使用方式
    1、首先下载CacheServer.jar http:39
    2、输入命令行 java -Dcache-server=C://xx//...//XXX.properties -jar CacheServer.jar
    3、cache-server为服务器的配置文件，如何使用请参照 WebKingCache项目->resouces->cache.properties
    
    使用方法(spring、spring-boot)
    1、开启HttpWebKingCache对Spring项目的支持@EnableHttpWebKingCache
    2、使用@Autowried private WebKingCache defaultHttpWebKingCache;
    
V1.3 在V1.2的基础上增加了以下功能
    
    更新内容
    1、更新了本地的持久化机制(非HTTP模式)。
    2、项目启动时自动更新数据到本地内容。
    
    使用方法(spring、spring-boot)
    在原有的V1.1基础上。
    配置文件中配置
    @Bean
    public ServerProperties serverProperties() {
        // oneValue 是否开启持久化机制
        // twoValue 开启持久化机制后数据存入那个文件夹下(绝对路径)(存入的文件夹路径要自己创建)
        return new ServerProperties(oneValue , twoValue);
    }
