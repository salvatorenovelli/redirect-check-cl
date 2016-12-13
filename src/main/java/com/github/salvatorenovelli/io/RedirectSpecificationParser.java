package com.github.salvatorenovelli.io;

import com.github.salvatorenovelli.model.RedirectSpecification;

import java.io.IOException;
import java.util.function.Consumer;

public interface RedirectSpecificationParser {
    void parse(Consumer<RedirectSpecification> consumer) throws IOException;

    int getNumSpecs();
}
