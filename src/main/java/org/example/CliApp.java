package org.example;

import java.time.LocalDate;

public class CliApp {
    static void main() {
        Library lib = new Library(); //Skapa ett biblioteksobjekt så vi kan anropa Library
        boolean running = true;
        do {
            printMenu();
            String val = IO.readln("Välj Menyalternativ: ");
            if (val == null) {
                IO.println("Felaktig input, programmet avslutas");
                running = false;
                continue;
            }
            val = val.trim(); //Vi trimmar även val så man inte får in ett blanksteg i slutet av menyalternativet
            switch (val) {
                case "1": {
                    String titel = IO.readln("Titel: ");
                    String forfattare = IO.readln("Författare: ");
                    String isbn = IO.readln("ISBN: ");

                    if (titel == null || forfattare == null || isbn == null) {
                        IO.println("Felaktig input, programmet avslutas");
                        break;
                    }

                    /** Använder trimning
                     * Skriver användaren av vana ett mellanslag efter isbn-numret hittas boken aldrig, och felmeddelandet blir
                     *   "Det finns ingen bok med det ISBN:et" trots att den ligger i arrayen.
                     */
                    titel = titel.trim(); //Trimmar värdena så vi kan
                    forfattare = forfattare.trim();
                    isbn = isbn.trim();

                    if (titel.isBlank() || forfattare.isBlank() || isbn.isBlank()) {
                        IO.println("Tom input, alla fält måste fyllas i.");
                        break;
                    }
                    Book bok = new Book(titel, forfattare, isbn);
                    if (lib.laggTillBok(bok)) {
                        IO.println("Bok " + bok.titel() + " har lagts till");
                    } else if (lib.hittaBok(isbn) != null) {
                        IO.println("Bok med ISBN " + isbn + " finns redan.");
                    } else {
                        IO.println("Biblioteket är fullt.");
                    }
                    break;
                }

                case "2": {
                    String id = IO.readln("Ange Medlems-Id: ");
                    String namn = IO.readln("Ange Namn: ");

                    if (id == null || namn == null) {
                        IO.println("Felaktig input, programmet avslutas");
                        break;
                    }

                    /** Använder trimning
                     * Skriver användaren av vana ett mellanslag efter isbn-numret hittas boken aldrig, och felmeddelandet blir
                     *   "Det finns ingen bok med det ISBN:et" trots att den ligger i arrayen.
                     */
                    id = id.trim(); //Trimmar värdena
                    namn = namn.trim();

                    if (id.isBlank() || namn.isBlank()) {
                        IO.println("Tomt medlemsid eller namn. Fälten måste fyllas i.");
                        break;
                    }
                    Member medlem = new Member(id, namn, 0);
                    if (lib.registreraMedlem(medlem)) {
                        IO.println("Medlem " + namn + " registrerad.");
                    } else if (lib.hittaMedlem(id) != null) {
                        IO.println("Medlem med medlems-id " + id + " finns redan.");
                    } else {
                        IO.println("Medlemsregistret är fullt.");
                    }
                    break;
                }

                case "3": {
                    String isbn = IO.readln("Ange ISBN på boken: ");
                    String medlemsId = IO.readln("Ange medlems-id: ");
                    if (isbn == null || medlemsId == null) {
                        IO.println("Felaktig input, programmet avslutas");
                        break;
                    }
                    /** Använder trimning
                     * Skriver användaren av vana ett mellanslag efter isbn-numret hittas boken aldrig, och felmeddelandet blir
                     *   "Det finns ingen bok med det ISBN:et" trots att den ligger i arrayen.
                     */
                    isbn = isbn.trim(); //Trimmar värdena
                    medlemsId = medlemsId.trim();

                    if (isbn.isBlank() || medlemsId.isBlank()) {
                        IO.println("Tomt värde på ISBN eller medlemsId. Fälten måste fyllas i.");
                        break;
                    }
                    if (lib.lanaBok(isbn, medlemsId)) {
                        IO.println("Boken lånas ut tom datumet: " + LocalDate.now().plusWeeks(3)); //Vi lånar ut boken till medlemmen i 3 veckor från dagens datum
                    } else if (lib.hittaBok(isbn) == null) {
                        IO.println("Det finns ingen bok med det ISBN:et.");
                    } else if (lib.hittaLan(isbn) != null) {
                        IO.println("Boken är redan utlånad.");

                    } else if (lib.hittaMedlem(medlemsId) == null) {
                        IO.println("Det finns ingen medlem med det id:t.");

                    } else {
                        IO.println("Medlemmen har redan max antal lån.");
                    }
                    break;
                }
                case "4": {
                    String isbn = IO.readln("Ange ISBN på boken som lämnas tillbaka: ");

                    if (isbn == null) {
                        IO.println("Avbruten inmatning av ISBN");
                        break;
                    }
                    isbn = isbn.trim();
                    if (isbn.isBlank()) {
                        IO.println("Tomt ISBN. Fältet måste fyllas i.");
                        break;
                    }
                    if (lib.aterlamnaBok(isbn)) {
                        IO.println("Boken är återlämnad.");
                    } else if (lib.hittaBok(isbn) == null) {
                        IO.println("Det finns ingen bok med det ISBN:et.");
                    } else {
                        IO.println("Den boken är inte utlånad.");
                    }
                    break;
                }
                case "5": {
                    String sokord = IO.readln("Sök bok (ange titel eller författare): ");
                    if (sokord == null) {
                        IO.println("Avbruten inmatning av titel eller författare");
                        break;
                    }
                    sokord = sokord.trim();
                    if (sokord.isBlank()) {
                        IO.println("Tom inmatning av titel eller författare. Fältet måste fyllas i.");
                        break;
                    }

                    /**Logik för att söka efter bok baserat på titel eller författare anropa sökBok() med sökord*/
                    Book[] traffar = lib.sokBok(sokord);
                    if (traffar.length == 0) {
                        IO.println("Inga böcker hittades med sökordet: " + sokord);
                        break;
                    }
                    /**Skriver ut sökresultat*/
                    for (Book bok : traffar) {
                        IO.println(bok.titel() + " - " + bok.forfattare() + " - " + bok.isbn());
                    }
                    break;
                }
                case "6": {
                    Book[] bocker = lib.sorteraPaTitel(lib.getAllaBocker()); //Kör metoden inuti Library. Skapar kopia av boklistan. Bubble sort metod/algoritm anropas

                    if (bocker.length == 0) {
                        IO.println("Inga böcker i biblioteket.");
                        break;
                    }
                    for (Book bok : bocker) { //Skriver ut boklistan
                        Loan lan = lib.hittaLan(bok.isbn());

                        String status;
                        if (lan == null) {
                            status = "Tillgänglig";
                        } else {
                            status = "Utlånad till " + lan.member().getNamn();
                        }
                        IO.println(bok.titel() + " - " + bok.forfattare() + " - " + bok.isbn() + " - " + status);
                    }
                    break;
                }
                case "e":
                    IO.println("Avslutar programmet");
                    running = false; //Avsluta programmet
                    break;
                default:
                    IO.println("Ogiltigt val: '" + val + "'. Välj 1-6 eller e.");
            }
        } while (running);
    }

    public static void printMenu() {
        String menyText = """
                       Bibliotekshanteraren
                       ====================
                       1. Lägg till bok
                       2. Registrera medlem
                       3. Låna bok
                       4. Lämna tillbaka bok
                       5. Sök bok (titel eller författare)
                       6. Visa alla böcker och status
                       e. Avsluta
                """;
        IO.println(menyText);
    }
}