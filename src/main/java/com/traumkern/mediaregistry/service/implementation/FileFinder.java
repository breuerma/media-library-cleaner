package com.traumkern.mediaregistry.service.implementation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

/**
 * This class exists to enable the mocking of the file scanning.
 *
 * @author breuerma
 *
 */

@Component
public class FileFinder {

    public Stream<Path> find(final Path argPath, final int argMaxScannerDepth,
            final BiPredicate<Path, BasicFileAttributes> argMatcher) throws IOException {
        return Files.find(argPath, argMaxScannerDepth, argMatcher);
    }

}
