/*
 *  Copyright 2015-2018 DataVens, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.anoyi.grpc.binding;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.*;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * <p> 扫描所有的rpc实现类，并放入容器中 </p>
 * 1. <接口全限定名, 服务实现bean代理类>
 *
 * @author changming.Y <changming.yang.ah@gmail.com>
 * @since 2022-02-21 15:01
 */
@Slf4j
public class GrpcServiceRegisterScanner implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (contextRefreshedEvent.getApplicationContext().getParent() == null) {
            registerBeans(contextRefreshedEvent.getApplicationContext());
        }
    }

    private void registerBeans(ApplicationContext applicationContext) {
        Class<? extends Annotation> annotationClass = com.anoyi.grpc.annotation.GrpcServiceImpl.class;
        Map<String, Object> beanWhithAnnotation = applicationContext.getBeansWithAnnotation(annotationClass);
        Set<Map.Entry<String, Object>> entitySet = beanWhithAnnotation.entrySet(    );

        for (Map.Entry<String, Object> entry : entitySet) {
            Object bean = entry.getValue();
            if (bean == null) {
                continue;
            }
            Class<? extends Object> clazz = bean.getClass();
            Class[] interfaces = clazz.getInterfaces();
            if (interfaces == null || interfaces.length != 1) {
                log.error("Only have one interface about rpc service.");
                throw new RuntimeException("Only have one interface about rpc service.");
            }
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
            beanFactory.registerSingleton(interfaces[0].getCanonicalName(), bean);
        }
    }
}
