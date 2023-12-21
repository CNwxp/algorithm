## eureka源码解析

#### client端源码解析

去拉注册表到本地

将自己注册到服务端

启动定时任务

- 客户端定时读取服务端注册表信息

- 客户端定时发送续约心跳,表示当前客户端还在存活

- 客户端定时上报更新信息给服务端





这是一个标准的eureka客户端的程序



```java
@SpringBootApplication
@EnableDiscoveryClient
public class EurekaConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaConsumerApplication.class, args);
    }

}

```

- [x]  如何加载spring.factories的文件

- [x]  @EnableDiscoveryClient注解的使用

由于不加注解就能进行注册所以着重理一下spring.factories类的内容

```properties
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.cloud.netflix.eureka.config.EurekaClientConfigServerAutoConfiguration,\
org.springframework.cloud.netflix.eureka.config.DiscoveryClientOptionalArgsConfiguration,\
org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration,\
org.springframework.cloud.netflix.ribbon.eureka.RibbonEurekaAutoConfiguration,\
org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration,\
org.springframework.cloud.netflix.eureka.reactive.EurekaReactiveDiscoveryClientConfiguration,\
org.springframework.cloud.netflix.eureka.loadbalancer.LoadBalancerEurekaAutoConfiguration

org.springframework.cloud.bootstrap.BootstrapConfiguration=\
org.springframework.cloud.netflix.eureka.config.EurekaConfigServerBootstrapConfiguration

```

比较主要的逻辑就在下面的这个类里EurekaClientAutoConfiguration

- EurekaInstanceConfig为空的时候创建eurekaInstanceConfigBean

- eurekaInstanceConfigBean是EurekaInstanceConfig的一个实现类，所以后面方法注入的其实是eurekaInstanceConfigBean

- 主要是获取配置文件的参数

```java
    @Bean
	@ConditionalOnMissingBean(value = EurekaInstanceConfig.class,
			search = SearchStrategy.CURRENT)
	public EurekaInstanceConfigBean eurekaInstanceConfigBean(InetUtils inetUtils,
			ManagementMetadataProvider managementMetadataProvider) {
		String hostname = getProperty("eureka.instance.hostname");
		boolean preferIpAddress = Boolean
				.parseBoolean(getProperty("eureka.instance.prefer-ip-address"));
		String ipAddress = getProperty("eureka.instance.ip-address");
		boolean isSecurePortEnabled = Boolean
				.parseBoolean(getProperty("eureka.instance.secure-port-enabled"));

		String serverContextPath = env.getProperty("server.servlet.context-path", "/");
		int serverPort = Integer.parseInt(
				env.getProperty("server.port", env.getProperty("port", "8080")));

		Integer managementPort = env.getProperty("management.server.port", Integer.class);

		return instance;
	}
```

初始化一个beanApplicationInfoManager

- 主要存放根据配置文件生成的实例信息和配置文件的内容

```java
        @Bean
		@ConditionalOnMissingBean(value = ApplicationInfoManager.class,
				search = SearchStrategy.CURRENT)
		public ApplicationInfoManager eurekaApplicationInfoManager(
				EurekaInstanceConfig config) {
			InstanceInfo instanceInfo = new InstanceInfoFactory().create(config);
			return new ApplicationInfoManager(config, instanceInfo
```



客户端最为核心的一个类

```java
 public EurekaClient eurekaClient(ApplicationInfoManager manager, EurekaClientConfig config, EurekaInstanceConfig instance, @Autowired(required = false) HealthCheckHandler healthCheckHandler) {
            ApplicationInfoManager appManager;
            if (AopUtils.isAopProxy(manager)) {
                appManager = (ApplicationInfoManager)ProxyUtils.getTargetObject(manager);
            } else {
                appManager = manager;
            }

            CloudEurekaClient cloudEurekaClient = new CloudEurekaClient(appManager, config, this.optionalArgs, this.context);
            cloudEurekaClient.registerHealthCheck(healthCheckHandler);
            return cloudEurekaClient;
        }
```

创建CloudEurekaClient的时候调用super也就是DiscoveryClient的构造方法，主要做了一下内容

1. 获取注册表
2. 客户端提交注册请求到Eureka (通常情况下启动项目时不推荐注册,因为如果注册失败,会抛出异常,后续的定时任务也无法创建)
3. 初始化定时任务

```java
    public CloudEurekaClient(ApplicationInfoManager applicationInfoManager, EurekaClientConfig config, AbstractDiscoveryClientOptionalArgs<?> args, ApplicationEventPublisher publisher) {
        super(applicationInfoManager, config, args);
        this.cacheRefreshedCount = new AtomicLong(0L);
        this.eurekaHttpClient = new AtomicReference();
        this.applicationInfoManager = applicationInfoManager;
        this.publisher = publisher;
        this.eurekaTransportField = ReflectionUtils.findField(DiscoveryClient.class, "eurekaTransport");
        ReflectionUtils.makeAccessible(this.eurekaTransportField);
    }
```

**获取注册表**

获取配置文件的参数是否需要去取注册表，默认为true

1. 在初始化EurekaClient时,会获取注册到EurekaServer上的注册表信息,如果获取不到,则通过本地获取,如果本地也没有则判断配置中是否配置必须获取,如果必须获取则抛出异常。

```java
 if (clientConfig.shouldFetchRegistry()) {
            try {
                boolean primaryFetchRegistryResult = fetchRegistry(false);
                if (!primaryFetchRegistryResult) {
                    logger.info("Initial registry fetch from primary servers failed");
                }
                boolean backupFetchRegistryResult = true;
                if (!primaryFetchRegistryResult && !fetchRegistryFromBackup()) {
                    backupFetchRegistryResult = false;
                    logger.info("Initial registry fetch from backup servers failed");
                }
                if (!primaryFetchRegistryResult && !backupFetchRegistryResult && clientConfig.shouldEnforceFetchRegistryAtInit()) {
                    throw new IllegalStateException("Fetch registry error at startup. Initial fetch failed.");
                }
            } catch (Throwable th) {
                logger.error("Fetch registry error at startup: {}", th.getMessage());
                throw new IllegalStateException(th);
            }
        }
```

查看"this.fetchRegistry(false)"获取注册表方法

```java
  private boolean fetchRegistry(boolean forceFullRegistryFetch) {
        Stopwatch tracer = FETCH_REGISTRY_TIMER.start();

        try {
            // If the delta is disabled or if it is the first time, get all
            // 获取本地的注册表
            Applications applications = getApplications();
            //  判断是否增量拉取
            if (clientConfig.shouldDisableDelta()
                    || (!Strings.isNullOrEmpty(clientConfig.getRegistryRefreshSingleVipAddress()))
                    || forceFullRegistryFetch
                    || (applications == null)
                    || (applications.getRegisteredApplications().size() == 0)
                    || (applications.getVersion() == -1)) //Client application does not have latest library supporting delta
            {
                logger.info("Disable delta property : {}", clientConfig.shouldDisableDelta());
                logger.info("Single vip registry refresh property : {}", clientConfig.getRegistryRefreshSingleVipAddress());
                logger.info("Force full registry fetch : {}", forceFullRegistryFetch);
                logger.info("Application is null : {}", (applications == null));
                logger.info("Registered Applications size is zero : {}",
                        (applications.getRegisteredApplications().size() == 0));
                logger.info("Application version is -1: {}", (applications.getVersion() == -1));
                getAndStoreFullRegistry();
            } else {
                getAndUpdateDelta(applications);
            }
            applications.setAppsHashCode(applications.getReconcileHashCode());
            logTotalInstances();
        } catch (Throwable e) {
            logger.info(PREFIX + "{} - was unable to refresh its cache! This periodic background refresh will be retried in {} seconds. status = {} stacktrace = {}",
                    appPathIdentifier, clientConfig.getRegistryFetchIntervalSeconds(), e.getMessage(), ExceptionUtils.getStackTrace(e));
            return false;
        } finally {
            if (tracer != null) {
                tracer.stop();
            }
        }

        // Notify about cache refresh before updating the instance remote status
        onCacheRefreshed();

        // Update remote status based on refreshed data held in the cache
        updateInstanceRemoteStatus();

        // registry was fetched successfully, so return true
        return true;
    }
```

发送http请求到服务端

查看 AbstractJerseyEurekaHttpClient 下的 getApplications()方法

```java
	public EurekaHttpResponse<Applications> getApplications(String... regions) {
		//传递"apps/" 请求地址与region,执行 getApplicationsInternal()方法
        return this.getApplicationsInternal("apps/", regions);
    }

	
	private EurekaHttpResponse<Applications> getApplicationsInternal(String urlPath, String[] regions) {
        ClientResponse response = null;
        String regionsParamValue = null;

        EurekaHttpResponse var8;
        try {
        	//1.根据传递进来的url也就是"apps/"获取到处理器WebResouce
            WebResource webResource = this.jerseyClient.resource(this.serviceUrl).path(urlPath);
            if (regions != null && regions.length > 0) {
                regionsParamValue = StringUtil.join(regions);
                webResource = webResource.queryParam("regions", regionsParamValue);
            }
			//2.封装请求参数
            Builder requestBuilder = webResource.getRequestBuilder();
            this.addExtraHeaders(requestBuilder);
            //3.提交get请求,拿到response请求结果
            response = (ClientResponse)((Builder)requestBuilder.accept(new MediaType[]{MediaType.APPLICATION_JSON_TYPE})).get(ClientResponse.class);
            Applications applications = null;
            //4.判断响应是否成功,成功将拿到注册表封装为Applications
            if (response.getStatus() == Status.OK.getStatusCode() && response.hasEntity()) {
                applications = (Applications)response.getEntity(Applications.class);
            }
			//5.将拿到封装好的注册表信息封装到EurekaHttpResponse中
            var8 = EurekaHttpResponse.anEurekaHttpResponse(response.getStatus(), Applications.class).headers(headersOf(response)).entity(applications).build();
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("Jersey HTTP GET {}/{}?{}; statusCode={}", new Object[]{this.serviceUrl, urlPath, regionsParamValue == null ? "" : "regions=" + regionsParamValue, response == null ? "N/A" : response.getStatus()});
            }
			//6.关闭
            if (response != null) {
                response.close();
            }

        }
		//7.响应结果
        return var8;
    }

```

获取注册表总结

- 在DiscoveryClient构造器中,会判断是否配置了获取注册表(通常情况下EurekaServer是一个注册中心配置为false),需要获取,则调用 " this.getAndStoreFullRegistry()" 执行全量获取,或者执行"this.getAndUpdateDelta(applications)"增量获取,(通过该步骤可以了解到在一个正常项目中假设所有EurekaServer宕机,是不影响使用的,服务消费方可以通过本地保存的Applications注册表信息进行消费调用调用)

- 在Eureka中内部通过Jersey 框架进行通信,以全量获取注册表信息方法"void getAndStoreFullRegistry()"为例,该方法内部会执行 AbstractJerseyEurekaHttpClient 下的 getApplications()方法,提交一个请求路径为"apps/"的get请求,拿到注册表信息,将注册表信息封装为Applications
  

**客户端提交注册请求到Eureka**

```java
 if (clientConfig.shouldRegisterWithEureka() && clientConfig.shouldEnforceRegistrationAtInit()) {
            try {
                if (!register() ) {
                    throw new IllegalStateException("Registration error at startup. Invalid server response.");
                }
            } catch (Throwable th) {
                logger.error("Registration error at startup: {}", th.getMessage());
                throw new IllegalStateException(th);
            }
        }
```

执行DiscoveryClient构造器创建EurekaClient时,会判断是否提交注册请求(通常情况下启动项目不会配置注册),查看DiscoveryClient中的注册方法 register(),最终会执行AbstractJerseyEurekaHttpClient的register(), 提交时拼接请求路径"apps/+当前服务名称",并携带当前服务封装的InstanceInfo。

```java
boolean register() throws Throwable {
        logger.info("DiscoveryClient_{}: registering service...", this.appPathIdentifier);

        EurekaHttpResponse httpResponse;
        try {
        	//1.拿到当前主机的instanceInfo,执行AbstractJerseyEurekaHttpClient的register()
            httpResponse = this.eurekaTransport.registrationClient.register(this.instanceInfo);
        } catch (Exception var3) {
            logger.warn("DiscoveryClient_{} - registration failed {}", new Object[]{this.appPathIdentifier, var3.getMessage(), var3});
            throw var3;
        }

        if (logger.isInfoEnabled()) {
            logger.info("DiscoveryClient_{} - registration status: {}", this.appPathIdentifier, httpResponse.getStatusCode());
        }

        return httpResponse.getStatusCode() == Status.NO_CONTENT.getStatusCode();
    }
```




AbstractJerseyEurekaHttpClient下的register()

```java
public EurekaHttpResponse<Void> register(InstanceInfo info) {
		//1.拼接请求地址"apps/+getAppName"
        String urlPath = "apps/" + info.getAppName();
        ClientResponse response = null;

        EurekaHttpResponse var5;
        try {
        	//2.封装请求参数
            Builder resourceBuilder = this.jerseyClient.resource(this.serviceUrl).path(urlPath).getRequestBuilder();
            this.addExtraHeaders(resourceBuilder);
            response = (ClientResponse)((Builder)((Builder)((Builder)resourceBuilder.header("Accept-Encoding", "gzip")).type(MediaType.APPLICATION_JSON_TYPE)).accept(new String[]{"application/json"})).post(ClientResponse.class, info);
            //3.提交post请求
            var5 = EurekaHttpResponse.anEurekaHttpResponse(response.getStatus()).headers(headersOf(response)).build();
        } finally {
            if (logger.isDebugEnabled()) {
                logger.debug("Jersey HTTP POST {}/{} with instance {}; statusCode={}", new Object[]{this.serviceUrl, urlPath, info.getId(), response == null ? "N/A" : response.getStatus()});
            }

            if (response != null) {
                response.close();
            }

        }

        return var5;
    }

```

#### 初始化定时任务 initScheduledTasks()

会执行三个定时任务

- 客户端定时读取服务端注册表信息

- 客户端定时发送续约心跳,表示当前客户端还在存活

- 客户端定时上报更新信息给服务端



#### server源码解析

会有定时器去清理过期的服务，如果没有触发自我保护机制，就及时的进行清理



某个服务注册上去会发请求到其它的注册中心。

[SpringCloud之Eureka服务端源码解析_eureka服务注册源码-CSDN博客](https://blog.csdn.net/majinan3456/article/details/129991439)


#### ribbon的原理
#### nacos流程图

![nacos Ap架构原型图](https://github.com/CNwxp/algorithm/blob/master/interview/Nacos%E6%BA%90%E7%A0%81%E5%89%96%E6%9E%90-%E6%9C%8D%E5%8A%A1%E6%B3%A8%E5%86%8C%E4%B8%8E%E5%8F%91%E7%8E%B0(%E4%B8%B4%E6%97%B6%E5%AE%9E%E4%BE%8BAP%E6%A8%A1%E5%BC%8F).png)
#### nacos原理
- Nacos服务注册与发现源码剖析
- Nacos注册表如何防止多节点读写并发冲突
- Nacos高并发支撑异步任务与内存队列剖析
- Nacos心跳机制与服务健康检查源码
- Nacos服务变动事件发布源码
- Nacos服务下线源码
- Nacos心跳在集群架构下的设计原理
- Nacos集群节点状态同步源码
- Nacos集群服务新增数据同步源码
- Nacos集群服务状态变动同步源码
- ----------------------------------------
- Nacos配置中心架构
- Client端是怎么从配置中心获取配置的
- 配置中心配置发生改变Client是如何感知的
- 集群架构下其他节点如何做数据同步的
------------------------------------------
- CAP与BASE原则
>>>  C:一致性  A：可用性  p:分区容错性:不要因为分布式系统多个节点之间的网络不通而导致整个系统不能用
- Nacos&Zookeeper&Eureka的CAP架构的横向对比
- Raft协议动态图解
 >>> 演示地址 http://thesecretlivesofdata.com/raft/
- Nacos集群CP机构基于Raft协议源码剖析
- Nacos集群CP架构的脑裂问题
- 基于云SaaS的超大规模注册中心架构设计

#### 限流容错降级Sentinel原理
