### 自动配置原理
  
#### @import注解作用
> @import注解主要作用是将类的实例加入到IOC容器中。
#### @import的用法
 - @import({类名.class,类名.class})
 - 使用importSelector方式
 > 使用这种方式必须实现importSelector接口，通过selectImports方法返回需要注入的类路径。
 - 实现ImportBeanDefinitionRegistrar接口
----------------------------------------------------
> 继承DeferredImportSelector做一个延迟加载
-----------------------------------------------------------
#### 自动加载配置的全过程
```java

protected AutoConfigurationEntry getAutoConfigurationEntry(AnnotationMetadata annotationMetadata) {
    // 判断是否允许自动装配 若不允许则直接返回空集 默认返回true
	if (!isEnabled(annotationMetadata)) {
		return EMPTY_ENTRY;
	}
    //getAttributes方法获得 @EnableAutoConfiguration注解中的属性exclude、excludeName等。
	AnnotationAttributes attributes = getAttributes(annotationMetadata);
    // ==又一关键方法== 该方法用于得到该项目所有jar包下的spring.factories文件key为EnableAutoConfiguration相关的自动配置类
	List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
    // 移除重复的配置类
	configurations = removeDuplicates(configurations);
    // 得到要排除的自动配置类
	Set<String> exclusions = getExclusions(annotationMetadata, attributes);
    // 检查要被排除的配置类
	checkExcludedClasses(configurations, exclusions);
    // 将要排除的配置类全部移除
	configurations.removeAll(exclusions);
    // 过滤操作，避免内存浪费
	configurations = getConfigurationClassFilter().filter(configurations);
    // 获取了符合条件的自动配置类后通知监听器来记录符合条件的自动配置类
	fireAutoConfigurationImportEvents(configurations, exclusions);
    // 将符合条件和要排除的自动配置类封装进AutoConfigurationEntry对象，并返回
	return new AutoConfigurationEntry(configurations, exclusions);
}

```



### 启动过程
spring-boot-maven-plugin 作用
springboot怎么启动spring容器的
为什么springboot可以直接jar包运行
spi机制



### 参考文献
[超详细的SpringBoot自动装配原理剖析（上）](https://juejin.cn/post/6891996081044291598)
[超详细的SpringBoot自动装配原理剖析（下）](https://juejin.cn/post/6891996081044291598)
