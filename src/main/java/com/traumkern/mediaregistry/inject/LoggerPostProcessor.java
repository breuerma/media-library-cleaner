package com.traumkern.mediaregistry.inject;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import net.vidageek.mirror.dsl.Mirror;

@Component
public class LoggerPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(final Object argBean, final String argBeanName)
            throws BeansException {
        final List<Field> myFieldList = new Mirror().on(argBean.getClass())
                                                    .reflectAll()
                                                    .fields();
        for (final Field myField : myFieldList) {
            final InjectLogger myLoggerAnnotation = new Mirror().on(myField)
                                                                .reflect()
                                                                .annotation(InjectLogger.class);
            final boolean myFieldHasLoggerAnnotation = (myLoggerAnnotation != null);
            final boolean myFieldReferencesLogger = Log.class.isAssignableFrom(myField.getType());
            if (myFieldReferencesLogger && myFieldHasLoggerAnnotation) {
                final Log myNewLogger = LogFactory.getLog(argBean.getClass());
                new Mirror().on(argBean)
                            .set()
                            .field(myField)
                            .withValue(myNewLogger);
            }
        }
        return argBean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object argBean, final String beanName) throws BeansException {
        return argBean;
    }

}
