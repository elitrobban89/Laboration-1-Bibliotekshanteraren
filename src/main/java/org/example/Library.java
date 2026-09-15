package org.example;

/**
 * Book[] boklista håller alla böcker i biblioteket
 * Member[] medlemmar håller alla medlemmar
 */
public class Library {

    private int antalBocker = 0;
    private int antalMedlemmar = 0;
    private int antalAktivaLan = 0;

    private static final int MAX_BOCKER = 100;
    private static final int MAX_MEDLEMMAR = 50;

    private Book[] boklista = new Book[MAX_BOCKER];
    private Member[] medlemmar = new Member[MAX_MEDLEMMAR];
    private Loan[] loans = new Loan[MAX_BOCKER];

    /**
     * Låna bok metod kollar att den inte redan är utlånad
     */
    public boolean lanaBok(String isbn, String medlemsId) { //Menyval 3
        Book bok = hittaBok(isbn);
        if (bok == null) return false;

        if (hittaLan(isbn) != null) return false;

        Member medlem = hittaMedlem(medlemsId);
        if (medlem == null) return false;
        if (!medlem.farLana()) return false;

        loans[antalAktivaLan] = new Loan(medlem, bok);
        antalAktivaLan++;
        medlem.setAntalLan(medlem.getAntalLan() + 1);
        return true;
    }

    /**
     * Hittar boken via isbn nummer
     */
    private Book hittaBok(String isbn) {
        for (int i = 0; i < antalBocker; i++) {
            if (boklista[i].isbn().equals(isbn)) {
                return boklista[i];
            }
        }
        return null;
    }

    /**
     * Metod som hittar medlemmen via medlemsid
     */
    private Member hittaMedlem(String medlemsId) {
        for (int i = 0; i < antalMedlemmar; i++) {
            if (medlemmar[i].getId().equals(medlemsId)) {
                return medlemmar[i];
            }
        }
        return null;
    }

    /**
     * Metod som hittar lån via bokens isbn om boken är utlånad eller ej
     */
    private Loan hittaLan(String isbn) {
        for (int i = 0; i < antalAktivaLan; i++) {
            if (loans[i].book().isbn().equals(isbn)) {
                return loans[i];
            }
        }
        return null;
    }

    /**
     * Metod som registrerar medlem med kontroller
     */
    public boolean registreraMedlem(Member medlem) {
        if (antalMedlemmar < MAX_MEDLEMMAR) {
            medlemmar[antalMedlemmar] = medlem;
            antalMedlemmar++;
        } else {
            return false;
        }
        return true;
    }

    /**
     * Lägger till bok i boklistan så man kan låna den
     */
    public boolean laggTillBok(Book bok) {
        if (antalBocker < MAX_BOCKER) {
            boklista[antalBocker] = bok;
            antalBocker++;
            return true;
        }
        return false; //Array är full utgång
    }
}
