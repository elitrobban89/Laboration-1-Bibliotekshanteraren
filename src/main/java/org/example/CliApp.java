package org.example;

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
            switch (val) {
                case "1": {
                    String titel = IO.readln("Titel: ");
                    String forfattare = IO.readln("Författare: ");
                    String isbn = IO.readln("ISBN: ");

                    if (titel == null || forfattare == null || isbn == null) {
                        IO.println("Felaktig input, programmet avslutas");
                        break;
                    }

                    Book bok = new Book(titel, forfattare, isbn);
                    if (lib.laggTillBok(bok)) {
                        IO.println("Bok " + bok.titel() + " har lagts till");
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
                    Member medlem = new Member(id, namn, 0);
                    if (lib.registreraMedlem(medlem)) {
                        IO.println("Medlem " + namn + " registrerad.");
                    } else {
                        IO.println("Medlemsregistrer är fullt.");
                    }
                    break;
                }

                case "3": {
                    IO.println("Du vill låna en bok");
                    break;
                }
                case "4":
                    IO.println("Lämna tillbaka en bok");
                    break;
                case "5":
                    IO.println("Sök bok (titel eller författare):");
                    break;
                case "6":
                    IO.println("Visa alla böcker och status:");
                    break;
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