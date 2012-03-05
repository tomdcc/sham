package org.shamdata;

import java.util.Random;

/**
 * A data generator. This interface contains methods used by the {@link Sham} class
 * on all generators.
 */
public interface ShamGenerator {

    /**
     * Sets the random number generator for this generator to use. Generally
     * this will be called by the {@link Sham} class when registering a
     * generator, so as to have a single shared RNG across all generators.
     *
     * @param random the RNG to use
     */
    void setRandom(Random random);
}
