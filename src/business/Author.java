package business;

import java.io.Serializable;

final public class Author extends Person implements Serializable {
	private static final long serialVersionUID = 7508481940058530471L;
	private String bio;
	private String credentials;

	public String getBio() {
		return bio;
	}

	@Override
	public String toString() {
		return "Author{" +
				"bio='" + bio + '\'' +
				", credentials='" + credentials + '\'' +
				'}';
	}

	public String getCredentials() {
		return credentials;
	}
	
	public Author(String f, String l, String t, Address a, String bio, String credentials) {
		super(f, l, t, a);
		this.bio = bio;
		this.credentials = credentials;
	}
}
