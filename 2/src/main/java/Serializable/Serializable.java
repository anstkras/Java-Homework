package Serializable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

interface Serializable {
    void serialize(OutputStream out) throws IOException;

    void deserialize(InputStream in) throws IOException;
}
