/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.annotation;

import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

/**
 * Convenient adapter for programmatic registration of annotated bean classes.
 * This is an alternative to {@link ClassPathBeanDefinitionScanner}, applying
 * the same resolution of annotations but for explicitly registered classes only.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Sam Brannen
 * @author Phillip Webb
 * @see AnnotationConfigApplicationContext#register
 * @since 3.0
 */
public class AnnotatedBeanDefinitionReader {

    private final BeanDefinitionRegistry registry;
    /**
     * bean 名称的生成策略
     */
    private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

    /**
     * 决定 bean 实例的范围（即生命周期）的。
     * 常见的生命周期有四种，PROTOTYPE（原型）、SINGLETON（单例）、REQUEST（请求）、SESSION（会话）。
     * 就是通过检查类上有没有 @Scope 这个注解。如果有的话，就按指定的走，没有的话，就按单例（默认）走。
     */
    private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

    /**
     * 根据“条件”判断一个 bean 定义该不该被注册。
     * 这可是 SpringBoot 自动配置（AutoConfiguration）的基石啊。
     * 就是去检测类上有没有标 @Conditional 这个注解。如果没有的话，bean 定义会被注册。
     * 如果有的话，需要再去计算具体的“条件”，然后才能确定 bean 定义到底要不要注册。
     */
    private ConditionEvaluator conditionEvaluator;


    /**
     * Create a new {@code AnnotatedBeanDefinitionReader} for the given registry.
     * If the registry is {@link EnvironmentCapable}, e.g. is an {@code ApplicationContext},
     * the {@link Environment} will be inherited, otherwise a new
     * {@link StandardEnvironment} will be created and used.
     *
     * @param registry the {@code BeanFactory} to load bean definitions into,
     *                 in the form of a {@code BeanDefinitionRegistry}
     * @see #AnnotatedBeanDefinitionReader(BeanDefinitionRegistry, Environment)
     * @see #setEnvironment(Environment)
     */
    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this(registry, getOrCreateEnvironment(registry));
    }

    /**
     * Create a new {@code AnnotatedBeanDefinitionReader} for the given registry and using
     * the given {@link Environment}.
     *
     * @param registry    the {@code BeanFactory} to load bean definitions into,
     *                    in the form of a {@code BeanDefinitionRegistry}
     * @param environment the {@code Environment} to use when evaluating bean definition
     *                    profiles.
     * @since 3.1
     */
    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry, Environment environment) {
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        Assert.notNull(environment, "Environment must not be null");
        this.registry = registry;
        this.conditionEvaluator = new ConditionEvaluator(registry, environment, null);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(this.registry);
    }


    /**
     * Return the BeanDefinitionRegistry that this scanner operates on.
     */
    public final BeanDefinitionRegistry getRegistry() {
        return this.registry;
    }

    /**
     * Set the Environment to use when evaluating whether
     * {@link Conditional @Conditional}-annotated component classes should be registered.
     * <p>The default is a {@link StandardEnvironment}.
     *
     * @see #registerBean(Class, String, Class...)
     */
    public void setEnvironment(Environment environment) {
        this.conditionEvaluator = new ConditionEvaluator(this.registry, environment, null);
    }

    /**
     * Set the BeanNameGenerator to use for detected bean classes.
     * <p>The default is a {@link AnnotationBeanNameGenerator}.
     */
    public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
        this.beanNameGenerator = (beanNameGenerator != null ? beanNameGenerator : new AnnotationBeanNameGenerator());
    }

    /**
     * Set the ScopeMetadataResolver to use for detected bean classes.
     * <p>The default is an {@link AnnotationScopeMetadataResolver}.
     */
    public void setScopeMetadataResolver(@Nullable ScopeMetadataResolver scopeMetadataResolver) {
        this.scopeMetadataResolver =
                (scopeMetadataResolver != null ? scopeMetadataResolver : new AnnotationScopeMetadataResolver());
    }


    /**
     * Register one or more annotated classes to be processed.
     * <p>Calls to {@code register} are idempotent; adding the same
     * annotated class more than once has no additional effect.
     *
     * @param annotatedClasses one or more annotated classes,
     *                         e.g. {@link Configuration @Configuration} classes
     */
    public void register(Class<?>... annotatedClasses) {
        for (Class<?> annotatedClass : annotatedClasses) {
            registerBean(annotatedClass);
        }
    }

    /**
     * Register a bean from the given bean class, deriving its metadata from
     * class-declared annotations.
     *
     * @param annotatedClass the class of the bean
     */
    public void registerBean(Class<?> annotatedClass) {
        doRegisterBean(annotatedClass, null, null, null);
    }

    /**
     * Register a bean from the given bean class, deriving its metadata from
     * class-declared annotations, using the given supplier for obtaining a new
     * instance (possibly declared as a lambda expression or method reference).
     *
     * @param annotatedClass   the class of the bean
     * @param instanceSupplier a callback for creating an instance of the bean
     *                         (may be {@code null})
     * @since 5.0
     */
    public <T> void registerBean(Class<T> annotatedClass, @Nullable Supplier<T> instanceSupplier) {
        doRegisterBean(annotatedClass, instanceSupplier, null, null);
    }

    /**
     * Register a bean from the given bean class, deriving its metadata from
     * class-declared annotations, using the given supplier for obtaining a new
     * instance (possibly declared as a lambda expression or method reference).
     *
     * @param annotatedClass   the class of the bean
     * @param name             an explicit name for the bean
     * @param instanceSupplier a callback for creating an instance of the bean
     *                         (may be {@code null})
     * @since 5.0
     */
    public <T> void registerBean(Class<T> annotatedClass, String name, @Nullable Supplier<T> instanceSupplier) {
        doRegisterBean(annotatedClass, instanceSupplier, name, null);
    }

    /**
     * Register a bean from the given bean class, deriving its metadata from
     * class-declared annotations.
     *
     * @param annotatedClass the class of the bean
     * @param qualifiers     specific qualifier annotations to consider,
     *                       in addition to qualifiers at the bean class level
     */
    @SuppressWarnings("unchecked")
    public void registerBean(Class<?> annotatedClass, Class<? extends Annotation>... qualifiers) {
        doRegisterBean(annotatedClass, null, null, qualifiers);
    }

    /**
     * Register a bean from the given bean class, deriving its metadata from
     * class-declared annotations.
     *
     * @param annotatedClass the class of the bean
     * @param name           an explicit name for the bean
     * @param qualifiers     specific qualifier annotations to consider,
     *                       in addition to qualifiers at the bean class level
     */
    @SuppressWarnings("unchecked")
    public void registerBean(Class<?> annotatedClass, String name, Class<? extends Annotation>... qualifiers) {
        doRegisterBean(annotatedClass, null, name, qualifiers);
    }

    /**
     * Register a bean from the given bean class, deriving its metadata from
     * class-declared annotations.
     *
     * @param annotatedClass        the class of the bean
     * @param instanceSupplier      a callback for creating an instance of the bean
     *                              (may be {@code null})
     * @param name                  an explicit name for the bean
     * @param qualifiers            specific qualifier annotations to consider, if any,
     *                              in addition to qualifiers at the bean class level
     * @param definitionCustomizers one or more callbacks for customizing the
     *                              factory's {@link BeanDefinition}, e.g. setting a lazy-init or primary flag
     * @since 5.0
     */
    <T> void doRegisterBean(Class<T> annotatedClass, @Nullable Supplier<T> instanceSupplier, @Nullable String name,
                            @Nullable Class<? extends Annotation>[] qualifiers, BeanDefinitionCustomizer... definitionCustomizers) {

        AnnotatedGenericBeanDefinition abd = new AnnotatedGenericBeanDefinition(annotatedClass);

        // @Conditional 装配条件判断是否需要跳过注册
        if (this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
            return;
        }

        abd.setInstanceSupplier(instanceSupplier);

        // Bean 的作用域和代理模式
        ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(abd);
        abd.setScope(scopeMetadata.getScopeName());

        // bean 的名称
        String beanName = (name != null ? name : this.beanNameGenerator.generateBeanName(abd, this.registry));

        // 处理定义的公共注解信息
        AnnotationConfigUtils.processCommonDefinitionAnnotations(abd);

        // 处理限定修饰符，就是 @Primary、@Lazy、@Qualifier 这三个注解。
        if (qualifiers != null) {
            for (Class<? extends Annotation> qualifier : qualifiers) {
                if (Primary.class == qualifier) {
                    abd.setPrimary(true);
                } else if (Lazy.class == qualifier) {
                    abd.setLazyInit(true);
                } else {
                    abd.addQualifier(new AutowireCandidateQualifier(qualifier));
                }
            }
        }

        // 应用 bean 定义自定义器，对 bean 定义进行一些自定义
        for (BeanDefinitionCustomizer customizer : definitionCustomizers) {
            customizer.customize(abd);
        }

        BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
        // 根据 bean 的生命周期，使用 AOP 技术为该 bean 定义生成代理。
        definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
        // 把这个 bean 定义注册到容器中。
        // BeanDefinitionReaderUtils.registerBeanDefinition 内部通过 DefaultListableBeanFactory.registerBeanDefinition(String beanName, BeanDefinition beanDefinition) 按名称将 bean 定义信息注册到容器中，
        // 实际上 DefaultListableBeanFactory 内部维护一个 Map<String, BeanDefinition> 类型变量 beanDefinitionMap，用于保存注 bean 定义信息（beanname 和 beandefine 映射）
        BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, this.registry);
    }


    /**
     * Get the Environment from the given registry if possible, otherwise return a new
     * StandardEnvironment.
     */
    private static Environment getOrCreateEnvironment(BeanDefinitionRegistry registry) {
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
        if (registry instanceof EnvironmentCapable) {
            return ((EnvironmentCapable) registry).getEnvironment();
        }
        return new StandardEnvironment();
    }

}
