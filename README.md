# CacheKing
# com.*包已经废弃 请使用org.*
这是一个Web缓存Jar  以后会升级为Web缓存服务器，是一款线程安全的缓存框架，会默认清空不需要的垃圾数据，可插拔式的一款弹性服务器端缓存框架。
已经了Spring进行了整合
使用 @EnableWebKingCache注解说明已经开启了WebKingCache对你项目的支持，@EnableWebKingCache注解中可配置您的缓存数据默认消失时间
该Bean存在Spring容器中的Key为webKingCache
