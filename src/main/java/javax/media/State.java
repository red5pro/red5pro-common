package javax.media;

/**
 * Controller and Processor state enums.
 * 
 * @author Paul Gregoire
 */
public enum State {

	// states are ordered by original ordinal value; original const values in
	// comment
	Unrealized, // 100
	Configuring, // 140
	Configured, // 180
	Realizing, // 200
	Realized, // 300
	Prefetching, // 400
	Prefetched, // 500
	Started; // 600

}
