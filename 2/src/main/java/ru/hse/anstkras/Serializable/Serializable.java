package ru.hse.anstkras.Serializable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface that allows converting an object into a stream by using {@code serialize}
 * and constructing it from given stream by using {@code deserialize}
 */
public interface Serializable {
    /**
     * Converts object into a stream
     *
     * @param out the stream to write the converted object
     * @throws IOException in case of problems with {@code out}
     */
    void serialize(OutputStream out) throws IOException;

    /**
     * Constructs object from a stream
     *
     * @param in the stream to get the object from
     * @throws IOException in case of problems with {@code in}
     */
    void deserialize(InputStream in) throws IOException;
}
