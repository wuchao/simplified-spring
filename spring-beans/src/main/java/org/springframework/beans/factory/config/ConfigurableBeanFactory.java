package org.springframework.beans.factory.config;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

import java.beans.PropertyEditor;
import java.security.AccessControlContext;

/**
 * 提供多个配置 bean factory 的特性
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {


    String SCOPE_SINGLETON = "singleton";

    String SCOPE_PROTOTYPE = "prototype";


    void setParentBeanFactory(BeanFactory parentBeanFactory);

    void setBeanClassLoader(@Nullable ClassLoader beanClassLoader);


    @Nullable
    ClassLoader getBeanClassLoader();


    void setTempClassLoader(@Nullable ClassLoader tempClassLoader);


    @Nullable
    ClassLoader getTempClassLoader();


    void setCacheBeanMetadata(boolean cacheBeanMetadata);


    boolean isCacheBeanMetadata();


    /**
     * SpEL 表达式解析器
     *
     * @param resolver
     */
    void setBeanExpressionResolver(@Nullable BeanExpressionResolver resolver);


    @Nullable
    BeanExpressionResolver getBeanExpressionResolver();


    /**
     * 类型转换
     *
     * @param conversionService
     */
    void setConversionService(@Nullable ConversionService conversionService);


    @Nullable
    ConversionService getConversionService();


    void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar);


    /**
     * 属性编辑器可以将String对象转换成java对象
     *
     * [SpringMVC类型转换、数据绑定](https://www.cnblogs.com/Leo_wl/p/3764937.html)
     *
     * @param requiredType
     * @param propertyEditorClass
     */
    void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass);


    void copyRegisteredEditorsTo(PropertyEditorRegistry registry);


    void setTypeConverter(TypeConverter typeConverter);


    /**
     * 可以将一个类型转换成另一个类型
     * @return
     */
    TypeConverter getTypeConverter();


    void addEmbeddedValueResolver(StringValueResolver valueResolver);


    boolean hasEmbeddedValueResolver();


    @Nullable
    String resolveEmbeddedValue(String value);


    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);


    int getBeanPostProcessorCount();


    void registerScope(String scopeName, Scope scope);


    String[] getRegisteredScopeNames();


    @Nullable
    Scope getRegisteredScope(String scopeName);


    AccessControlContext getAccessControlContext();


    void copyConfigurationFrom(ConfigurableBeanFactory otherFactory);


    void registerAlias(String beanName, String alias) throws BeanDefinitionStoreException;


    void resolveAliases(StringValueResolver valueResolver);


    BeanDefinition getMergedBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;


    boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException;


    void setCurrentlyInCreation(String beanName, boolean inCreation);


    boolean isCurrentlyInCreation(String beanName);


    void registerDependentBean(String beanName, String dependentBeanName);


    String[] getDependentBeans(String beanName);


    String[] getDependenciesForBean(String beanName);


    void destroyBean(String beanName, Object beanInstance);


    void destroyScopedBean(String beanName);


    void destroySingletons();

}
