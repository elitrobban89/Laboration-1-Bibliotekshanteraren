package org.example;

/**
 * En medlem i biblioteket.
 * Till skillnad från Book är detta en vanlig klass, eftersom antalet aktiva
 * lån ändras över tid medan medlemmen behåller sin identitet.
 */
public class Member {

    /**
     * Högsta antal aktiva lån en medlem får ha samtidigt.
     */
    private static final int MAX_LAN = 5;

    private final String id;
    private final String namn;
    private int antalLan;

    /**
     * Konstruktor
     */
    public Member(String id, String namn, int antalLan) {
        this.id = id;
        this.namn = namn;
        this.antalLan = antalLan;
    }

    /**
     * GetMetoder
     */
    public int getAntalLan() {
        return antalLan;
    }

    public void setAntalLan(int antalLan) {
        this.antalLan = antalLan;
    }

    public String getId() {
        return id;
    }

    public String getNamn() {
        return namn;
    }

    /**
     * Egen metod för kontroll om man får låna fler böcker
     */
    public boolean farLana() {
        return antalLan < MAX_LAN;
    }
}
