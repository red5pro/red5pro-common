package javax.media;

/**
 * Controller and Processor state enums.
 *
 * @author Paul Gregoire
 */
public enum State {

    // states are ordered by original ordinal value; original const values in
    // comment
    /** Initial state; no resources acquired (100). */
    Unrealized,
    /** Configuration of a processor is in progress (140). */
    Configuring,
    /** Processor is configured (180). */
    Configured,
    /** Realization is in progress (200). */
    Realizing,
    /** Resources needed for operation are known (300). */
    Realized,
    /** Prefetching of media data is in progress (400). */
    Prefetching,
    /** Media data has been prefetched and the controller is ready to start (500). */
    Prefetched,
    /** Running (600). */
    Started;

}
