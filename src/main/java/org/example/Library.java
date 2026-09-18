package org.example;

/**
 * Book[] boklista håller alla böcker i biblioteket
 * Member[] medlemmar håller alla medlemmar
 * Loan[] loans håller de aktiva lånen, dvs vilka böcker som är utlånade och till vem
 * Alla tre arrayerna växer automatiskt när de blir fulla (krav för VG)
 */
public class Library {

    private int antalBocker = 0;
    private int antalMedlemmar = 0;
    private int antalAktivaLan = 0;

    /**
     * Startstorlekar för arrayerna, inte maxvärden (krav för VG: dynamisk kapacitet).
     * När en array blir full byts den ut mot en dubbelt så stor via dynamiskArray(),
     * dynamiskMember() respektive dynamiskLan(), och programmet fortsätter.
     * Värdena är små med avsikt, så att växlingen går att visa utan att först mata in hundra böcker.
     */
    private static final int START_BOCKER = 4;
    private static final int START_MEDLEMMAR = 2;

    private Book[] boklista = new Book[START_BOCKER];
    private Member[] medlemmar = new Member[START_MEDLEMMAR];
    private Loan[] loans = new Loan[START_BOCKER];

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
        if (antalAktivaLan >= loans.length) {
            loans = dynamiskLan(loans); //Tillkommer Dynamisk kapacitet
        }
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
     * Uppdaterad: för dynamisk kapacitet Member array
     */
    public boolean registreraMedlem(Member medlem) {
        if (hittaMedlem(medlem.getId()) != null) return false;

        if (antalMedlemmar >= medlemmar.length) { //Om arrayen är full använd dynamisk Member array istället
            medlemmar=dynamiskMember(medlemmar); //Fältet medlemmar pekar om på ny array
        }
        medlemmar[antalMedlemmar] = medlem;
        antalMedlemmar++;
        return true;
    }

    /**
     * Lägger till bok i boklistan så man kan låna den
     * Dubblettkontroll införd.
     */
    public boolean laggTillBok(Book bok) {
        if (hittaBok(bok.isbn()) != null) return false; //Dublettkontroll om boken redan finns i arrayen

        if (antalBocker >= boklista.length) { //Om arrayen är full anropa ny arrayen som växer dynamiskt
            boklista = dynamiskArray(boklista);
        }
        boklista[antalBocker] = bok; //Lagra böckerna i ny array
        antalBocker++;
        return true;
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
        for (int i = 0; i < bocker.length - 1; i++) {
            for (int j = 0; j < bocker.length - 1; j++) { //Bubble sort algoritm
                if (bocker[j].titel().compareToIgnoreCase(bocker[j + 1].titel()) > 0) { //Obs här vi använder titel() istället för getTitle() ifrån Recordet Book. Vi ignorerar case sensitive
                    Book temp = bocker[j];
                    bocker[j] = bocker[j + 1];
                    bocker[j + 1] = temp;
                }
            }
        }
        return bocker;
    }


    /**
     * Statistikfunktion som skall nås via nytt menyval 7 implementeras i CliApp (krav för VG)
     * Metod som hittar medlemmen med flest lån
     * Om man inte hittar några medlemmar returneras null så att programmet inte kraschar
     */
    public Member flestLan() {
        if (antalMedlemmar == 0) {
            return null;
        }
        Member flestLan = medlemmar[0];
        for (int i = 1; i < antalMedlemmar; i++) {
            if (medlemmar[i].getAntalLan() > flestLan.getAntalLan()) {
                flestLan = medlemmar[i];
            }
        }
        return flestLan;
    }

    /**
     * (Krav för VG)
     * Dynamisk kapacitet metod för Böcker array
     */
    private Book[] dynamiskArray(Book[] gammal) {
        Book[] ny = new Book[gammal.length * 2];//Dubblera storleken på gamla arrayen--> ny array
        for (int i = 0; i < gammal.length; i++) { //kopiera till ny array
            ny[i] = gammal[i];
        }
        return ny;
    }

    /**
     * (Krav för VG)
     * Dynamisk kapacitet metod för Medlem array
     */

    private Member[] dynamiskMember(Member[] gammal) {
        Member[] ny = new Member[gammal.length * 2];//Dubblera storleken på gamla arrayen--> ny array
        for (int i = 0; i < gammal.length; i++) { //kopiera till ny array
            ny[i] = gammal[i];
        }
        return ny;
    }
    /**
     * Metod som gör Lån dynamiskt då det krävs för att få programmet att fungera igen när böcker och medlemmar är fixade dynamiskt
     * Uppdaterad: för dynamisk kapacitet Lån array
     */

    private Loan[] dynamiskLan(Loan[] gammal) {
        Loan[] ny = new Loan[gammal.length * 2];//Dubblera storleken på gamla arrayen--> ny array
        for (int i = 0; i < gammal.length; i++) { //kopiera till ny array
            ny[i] = gammal[i];
        }
        return ny;
    }
}
