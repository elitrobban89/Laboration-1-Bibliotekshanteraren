# Laboration 1 — Bibliotekshanteraren (CLI)

Ett enkelt kommandoradsprogram i Java för att hantera ett bibliotek: böcker, medlemmar samt lån och återlämning.

## Status

Datamodellen är komplett — `Book`, `Member` och `Loan` finns. `Library` håller arrayerna och äger reglerna, och menyval 1 och 2 är inkopplade: böcker och medlemmar kan läggas till och ligger kvar under körningen. Utlåningslogiken finns färdig i `Library.lanaBok()` men är ännu inte kopplad till menyval 3.

- [x] Meny med loop och avslut
- [x] Robust felhantering av menyval (ogiltig inmatning, tom ström)
- [x] `Book` som record
- [x] `Member` som klass med regeln `farLana()`
- [x] `Loan` som kopplar ihop bok och medlem
- [x] `Library` med datalagring i arrayer med fast storlek
- [x] Lägg till bok (menyval 1)
- [x] Registrera medlem (menyval 2)
- [ ] Låna bok — logiken klar i `Library`, menyval 3 inte inkopplat
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
├── Member.java   # klass: id, namn, antal aktiva lån
├── Loan.java     # record: kopplar en medlem till en lånad bok
└── Library.java  # lagring i arrayer samt reglerna för utlåning
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

`Member` är en vanlig klass med privata fält, konstruktor, getmetoder och en egen metod som avgör om medlemmen får låna fler böcker:

```java
Member medlem = new Member("M01", "Anna", 3);
medlem.getNamn();   // "Anna"
medlem.farLana();   // true — 3 < MAX_LAN
```

`id` och `namn` är `final`: de sätts i konstruktorn och ändras aldrig. `antalLan` är däremot föränderlig, eftersom antalet aktiva lån växer och krymper när böcker lånas och lämnas tillbaka. Gränsen ligger i konstanten `MAX_LAN` (5), så regeln står på ett enda ställe i koden.

`farLana()` returnerar bara `true`/`false` och skriver inte ut något själv — all in- och utmatning hör hemma i `CliApp`. Därmed går regeln att återanvända oberoende av terminalgränssnittet:

```java
if (medlem.farLana()) {
    // låna ut boken
}
```

#### Varför klass och inte record

`Book` och `Member` skiljer sig åt på den punkt som avgör formvalet: en bok *är* sina värden, medan en medlem har ett tillstånd som ändras över tid.

1. **Medlemmen behåller sin identitet när tillståndet ändras.** När Anna lånar en bok är det fortfarande samma Anna. Som record hade varje utlåning krävt ett helt nytt objekt — `medlemmar[i] = new Member(m.getId(), m.getNamn(), m.getAntalLan() + 1)` — i stället för att räknaren ökas på plats.
2. **Genererad `equals()` hade räknat med lånen.** I en record ingår alla komponenter i `equals()`, så Anna med 0 lån hade inte varit lika med Anna med 1 lån. Som vanlig klass jämförs objekt på referens, och medlemmar slås upp på `getId()`.
3. **Regeln hör ihop med det föränderliga fältet.** `MAX_LAN` och `antalLan` bor i samma klass, så villkoret för utlåning står bredvid det tillstånd det begränsar.

Jämför med `Book`, där inget av detta gäller: en bok byter aldrig titel eller isbn, och två böcker med samma värden *ska* räknas som lika. Därav record i det ena fallet och klass i det andra.

### `Loan`

`Loan` kopplar ihop en medlem och en bok:

```java
public record Loan(Member member, Book book) { }
```

Fälten är objektreferenser och inte id-strängar. Det ger tre fördelar:

1. **Ingen uppslagning.** `lan.book().titel()` och `lan.member().getNamn()` når uppgifterna direkt, utan att leta igenom arrayerna först.
2. **Typsäkerhet.** Med `Loan(String, String)` hade det gått att råka skicka ett isbn där ett medlems-id ska vara — samma typ, inget kompileringsfel. Med `Member` och `Book` är det omöjligt.
3. **En enda sanning om medlemmen.** `lan.member()` är samma objekt som ligger i medlemsarrayen, inte en kopia. Ändras lånräknaren syns det på båda ställena.

Isbn behöver alltså inget eget fält i `Loan` — det nås genom `lan.book().isbn()` och finns därmed bara lagrat på ett ställe.

Ett lån är oföränderligt: det är antingen aktivt eller så finns det inte. Återlämning innebär att posten tas bort ur arrayen i stället för att flaggas om, så `Loan` blir en record precis som `Book`.

### Sammanfattning av formvalen

| Typ | Form | Varför |
| --- | --- | --- |
| `Book` | record | Värdetyp — en bok *är* sina värden och ändras aldrig |
| `Member` | klass | Antalet aktiva lån ändras medan medlemmen behåller sin identitet |
| `Loan` | record | En oföränderlig koppling; tas bort vid återlämning i stället för att ändras |

## Lagring och regler

`Library` äger all data och alla regler. Den skriver aldrig ut något — den svarar, och `CliApp` formulerar svaret för användaren. Samma uppdelning som i `Member.farLana()`, som returnerar `true`/`false` utan att säga något till användaren.

### Arrayer med fast storlek

Varje array har en räknare bredvid sig:

```java
private Book[] boklista = new Book[MAX_BOCKER];
private int antalBocker = 0;
```

Arrayen är full storlek från start, men alla platser innehåller `null`. Räknaren markerar var det riktiga innehållet slutar:

```
index:     0      1      2      3      4     ...    99
         [bok]  [bok]  [bok]  null   null   ...   null
                               ↑
                        antalBocker = 3
```

Därför går alla loopar till `antalBocker`, aldrig till `boklista.length`, och nästa insättning sker på plats `antalBocker`. `for`-each går inte att använda inne i `Library`, eftersom den skulle träffa `null`-platserna.

Metoder som lägger till returnerar `boolean`: `false` när arrayen är full, så att `CliApp` kan skriva ett felmeddelande i stället för att programmet kraschar med `ArrayIndexOutOfBoundsException`.

### Uppslagning

Tre privata metoder med samma form — loopa till räknaren, jämför med `equals()`, returnera träffen eller `null`:

| Metod | Söker i | Jämför |
| --- | --- | --- |
| `hittaBok(isbn)` | `boklista` | `boklista[i].isbn()` |
| `hittaMedlem(id)` | `medlemmar` | `medlemmar[i].getId()` |
| `hittaLan(isbn)` | `loans` | `loans[i].book().isbn()` |

`null` betyder "finns inte" och är ett förväntat svar, inte ett fel.

### Utlåning

`lanaBok(isbn, medlemsId)` gör fyra kontroller innan lånet skapas, var och en med en tidig utgång:

```java
Book bok = hittaBok(isbn);
if (bok == null) return false;              // boken finns inte

if (hittaLan(isbn) != null) return false;   // redan utlånad

Member medlem = hittaMedlem(medlemsId);
if (medlem == null) return false;           // medlemmen finns inte
if (!medlem.farLana()) return false;        // har nått MAX_LAN
```

Att lägga kontrollerna som *guard clauses* i stället för nästlade `if`-satser håller metoden platt och läsbar. Först när alla fyra passerat skapas `Loan`-objektet, räknaren ökas och medlemmens lånantal justeras.

Verifierat genom testkörning: en bok kan lånas ut en gång, ett andra försök på samma bok nekas, okänt isbn och okänt medlems-id nekas, och det sjätte lånet för samma medlem stoppas av `farLana()`.

## Att göra härnäst

- Koppla menyval 3 till `Library.lanaBok()`
- `aterlamna(isbn)` — hitta lånet, minska medlemmens räknare, ta bort posten ur `loans`
- `sokBok(text)` för menyval 5
- Metod som listar böcker med status för menyval 6, byggd på `hittaLan()`