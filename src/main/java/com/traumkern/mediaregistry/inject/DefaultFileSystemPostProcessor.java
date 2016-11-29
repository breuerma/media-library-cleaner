package com.traumkern.mediaregistry.inject;

import java.lang.reflect.Field;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import net.vidageek.mirror.dsl.Mirror;

@Component
public class DefaultFileSystemPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(final Object argBean, final String argBeanName)
            throws BeansException {
        final List<Field> myFieldList = new Mirror().on(argBean.getClass())
                                                    .reflectAll()
                                                    .fields();
        for (final Field myField : myFieldList) {
            final InjectFileSystem myFileSystemAnnotation = new Mirror().on(myField)
                                                                        .reflect()
                                                                        .annotation(InjectFileSystem.class);
            final boolean myFieldHasFileSystemAnnotation = (myFileSystemAnnotation != null);
            final boolean myFieldReferencesFileSystem = FileSystem.class.isAssignableFrom(myField.getType());
            if (myFieldReferencesFileSystem && myFieldHasFileSystemAnnotation) {
                final FileSystem myNewDefaultFileSystem = FileSystems.getDefault();
                new Mirror().on(argBean)
                            .set()
                            .field(myField)
                            .withValue(myNewDefaultFileSystem);
            }
        }
        return argBean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object argBean, final String beanName) throws BeansException {
        return argBean;
    }

}
