package business;

import java.io.Serializable;
import java.time.LocalDate;

public class CheckoutEntry implements Serializable {
    private BookCopy bookCopy;
    private LocalDate checkoutDate;
    private LocalDate dueDate;

    public CheckoutEntry(BookCopy bookCopy, LocalDate checkoutDate) {
        this.bookCopy = bookCopy;
        this.checkoutDate = checkoutDate.minusDays(25);
        this.dueDate = this.checkoutDate.plusDays(bookCopy.getBook().getMaxCheckoutLength()); // Calculate due date based on checkout period
        LocalDate returnDate = null;
    }

    public BookCopy getBookCopy() {
        return bookCopy;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    @Override
    public String toString() {
        return "Book Copy: " + bookCopy.getCopyNumber() +
                " | Checked out on: " + checkoutDate +
                " | Due: " + dueDate;
    }
}
