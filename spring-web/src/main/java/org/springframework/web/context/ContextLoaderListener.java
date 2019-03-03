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

package org.springframework.web.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 继承 Spring 的 ContextLoader 上下文加载器类 —— 管理 Spring 的启动和销毁，
 * 实现 ServletContextListener 接口（Servlet 上下文监听器）—— 监听 Web 服务器上下文的启动和停止事件。
 * <p>
 * Web 应用启动和停止时，Web 服务器会触发 ServletContextListener 接口的两个方法，这两个方法就会通过 ContextLoader 启动和销毁 Spring 的上下文。
 * <p>
 * <p>
 * <p>
 * Bootstrap listener to start up and shut down Spring's root {@link WebApplicationContext}.
 * Simply delegates to {@link ContextLoader} as well as to {@link ContextCleanupListener}.
 *
 * <p>As of Spring 3.1, {@code ContextLoaderListener} supports injecting the root web
 * application context via the {@link #ContextLoaderListener(WebApplicationContext)}
 * constructor, allowing for programmatic configuration in Servlet 3.0+ environments.
 * See {@link org.springframework.web.WebApplicationInitializer} for usage examples.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @see #setContextInitializers
 * @see org.springframework.web.WebApplicationInitializer
 * @since 17.02.2003
 */
public class ContextLoaderListener extends ContextLoader implements ServletContextListener {

    /**
     * Create a new {@code ContextLoaderListener} that will create a web application
     * context based on the "contextClass" and "contextConfigLocation" servlet
     * context-params. See {@link ContextLoader} superclass documentation for details on
     * default values for each.
     * <p>This constructor is typically used when declaring {@code ContextLoaderListener}
     * as a {@code <listener>} within {@code web.xml}, where a no-arg constructor is
     * required.
     * <p>The created application context will be registered into the ServletContext under
     * the attribute name {@link WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE}
     * and the Spring application context will be closed when the {@link #contextDestroyed}
     * lifecycle method is invoked on this listener.
     *
     * @see ContextLoader
     * @see #ContextLoaderListener(WebApplicationContext)
     * @see #contextInitialized(ServletContextEvent)
     * @see #contextDestroyed(ServletContextEvent)
     */
    public ContextLoaderListener() {
    }

    /**
     * ContextLoaderListener
     * 作为一个Listener会首先启动，创建一个WebApplicationContext用于加载除Controller等Web组件以外的所有bean，
     * 这个ApplicationContext作为根容器存在，对于整个Web应用来说，只能存在一个，也就是父容器，会被所有子容器共享，
     * 子容器可以访问父容器里的bean，反过来则不行。
     * A. XML配置下会直接创建ContextLoaderListener，然后在contextInitialized方法中初始化WebApplicationContext。
     * B. 如果使用的是AnnotationConfig，则通过AnnotationConfigWebApplicationContext获取一个WebApplicationContext，
     * 之后传给ContextLoadListener。
     * 之后再contextInitialized方法中调用父类ContextLoader的initWebApplicationContext进行初始化。
     *
     * Create a new {@code ContextLoaderListener} with the given application context. This
     * constructor is useful in Servlet 3.0+ environments where instance-based
     * registration of listeners is possible through the {@link javax.servlet.ServletContext#addListener}
     * API.
     * <p>The context may or may not yet be {@linkplain
     * org.springframework.context.ConfigurableApplicationContext#refresh() refreshed}. If it
     * (a) is an implementation of {@link ConfigurableWebApplicationContext} and
     * (b) has <strong>not</strong> already been refreshed (the recommended approach),
     * then the following will occur:
     * <ul>
     * <li>If the given context has not already been assigned an {@linkplain
     * org.springframework.context.ConfigurableApplicationContext#setId id}, one will be assigned to it</li>
     * <li>{@code ServletContext} and {@code ServletConfig} objects will be delegated to
     * the application context</li>
     * <li>{@link #customizeContext} will be called</li>
     * <li>Any {@link org.springframework.context.ApplicationContextInitializer ApplicationContextInitializer org.springframework.context.ApplicationContextInitializer ApplicationContextInitializers}
     * specified through the "contextInitializerClasses" init-param will be applied.</li>
     * <li>{@link org.springframework.context.ConfigurableApplicationContext#refresh refresh()} will be called</li>
     * </ul>
     * If the context has already been refreshed or does not implement
     * {@code ConfigurableWebApplicationContext}, none of the above will occur under the
     * assumption that the user has performed these actions (or not) per his or her
     * specific needs.
     * <p>See {@link org.springframework.web.WebApplicationInitializer} for usage examples.
     * <p>In any case, the given application context will be registered into the
     * ServletContext under the attribute name {@link
     * WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE} and the Spring
     * application context will be closed when the {@link #contextDestroyed} lifecycle
     * method is invoked on this listener.
     *
     * @param context the application context to manage
     * @see #contextInitialized(ServletContextEvent)
     * @see #contextDestroyed(ServletContextEvent)
     */
    public ContextLoaderListener(WebApplicationContext context) {
        super(context);
    }

    /**
     * Initialize the root web application context.
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {
        initWebApplicationContext(event.getServletContext());
    }


    /**
     * Close the root web application context.
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        closeWebApplicationContext(event.getServletContext());
        ContextCleanupListener.cleanupAttributes(event.getServletContext());
    }

}
