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

        if (hittaLan(isbn) != null) return false; //Kollar om boken redan är utlånad eller inte

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
    public Book hittaBok(String isbn) {
        for (int i = 0; i < antalBocker; i++) {
            if (boklista[i].isbn().equals(isbn)) {
                return boklista[i];
            }
        }
        return null;
    }

    /**
     * För Menyval 5 kan vi inte använda hittaBok utan får skapa en ny egen metod som hittar boken via titel eller författare
     * dock så använder vi här sökord istället för både titel och författare
     */
    public Book[] sokBok(String sokord) {
        Book[] traffar = new Book[antalBocker];
        int antalTraffar = 0;
        for (int i = 0; i < antalBocker; i++) {
            if (boklista[i].titel().toLowerCase().contains(sokord.toLowerCase()) || boklista[i].forfattare().toLowerCase().contains(sokord.toLowerCase())) {
                traffar[antalTraffar] = boklista[i];
                antalTraffar++;
            }
        }
        Book[] kopia = new Book[antalTraffar];
        for (int i = 0; i < antalTraffar; i++) {
            kopia[i] = traffar[i];
        }
        return kopia;
    }


    /**
     * Metod som hittar medlemmen via medlemsid
     */
    public Member hittaMedlem(String medlemsId) {
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
    public Loan hittaLan(String isbn) {
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
        if (hittaMedlem(medlem.getId()) != null) return false;

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
     * Dubblettkontroll införd.
     */
    public boolean laggTillBok(Book bok) {
        if (hittaBok(bok.isbn()) != null) return false; //Dublettkontroll om boken redan finns i arrayen
        if (antalBocker < MAX_BOCKER) {
            boklista[antalBocker] = bok;
            antalBocker++;
            return true;
        }
        return false; //Array är full utgång
    }

    public boolean aterlamnaBok(String isbn) {
        Book bok = hittaBok(isbn);
        if (bok == null) return false;

        for (int i = 0; i < antalAktivaLan; i++) {
            if (loans[i].book().isbn().equals(isbn)) { //Om man återlämnar en bok
                Member medlem = loans[i].member();
                medlem.setAntalLan(medlem.getAntalLan() - 1); //Ta bort lånet utav boken
                loans[i] = loans[antalAktivaLan - 1];
                loans[antalAktivaLan - 1] = null;
                antalAktivaLan--;
                return true;
            }
        }
        return false;
    }

    /**
     * Vi använder en kopia av Book arrayen för att kunna returnera en kopia av boklistan för användaren
     *
     * @return
     */
    public Book[] getAllaBocker() {
        Book[] kopia = new Book[antalBocker];
        for (int i = 0; i < antalBocker; i++) {
            kopia[i] = boklista[i];
        }
        return kopia;
    }

    /**
     * Egen sorteringsalgoritm (krav för VG)
     * Obs tillämpas på vår kopia utav arrayen så vi inte påverkar den ursprungliga arrayen
     * Stödjer menyval 6 men även 5 ifall det behövs
     */
    public Book[] sorteraPaTitel(Book[] bocker) {
        for (int i=0; i<bocker.length - 1; i++) {
            for (int j=0; j<bocker.length -1; j++) { //Bubble sort algoritm
                if (bocker[j].titel().compareToIgnoreCase(bocker[j + 1].titel()) > 0) { //Obs här vi använder titel() istället för getTitle() ifrån Recordet Book. Vi ignorerar case sensitive
                    Book temp = bocker[j];
                    bocker[j] = bocker[j+1];
                    bocker[j+1] = temp;
                }
            }
        }
        return bocker;
    }
}
