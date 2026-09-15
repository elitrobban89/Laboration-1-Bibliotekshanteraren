# Laboration 1 — Bibliotekshanteraren (CLI)

Ett enkelt kommandoradsprogram i Java för att hantera ett bibliotek: böcker, medlemmar samt lån och återlämning.

## Status

Menysystemet är klart, inklusive felhantering. Datamodellen är påbörjad — `Book` och `Member` finns, `Loan` återstår. Menyvalen skriver just nu ut vad som valts; funktionaliteten bakom varje val implementeras steg för steg.

- [x] Meny med loop och avslut
- [x] Robust felhantering av menyval (ogiltig inmatning, tom ström)
- [x] `Book` som record
- [x] `Member` med regeln `farLanaFler()`
- [ ] `Loan` som kopplar ihop bok och medlem
- [ ] Datalagring i arrayer med fast storlek
- [ ] Lägg till bok
- [ ] Registrera medlem
- [ ] Låna bok
- [ ] Lämna tillbaka bok
- [ ] Sök bok (titel eller författare)
- [ ] Visa alla böcker och status

## Krav

- JDK 26 (projektet kompileras mot source/target 26 och använder `java.lang.IO` samt `main` utan parametrar)
- Maven

## Bygga och köra

```bash
# Bygg
mvn clean package

# Kör
java -cp target/classes org.example.CliApp
```

I IntelliJ IDEA går det också att köra `CliApp` direkt via play-knappen.

## Användning

Vid start visas menyn:

```
Bibliotekshanteraren
====================
1. Lägg till bok
2. Registrera medlem
3. Låna bok
4. Lämna tillbaka bok
5. Sök bok (titel eller författare)
6. Visa alla böcker och status
e. Avsluta
```

Skriv siffran för önskat alternativ och tryck Enter. Skriv `e` för att avsluta programmet.

### Felhantering

Menyn tar emot all inmatning som text och tolkar den aldrig som ett tal, så bokstäver där siffror förväntas kan inte krascha programmet.

- Ett val som inte finns i menyn (t.ex. `ghg` eller `9`) ger meddelandet `Ogiltigt val: '...'. Välj 1-6 eller e.` och menyn visas på nytt.
- Om inströmmen tar slut (Ctrl+Z i Windows, Ctrl+D i Linux/macOS, eller pipad indata) avslutas programmet kontrollerat i stället för att kasta `NullPointerException`.

## Projektstruktur

```
src/main/java/org/example/
├── CliApp.java   # meny, inläsning och programloop
├── Book.java     # record: titel, författare, isbn
└── Member.java   # record: id, namn, antal aktiva lån
```

## Datamodell

### `Book`

`Book` är en record och därmed oföränderlig — inga set-metoder. Konstruktor, accessorer (`titel()`, `forfattare()`, `isbn()`), `equals()`, `hashCode()` och `toString()` genereras automatiskt.

```java
Book bok = new Book("Sagan om ringen", "Tolkien", "91-1-234567-8");
bok.titel();   // "Sagan om ringen"
```

Utlåningsstatus lagras inte i boken, utan kommer att hanteras separat så att det går att se *vem* som lånat ett exemplar.

#### Varför en record passar här

En bok är en **värdetyp** — den definieras helt av sina värden och har inget föränderligt tillstånd. Fyra skäl gör record till rätt form:

1. **Oföränderligheten garanteras av språket.** Fälten blir `private final` och klassen implicit `final`, så ingen kan ärva `Book` och lägga till muterbart tillstånd. Garantin kommer från kompilatorn i stället för från disciplin hos den som skriver koden.
2. **Värdelikhet gratis.** `equals()` och `hashCode()` jämför fältinnehåll, så två böcker med samma titel, författare och isbn räknas som lika. Det är precis vad som behövs vid sökning bland böckerna.
3. **Mindre boilerplate.** Konstruktor, accessorer och `toString()` genereras. Klassen beskriver *vad en bok är* i stället för att fyllas av getters.
4. **Säker att dela.** Eftersom inget kan ändras kan samma `Book`-objekt ligga i flera strukturer utan risk för att någon muterar den bakom ryggen på övriga.

### `Member`

`Member` har `id`, `namn` och `antalLan` samt en egen metod som avgör om medlemmen får låna fler böcker:

```java
Member medlem = new Member("M01", "Anna", 3);
medlem.farLanaFler();   // true — 3 < MAX_LAN
```

Gränsen ligger i konstanten `MAX_LAN` (5), så regeln står på ett enda ställe i koden. Metoden returnerar bara `true`/`false` och skriver inte ut något själv — all in- och utmatning hör hemma i `CliApp`. Därmed går regeln att återanvända och testa oberoende av terminalgränssnittet.

#### Record eller vanlig klass?

En record *är* en klass: efter kompilering är `Member` en `public final class Member extends java.lang.Record`, med privata fält, konstruktor, accessorer och den egna metoden. Skillnaden märks först när `antalLan` ska ändras. I en record byggs då ett nytt objekt:

```java
medlemmar[i] = new Member(m.id(), m.namn(), m.antalLan() + 1);
```

En vanlig klass hade i stället kunnat öka räknaren på plats med `m.lanaBok()`. Ett ytterligare påpekande: eftersom `antalLan` ingår i den genererade `equals()` är samma medlem med olika antal lån *inte* lika enligt Java, så medlemmar slås alltid upp på `id` och aldrig med `equals()`. Visar sig lånehanteringen bli tydligare med muterbart tillstånd görs `Member` om till en vanlig klass med `lanaBok()` och `aterlamnaBok()`.