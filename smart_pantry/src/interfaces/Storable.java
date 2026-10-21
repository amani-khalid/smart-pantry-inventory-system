package interfaces;

/**
 * Interface for objects that can be stored to and loaded from files
 */
public interface Storable {
	/**
	 * Converts the object to a string format for file storage
	 * 
	 * @return string representation for file storage
	 */
	String toFileString();
}