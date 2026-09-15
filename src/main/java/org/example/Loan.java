package org.example;

/**
 * Record för Lån klassen dvs är ett lån i biblioteket.
 * <p>
 * Exempel på vad man kommer åt genom lånet:
 * lan.book().isbn()       // "91-1-234567-8"
 * lan.book().titel()      // "Sagan om ringen"
 * lan.member().getId()    // "M01"
 * lan.member().getNamn()  // "Anna"
 * Kopplar ihop bok och medem
 */
public record Loan(Member member, Book book) {
}
