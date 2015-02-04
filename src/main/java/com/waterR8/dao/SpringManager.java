package com.waterR8.dao;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;


/** Handle the creation of the Spring Context
 * 
 * @author casey
 *
 */
public class SpringManager {
    
    static Logger __logger = Logger.getLogger(SpringManager.class);
    

    static private SpringManager __instance;
    static public SpringManager getInstance() throws Exception {
        if(__instance == null) {
            __instance = new SpringManager();
        }
        return __instance;
    }
    
    
    BeanFactory _beanFactory;
    private SpringManager() throws Exception {
        __logger.info("Creating new SpringManager");
        ClassPathResource resource = new ClassPathResource("spring.xml");
        _beanFactory = new XmlBeanFactory(resource);
    }
    
    public BeanFactory getBeanFactory() {
        return _beanFactory;
    }
}

