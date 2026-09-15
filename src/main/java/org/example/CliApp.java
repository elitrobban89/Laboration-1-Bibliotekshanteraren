package org.example;

public class CliApp {
    static void main() {
        boolean running = true;
        do {
            printMenu();
            String val = IO.readln("Välj Menyalternativ: ");
            switch (val) {
                case "1":
                    IO.println("Du har valt Lägg till bok");
                    break;
                case "2":
                    IO.println("Du har valt Registrera medlem");
                    break;
                case "3":
                    IO.println("Du vill låna en bok");
                    break;
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