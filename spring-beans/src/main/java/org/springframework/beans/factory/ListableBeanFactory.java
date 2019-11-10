/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Extension of the {@link BeanFactory} interface to be implemented by bean factories
 * that can enumerate all their bean instances, rather than attempting bean lookup
 * by name one by one as requested by clients. BeanFactory implementations that
 * preload all their bean definitions (such as XML-based factories) may implement
 * this interface.
 *
 * <p>If this is a {@link HierarchicalBeanFactory}, the return values will <i>not</i>
 * take any BeanFactory hierarchy into account, but will relate only to the beans
 * defined in the current factory. Use the {@link BeanFactoryUtils} helper class
 * to consider beans in ancestor factories too.
 *
 * <p>The methods in this interface will just respect bean definitions of this factory.
 * They will ignore any singleton beans that have been registered by other means like
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}'s
 * {@code registerSingleton} method, with the exception of
 * {@code getBeanNamesOfType} and {@code getBeansOfType} which will check
 * such manually registered singletons too. Of course, BeanFactory's {@code getBean}
 * does allow transparent access to such special beans as well. However, in typical
 * scenarios, all beans will be defined by external bean definitions anyway, so most
 * applications don't need to worry about this differentiation.
 *
 * <p><b>NOTE:</b> With the exception of {@code getBeanDefinitionCount}
 * and {@code containsBeanDefinition}, the methods in this interface
 * are not designed for frequent invocation. Implementations may be slow.
 */
public interface ListableBeanFactory extends BeanFactory {

	/**
	 * 判断传入的 beanName 是否在 BeanFactory 中被定义
	 *
	 * @param beanName
	 * @return
	 */
	boolean containsBeanDefinition(String beanName);

	/**
	 * 返回 BeanFactory 中的定义定义了的 Bean 的个数
	 *
	 * @return
	 */
	int getBeanDefinitionCount();

	/**
	 * 返回 BeanFactory 中定义了的 Bean 的名字
	 *
	 * @return
	 */
	String[] getBeanDefinitionNames();

	/**
	 * 返回与传入的类型（type）（包括子类）匹配的 Bean 的名称，从 Bean 定义或 FactoryBeans 的 getObjectType 值判断
	 *
	 * @param type
	 * @return
	 */
	String[] getBeanNamesForType(ResolvableType type);

	/**
	 * 返回对于指定类型 Bean（包括子类）的所有名字
	 *
	 * @param type
	 * @return
	 */
	String[] getBeanNamesForType(@Nullable Class<?> type);


	String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit);


	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException;


	<T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException;


	String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType);


	Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException;


	@Nullable
	<A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType)
			throws NoSuchBeanDefinitionException;

}

