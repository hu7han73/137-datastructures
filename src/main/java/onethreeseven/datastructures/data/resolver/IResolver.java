package onethreeseven.datastructures.data.resolver;


/**
 * Resolve some type into another type
 * @param <I> Input type to resolve
 * @param <O> The output to that is resolved to
 * @author Luke Bermingham
 */
interface IResolver<I,O> {

    /**
     * Resolve some input into the output type.
     * @param in The input
     * @return The "resolved" output.
     */
    O resolve(I in);

}
