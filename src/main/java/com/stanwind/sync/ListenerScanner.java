package com.stanwind.sync;

import com.stanwind.sync.CanalConstant.EventType;
import com.stanwind.sync.anno.Listener;
import com.stanwind.sync.anno.SyncListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * ListenerScanner 注解扫描配置映射关系 ----------*********可以继续拆分***
 *
 * @author : Stan
 * @version : 1.0
 * @date :  2019-08-07 15:02
 **/
@Component
public class ListenerScanner implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(ListenerScanner.class);

    public List<Listener> DELETE_METHODS = new CopyOnWriteArrayList<>();
    public List<Listener> INSERT_METHODS = new CopyOnWriteArrayList<>();
    public List<Listener> UPDATE_METHODS = new CopyOnWriteArrayList<>();

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("start to scan listeners");
        //遍历所有bean
        String[] beans = applicationContext.getBeanDefinitionNames();
        for (String beanName : beans) {
            Object bean = applicationContext.getBean(beanName);
            Class<?> beanType = AopUtils.getTargetClass(bean);
            if (Objects.isNull(beanType)) {
                continue;
            }

            for (Method method : beanType.getMethods()) {
                for (Annotation anno : method.getDeclaredAnnotations()) {
                    if (anno.annotationType().equals(SyncListener.class)) {
                        Listener listener = createListener((SyncListener) anno, beanType, method);
                        pushToList(((SyncListener) anno).type(), listener);
                    }
                }
            }
        }
    }

    protected void pushToList(EventType type, Listener listener) {
        logger.info("Exec method {}, when table 【{}.{}】 {}", listener.getMethod().getName(), listener.getDb(),
                listener.getTables(), type.getRemark());
        switch (type) {
            case ALL:
                DELETE_METHODS.add(listener);
                INSERT_METHODS.add(listener);
                UPDATE_METHODS.add(listener);
                break;
            case INSERT:
                INSERT_METHODS.add(listener);
                break;
            case DELETE:
                DELETE_METHODS.add(listener);
                break;
            case UPDATE:
                UPDATE_METHODS.add(listener);
                break;
            default:
                ;
        }
    }

    protected Listener createListener(SyncListener l, Class<?> beanType, Method method) {
        Listener listener = new Listener();
        listener.setDb(l.db());
        listener.setKeys(Arrays.asList(l.key()));
        listener.setTables(Arrays.asList(l.table()));
        listener.setClaz(beanType);
        listener.setMethod(method);

        if (listener.getKeys().size() > listener.getTables().size()) {
            logger.warn("表和主键配置不一致 {}", method.getName());
        }

        return listener;
    }

    /**
     * @param beanName   对象
     * @param methodName 方法名称
     * @param params     参数
     */
    public Object springInvokeMethod(String beanName, String methodName, Object[] params) throws Exception {
        Object service = applicationContext.getBean(beanName);
        Class<? extends Object>[] paramClass = null;
        if (params != null) {
            int paramsLength = params.length;
            paramClass = new Class[paramsLength];
            for (int i = 0; i < paramsLength; i++) {
                paramClass[i] = params[i].getClass();
            }
        }
        // 找到方法
        Method method = ReflectionUtils.findMethod(service.getClass(), methodName, paramClass);
        // 执行方法
        return ReflectionUtils.invokeMethod(method, service, params);

    }
}