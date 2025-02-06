package business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

final public class LibraryMember extends Person implements Serializable {
	private static final long serialVersionUID = -2226197306790714013L;
	private final String memberId;
	private List<CheckoutEntry> checkoutEntries;
	
	public LibraryMember(String memberId, String fname, String lname, String tel,Address add) {
		super(fname,lname, tel, add);
		this.memberId = memberId;
		this.checkoutEntries = new ArrayList<CheckoutEntry>();
	}
	
	public String getMemberId() {
		return memberId;
	}

	public void addCheckoutEntry(CheckoutEntry entry) {
		if (checkoutEntries == null) {
			checkoutEntries = new ArrayList<>(); // Extra safety check before adding
		}
		checkoutEntries.add(entry);
	}

	public List<CheckoutEntry> getCheckoutEntries() {
		return checkoutEntries;
	}
	
	@Override
	public String toString() {
		return "Member Info: " + "ID: " + memberId + ", name: " + getFirstName() + " " + getLastName() + 
				", " + getTelephone() + " " + getAddress();
	}
}
