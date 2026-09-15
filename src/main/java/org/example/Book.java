package org.example;

/**
 * Record för Book klassen
 * Obs att klassen är final. Ingen kan ärva Book. Men fälten är också final kan inte ändras inga set metoder.
 * Vi får gratiskod för getmetoder, konstruktor, equals och hashcode
 */
public record Book(String titel, String forfattare, String isbn) {
}

