package org.example;

/** Record för member klassen
 * Fälten blir private final automatiskt så man skriver inte det nedan.
 * private static final int MAX_LAN = 5; Sätter MAX_LAN till 5
 */
public record Member(String id, String namn, int antalLan) {

    private static final int MAX_LAN = 5;

    /** Metod för kontroll om man får låna fler böcker
     *
     */
    public boolean farLanaFler() {
    return antalLan < MAX_LAN;
    }

}
