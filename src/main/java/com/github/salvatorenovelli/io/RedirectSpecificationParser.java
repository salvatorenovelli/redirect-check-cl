package com.github.salvatorenovelli.io;

import java.io.IOException;

public interface RedirectSpecificationParser {
    void parse() throws IOException;
    int getNumSpecs();
}
