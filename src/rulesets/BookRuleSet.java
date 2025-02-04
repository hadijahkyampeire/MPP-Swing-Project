package rulesets;

import business.Author;
import librarysystem.BookWindow;

import java.awt.Component;
import java.util.List;

/**
 * Rules for Book Creation:
 * 1. All fields (ISBN, Title, Authors, Borrow Period) must be non-empty.
 * 2. ISBN must be numeric and consist of either 10 or 13 characters.
 * 3. If ISBN has 10 digits, it must start with 0 or 1.
 * 4. If ISBN has 13 digits, it must start with 978 or 979.
 * 5. Borrow Period must be either 7 days or 21 days.
 * 6. At least one Author must be added.
 */

public class BookRuleSet implements RuleSet {
    private BookWindow bookWindow;

    @Override
    public void applyRules(Component ob) throws RuleException {
        bookWindow = (BookWindow) ob;
        nonEmptyRule();
        isbnNumericRule();
        isbn10Or13DigitsRule();
        isbn10DigitsFirstNumberRule();
        isbn13DigitsFirstThreeRule();
        borrowPeriodRule();
        authorsRule();
    }

    private void nonEmptyRule() throws RuleException {
        String isbn = bookWindow.getIsbnValue().trim();
        String title = bookWindow.getTitleValue().trim();

        if (isbn.isEmpty() || title.isEmpty()) {
            throw new RuleException("ISBN and Title must not be empty.");
        }
    }

    private void isbnNumericRule() throws RuleException {
        String isbn = bookWindow.getIsbnValue().trim();
        try {
            Long.parseLong(isbn);
        } catch (NumberFormatException e) {
            throw new RuleException("ISBN must be numeric.");
        }
    }

    private void isbn10Or13DigitsRule() throws RuleException {
        String isbn = bookWindow.getIsbnValue().trim();
        if (isbn.length() != 10 && isbn.length() != 13) {
            throw new RuleException("ISBN must be either 10 or 13 digits.");
        }
    }

    private void isbn10DigitsFirstNumberRule() throws RuleException {
        String isbn = bookWindow.getIsbnValue().trim();
        if (isbn.length() == 10 && !(isbn.startsWith("0") || isbn.startsWith("1"))) {
            throw new RuleException("ISBN of 10 digits must start with 0 or 1.");
        }
    }

    private void isbn13DigitsFirstThreeRule() throws RuleException {
        String isbn = bookWindow.getIsbnValue().trim();
        if (isbn.length() == 13 && !(isbn.startsWith("978") || isbn.startsWith("979"))) {
            throw new RuleException("If ISBN has 13 digits, it must start with 978 or 979.");
        }
    }

    private void borrowPeriodRule() throws RuleException {
        int borrowPeriod = bookWindow.getBorrowPeriodValue().equals("7 days") ? 7 : 21;
        if (borrowPeriod != 7 && borrowPeriod != 21) {
            throw new RuleException("Borrow period must be either 7 or 21 days.");
        }
    }

    private void authorsRule() throws RuleException {
        List<Author> authors = bookWindow.getAuthors();
        if (authors.isEmpty()) {
            throw new RuleException("At least one Author must be added.");
        }
    }
}
