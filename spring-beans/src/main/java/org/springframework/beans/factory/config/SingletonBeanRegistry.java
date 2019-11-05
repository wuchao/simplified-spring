package org.springframework.beans.factory.config;

import org.springframework.lang.Nullable;

public interface SingletonBeanRegistry {

	/**
	 * 在给定的 beanName 下，在 Bean 注册表中将给定的现有对象注册为 singleton
	 *
	 * @param beanName
	 * @param singletonObject
	 */
	void registerSingleton(String beanName, Object singletonObject);

	/**
	 * 返回在给定名称下注册的（原始）单例对象
	 *
	 * @param beanName
	 * @return
	 */
	@Nullable
	Object getSingleton(String beanName);

	/**
	 * 检查此注册表是否包含具有给定名称的单例实例
	 *
	 * @param beanName
	 * @return
	 */
	boolean containsSingleton(String beanName);

	/**
	 * 回在此注册表中已注册的所有单例 Bean 的名称
	 *
	 * @return
	 */
	String[] getSingletonNames();

	/**
	 * 返回在此注册表中已注册的单例 Bean 的数量
	 *
	 * @return
	 */
	int getSingletonCount();

	/**
	 * 返回此注册表使用的单例互斥锁（对于外部协作者）
	 *
	 * @return
	 */
	Object getSingletonMutex();

}
