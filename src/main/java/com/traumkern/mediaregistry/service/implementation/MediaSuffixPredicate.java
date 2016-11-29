package com.traumkern.mediaregistry.service.implementation;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MediaSuffixPredicate implements BiPredicate<Path, BasicFileAttributes>, InitializingBean {

    @Value("${mediaregistry.suffixList}")
    private String suffixListConfigured;

    private final List<String> suffixList;

    public MediaSuffixPredicate() {
        this.suffixList = new ArrayList<String>();
    }

    @Override
    public boolean test(final Path argPath, final BasicFileAttributes argAttributes) {
        if (argAttributes.isDirectory() || argAttributes.isSymbolicLink() || argAttributes.isOther()) {
            return false;
        }
        return this.suffixList.stream()
                              .filter(hasMediaSuffix(argPath.toString()))
                              .collect(Collectors.counting()) > 0;
    }

    @Override
    // For some reason the list property injection does not work - so there is this workaround
    public void afterPropertiesSet() throws Exception {
        Arrays.asList(this.suffixListConfigured.split(","))
              .stream()
              .map(mySuffix -> mySuffix.trim())
              .forEachOrdered(this.suffixList::add);

    }

    public static Predicate<String> hasMediaSuffix(final String argFileName) {
        return p -> {
            final String myFileNameLowerCase = argFileName.toLowerCase();
            return myFileNameLowerCase.endsWith(p.toLowerCase());
        };
    }

}
