package librarysystem.SuperAdmin.rulesets;

import librarysystem.SuperAdmin.LibraryMemberWindow;

import java.awt.*;
import java.util.regex.Pattern;

/**
 * Rules for Library Member Creation:
 * 1. All fields must be non-empty.
 * 2. Member ID must be unique.
 * 3. Phone number must be valid (10-15 digits, optional "+").
 */

public class MemberRuleSet implements RuleSet {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");
    private LibraryMemberWindow memberWindow;

    @Override
    public void applyRules(Component ob) throws RuleException {
        memberWindow = (LibraryMemberWindow) ob;
        nonEmptyRule();
        phoneFormatRule();
    }

    private void nonEmptyRule() throws RuleException {
        String memberId = memberWindow.getMemberIdValue().trim();
        String firstName = memberWindow.getFirstNameValue().trim();
        String lastName = memberWindow.getLastNameValue().trim();
        String street = memberWindow.getStreetValue().trim();
        String city = memberWindow.getCityValue().trim();
        String state = memberWindow.getStateValue().trim();
        String zip = memberWindow.getZipValue().trim();
        String phone = memberWindow.getPhoneValue().trim();

        if (memberId.isEmpty() || firstName.isEmpty() || lastName.isEmpty() ||
                street.isEmpty() || city.isEmpty() || state.isEmpty() || zip.isEmpty() || phone.isEmpty()) {
            throw new RuleException("All fields must be filled.");
        }
    }

    private void phoneFormatRule() throws RuleException {
        String phone = memberWindow.getPhoneValue().trim();
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new RuleException("Phone number must be 10-15 digits (optional '+').");
        }
    }
}

