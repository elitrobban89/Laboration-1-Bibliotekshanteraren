package org.example;

/**
 * Record för Book klassen
 * obs att den är final kan inte ändras inga set metoder.
 * Vi får gratiskod för getmetoder, konstruktor, equals och hashcode
 */
public record Book(String titel, String forfattare, String isbn) {
}

