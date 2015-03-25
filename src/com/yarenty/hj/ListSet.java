package com.yarenty.hj;

/**
 * <p>ListSet interface.</p>
 *
 * @author Shams Imam (shams@rice.edu)
 */
public interface ListSet {

    /**
     * <p>add.</p>
     *
     * @param o a {@link Object} object.
     * @return a int.
     */
    int add(final Object o);

    /**
     * <p>remove.</p>
     *
     * @param o a {@link Object} object.
     * @return a int.
     */
    int remove(final Object o);

    /**
     * <p>contains.</p>
     *
     * @param o a {@link Object} object.
     * @return a boolean.
     */
    boolean contains(final Object o);
}
