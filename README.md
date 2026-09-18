# Laboration 1 — Bibliotekshanteraren (CLI)

Ett enkelt kommandoradsprogram i Java för att hantera ett bibliotek: böcker, medlemmar samt lån och återlämning.

## Status

Programmet är funktionellt komplett för både godkänt och väl godkänt. `Book`, `Member` och `Loan` utgör datamodellen, `Library` håller arrayerna och äger reglerna, och `CliApp` sköter meny, inläsning och utskrift. Samtliga sju menyval är inkopplade: böcker och medlemmar kan läggas till, böcker lånas ut och lämnas tillbaka, beståndet listas med aktuell utlåningsstatus, och sökningen hittar böcker på del av titel eller författare utan hänsyn till versaler. Vid utlåning visas ett återlämningsdatum tre veckor fram. Listningen i menyval 6 sorteras i bokstavsordning på titel med en egen bubble sort, menyval 7 visar vilken medlem som har flest aktiva lån, och alla tre arrayerna växer automatiskt när de blir fulla. Samtliga VG-krav är uppfyllda.

- [x] Meny med loop och avslut
- [x] Robust felhantering av menyval (ogiltig inmatning, tom ström)
- [x] Validering av inmatade fält: tomma värden, blanksteg och dubbletter
- [x] `Book` som record
- [x] `Member` som klass med regeln `farLana()`
- [x] `Loan` som kopplar ihop bok och medlem
- [x] `Library` med datalagring i arrayer, ingen `ArrayList`
- [x] Lägg till bok (menyval 1)
- [x] Registrera medlem (menyval 2)
- [x] Låna bok (menyval 3), med specifika felmeddelanden
- [x] Lämna tillbaka bok (menyval 4)
- [x] Sök bok på del av titel eller författare (menyval 5), skiftlägesokänsligt via egen sökloop
- [x] Visa alla böcker med status (menyval 6), sorterat i bokstavsordning på titel

För väl godkänt:

- [x] Egen sorteringsalgoritm (bubble sort) på titel för menyval 6
- [x] Statistik: medlem med flest aktiva lån (menyval 7)
- [x] Dynamisk kapacitet: arrayer som växer när de blir fulla
- [x] Reflektion i README om Collections Framework

## Krav

- JDK 27 (`pom.xml` sätter `maven.compiler.release` till 27; projektet använder `java.lang.IO` samt `main` utan parametrar)
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
7. Statistik - Medlemmen med flest lån
e. Avsluta
```

Skriv siffran för önskat alternativ och tryck Enter. Skriv `e` för att avsluta programmet.

### Felhantering

Menyn tar emot all inmatning som text och tolkar den aldrig som ett tal, så bokstäver där siffror förväntas kan inte krascha programmet.

- Ett val som inte finns i menyn (t.ex. `ghg` eller `9`) ger meddelandet `Ogiltigt val: '...'. Välj 1-6 eller e.` och menyn visas på nytt.
- Om inströmmen tar slut (Ctrl+Z i Windows, Ctrl+D i Linux/macOS, eller pipad indata) avslutas programmet kontrollerat i stället för att kasta `NullPointerException`.

#### Inläsning med `java.lang.IO` i stället för `Scanner`

All inläsning sker med `IO.readln()` från `java.lang.IO`, inte med `Scanner`. Valet är medvetet och gjort av robusthetsskäl:

- `IO.readln()` returnerar `null` när inströmmen tar slut. `Scanner.nextLine()` kastar i stället `NoSuchElementException`, som måste fångas eller föregås av `hasNextLine()` för att programmet inte ska krascha.
- Varje `null` hanteras redan i menyn och i samtliga inmatningsblock, så samma situation som hade krävt undantagshantering med `Scanner` täcks här av en vanlig `if`-sats.

Funktionellt är de likvärdiga för den här uppgiften — båda läser en rad text i taget från standard in, och inget värde tolkas som tal i något av fallen.

#### Validering av inmatade fält

Menyval 1, 2 och 3 läser fritext från användaren. Varje block kör samma kedja innan värdena används:

```java
if (titel == null || forfattare == null || isbn == null) { ... break; }   // tom ström

titel = titel.trim();                                                     // putsa kanterna
forfattare = forfattare.trim();
isbn = isbn.trim();

if (titel.isBlank() || forfattare.isBlank() || isbn.isBlank()) { ... break; }   // tomt fält
```

Ordningen är tvingande. `null`-kontrollen måste komma först, eftersom `null.trim()` skulle kasta just det undantag kontrollen finns för att undvika. Trimningen måste komma före `isBlank()`, så att ett fält med bara blanksteg blir tomt och fångas.

Trimningen gör också att uppslagningar fungerar som användaren förväntar sig: `hittaBok()` jämför med `equals()`, där blanksteg räknas som tecken, så `" 91-1 "` hade annars aldrig matchat en bok lagrad som `"91-1"`.

Även själva menyvalet trimmas innan det matchas i `switch`-satsen, så att `" 6 "` väljer statuslistan i stället för att avvisas som ogiltigt val. Någon `isBlank()`-kontroll behövs inte där — en tom sträng matchar inget `case` och hamnar i `default`, vilket är rätt beteende.

#### Dubbletter

`laggTillBok()` och `registreraMedlem()` avvisar värden som redan finns:

```java
if (hittaBok(bok.isbn()) != null) return false;
if (hittaMedlem(medlem.getId()) != null) return false;
```

Utan dem hade en andra bok med samma isbn hamnat i arrayen men aldrig gått att nå, eftersom `hittaBok()` alltid returnerar första träffen. Regeln ligger i `Library` och inte i `CliApp`, så den gäller oavsett vem som anropar metoden.

Sedan arrayerna växer dynamiskt är dubbletten det enda skälet kvar till att dessa två metoder returnerar `false`, så `CliApp` kan skriva dubblettmeddelandet direkt i en `else`-gren utan att först ställa en följdfråga. Vid utlåning, där `lanaBok()` kan misslyckas av fyra skäl, behövs följdfrågorna fortfarande.

#### Sammanställning

| Felaktig inmatning | Vad som händer |
| --- | --- |
| Bokstäver där siffror väntas | Inget parsas som tal; `default` fångar ogiltiga menyval |
| Tom ström (Ctrl+Z / Ctrl+D) | `null`-kontroll avbryter menyvalet |
| Tomt fält eller bara blanksteg | `isBlank()` avvisar med felmeddelande |
| Blanksteg runt värden | `trim()` putsar innan värdet lagras eller söks upp |
| Befintligt isbn eller medlems-id | Avvisas av dubblettvakten i `Library` |
| Okänd bok, okänd medlem, utlånad bok, nått lånetak | Egna felmeddelanden via `else if`-kedjan |
| Tomt sökord i menyval 5 | Avvisas av `isBlank()`; annars hade `contains("")` matchat varje bok |

## Projektstruktur

```
src/main/java/org/example/
├── CliApp.java   # meny, inläsning och programloop
├── Book.java     # record: titel, författare, isbn
├── Member.java   # klass: id, namn, antal aktiva lån
├── Loan.java     # record: kopplar en medlem till en lånad bok
└── Library.java  # lagring i arrayer samt reglerna för utlåning och sökning
```

## Datamodell

### `Book`

`Book` är en record och därmed oföränderlig — inga set-metoder. Konstruktor, accessorer (`titel()`, `forfattare()`, `isbn()`), `equals()`, `hashCode()` och `toString()` genereras automatiskt.

```java
Book bok = new Book("Sagan om ringen", "Tolkien", "91-1-234567-8");
bok.titel();   // "Sagan om ringen"
```

Utlåningsstatus lagras inte i boken, utan hålls separat i `loans`-arrayen, så att det går att se *vem* som lånat ett exemplar.

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

### Tre arrayer med räknare

`Library` håller tre arrayer. De skapas små och växer vid behov — se "Dynamisk kapacitet" längre ner:

```java
private Book[] boklista = new Book[START_BOCKER];         // alla böcker
private Member[] medlemmar = new Member[START_MEDLEMMAR];  // alla medlemmar
private Loan[] loans = new Loan[START_BOCKER];             // aktiva lån
```

`loans` är strukturen som håller reda på **vilka böcker som är utlånade och till vem**. Varje `Loan` pekar på både boken och medlemmen, så `hittaLan(isbn)` svarar på om en bok är utlånad, och `lan.member()` på till vem. En bok som inte förekommer i `loans` är tillgänglig — utlåningsstatus lagras alltså aldrig i `Book` själv.

Lånearrayen startar lika stor som boklistan, eftersom en bok bara kan vara utlånad till en person åt gången och fler aktiva lån än böcker därför aldrig kan uppstå. Den måste dock växa i takt med boklistan — se "Dynamisk kapacitet".

Varje array har en räknare bredvid sig:

```java
private Book[] boklista = new Book[START_BOCKER];
private int antalBocker = 0;
```

Arrayen har sin fulla längd från början, men alla platser innehåller `null`. Räknaren markerar var det riktiga innehållet slutar:

```
index:     0      1      2      3
         [bok]  [bok]  [bok]  null
                               ↑
                        antalBocker = 3
```

Därför går alla loopar till `antalBocker`, aldrig till `boklista.length`, och nästa insättning sker på plats `antalBocker`. `for`-each går inte att använda inne i `Library`, eftersom den skulle träffa `null`-platserna.

Metoder som lägger till returnerar `boolean`: `false` när arrayen är full, så att `CliApp` kan skriva ett felmeddelande i stället för att programmet kraschar med `ArrayIndexOutOfBoundsException`.

### Uppslagning

Tre uppslagningsmetoder med samma form — loopa till räknaren, jämför med `equals()`, returnera träffen eller `null`:

| Metod | Söker i | Jämför |
| --- | --- | --- |
| `hittaBok(isbn)` | `boklista` | `boklista[i].isbn()` |
| `hittaMedlem(id)` | `medlemmar` | `medlemmar[i].getId()` |
| `hittaLan(isbn)` | `loans` | `loans[i].book().isbn()` |

`null` betyder "finns inte" och är ett förväntat svar, inte ett fel.

Metoderna är `public` och inte `private`, av skäl som beskrivs under "Felmeddelanden vid utlåning". Sökningen i menyval 5 följer *inte* den här formen — `sokBok()` jämför med `contains()` i stället för `equals()` och returnerar flera träffar i stället för den första, vilket beskrivs under "Sökning".

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

### Felmeddelanden vid utlåning

`lanaBok()` returnerar ett enda `boolean` trots att den kan misslyckas av fyra skäl. För att ändå kunna ge användaren ett begripligt besked ställer `CliApp` diagnosfrågor — men först **efter** ett misslyckat försök, aldrig före:

```java
if (lib.lanaBok(isbn, medlemsId)) {
    // lyckades
} else if (lib.hittaBok(isbn) == null) {
    IO.println("Det finns ingen bok med det ISBN:et.");
} else if (lib.hittaLan(isbn) != null) {
    IO.println("Boken är redan utlånad.");
} else if (lib.hittaMedlem(medlemsId) == null) {
    IO.println("Det finns ingen medlem med det id:t.");
} else {
    IO.println("Medlemmen har redan max antal lån.");
}
```

Ordningen på grenarna följer kontrollordningen i `lanaBok()`, så att rätt orsak rapporteras när flera saker är fel samtidigt. Att fråga efteråt i stället för före innebär också att den lyckade vägen inte belastas med tre extra genomsökningar av arrayerna.

Priset är att `hittaBok()`, `hittaMedlem()` och `hittaLan()` fick ändras från `private` till `public`. De var tänkta som interna hjälpmetoder, och som publika blir de en del av `Library`s gränssnitt som andra klasser kan bli beroende av. Avvägningen gjordes medvetet: begripliga felmeddelanden bedömdes väga tyngre än en helt sluten klass, och `lanaBok()` behåller fortfarande sista ordet om huruvida ett lån får ske.

Alla fem utfallen är verifierade genom körning: lyckad utlåning, redan utlånad bok, okänt isbn, okänt medlems-id och nått lånetak.

### Återlämning

`aterlamnaBok(isbn)` är motsatsen till utlåning, och den enda metoden som tar bort något ur en array.

Till skillnad från de andra uppslagningarna kan den inte använda `hittaLan()`. Den metoden returnerar lånet men kastar bort **var** i arrayen det låg, och för att ta bort en post krävs just indexet. Därför loopar `aterlamnaBok()` själv:

```java
for (int i = 0; i < antalAktivaLan; i++) {
    if (loans[i].book().isbn().equals(isbn)) {
        Member medlem = loans[i].member();
        medlem.setAntalLan(medlem.getAntalLan() - 1);

        loans[i] = loans[antalAktivaLan - 1];   // flytta sista posten till luckan
        loans[antalAktivaLan - 1] = null;       // töm sista platsen
        antalAktivaLan--;                       // krymp det använda området
        return true;
    }
}
return false;
```

En array kan inte krympa, så borttagning betyder att skriva något annat på platsen. Den sista aktiva posten flyttas in i luckan och räknaren minskas med ett:

```
före:    [lån0] [lån1] [lån2] [lån3]  null ...     antalAktivaLan = 4
                  ↑ ska bort
efter:   [lån0] [lån3] [lån2]  null   null ...     antalAktivaLan = 3
```

Ordningen bland lånen kastas om, vilket är harmlöst eftersom lån aldrig slås upp på position utan alltid på isbn. Alternativet — att skifta alla efterföljande poster ett steg åt vänster — bevarar ordningen men kostar en extra loop utan att ge något.

Att `medlem` hämtas ur lånet och inte slås upp separat är samma poäng som tidigare: `loans[i].member()` **är** objektet i medlemsarrayen, så lånräknaren minskas på rätt person utan uppslagning.

Verifierat genom körning: att återlämna det första av tre aktiva lån tar bort rätt post, lämnar de två andra sökbara trots omflyttningen, minskar medlemmens räknare, och gör boken utlåningsbar igen. Återlämning av en bok som inte är utlånad, och av ett okänt isbn, ger båda `false` med skilda felmeddelanden i menyn.

### Statuslistan

Menyval 6 ska visa alla böcker med status — tillgänglig eller utlånad, och i så fall till vem. Det kräver att `Library` lämnar ut hela beståndet, vilket väcker frågan hur en klass delar med sig av sin data utan att ge bort kontrollen över den.

`getAllaBocker()` returnerar därför en **kopia i exakt rätt storlek**, inte den interna arrayen:

```java
public Book[] getAllaBocker() {
    Book[] kopia = new Book[antalBocker];
    for (int i = 0; i < antalBocker; i++) {
        kopia[i] = boklista[i];
    }
    return kopia;
}
```

Två skäl till att inte returnera `boklista` rakt av:

1. **Den interna arrayen är mestadels tom.** Med tre böcker i en array som rymmer fyra följer en `null`-plats med, och en `for`-each över den kraschar direkt. Kopian är `antalBocker` lång och innehåller inga `null`, vilket gör att anroparen kan använda `for`-each och `.length` utan att känna till räknarmönstret.
2. **Inkapslingen skulle gå förlorad.** Den som får referensen till `boklista` kan skriva `bocker[0] = null` och ändra bibliotekets innehåll utifrån, förbi alla kontroller. Ändringar i kopian påverkar ingenting.

Kopieringen sker med en egen loop i stället för `java.util.Arrays.copyOf()`, i linje med uppgiftens inriktning på manuell arrayhantering.

Statusen lagras inte någonstans utan läses ur `loans` vid varje visning:

```java
Loan lan = lib.hittaLan(bok.isbn());
String status = (lan == null) ? "Tillgänglig" : "Utlånad till " + lan.member().getNamn();
```

Eftersom `hittaLan()` svarar på både om boken är utlånad och till vem, täcks kravets båda halvor av ett enda anrop. Att statusen härleds i stället för att lagras gör att listan aldrig kan hamna i otakt med verkligheten — verifierat genom körning: samma bok visas som utlånad till Anna före återlämning och som tillgänglig direkt efter, utan att någon status uppdaterats explicit.

Ett tomt bibliotek fångas före loopen och ger beskedet "Inga böcker i biblioteket." i stället för tystnad.

### Sortering i bokstavsordning

VG-kravet säger att böckerna i menyval 6 ska visas i bokstavsordning på titel, och att sorteringen ska ske med en egen algoritm — inte `Arrays.sort()` eller `Collections.sort()`. Valet föll på bubble sort:

```java
public Book[] sorteraPaTitel(Book[] bocker) {
    for (int i = 0; i < bocker.length - 1; i++) {
        for (int j = 0; j < bocker.length - 1; j++) {
            if (bocker[j].titel().compareToIgnoreCase(bocker[j + 1].titel()) > 0) {
                Book temp = bocker[j];
                bocker[j] = bocker[j + 1];
                bocker[j + 1] = temp;
            }
        }
    }
    return bocker;
}
```

**Parameter i stället för fältet.** Metoden tar emot en `Book[]` och sorterar den, i stället för att gå direkt på `boklista`. Utan parameter hade bibliotekets egen lagring byggts om som en bieffekt av att *visa* listan, och inmatningsordningen hade försvunnit permanent. Nu sorteras den kopia som `getAllaBocker()` redan lämnar ut, vilket gör att `boklista` är orörd efteråt — verifierat genom att läsa ut beståndet en andra gång efter sorteringen. Att metoden är oberoende av fältet betyder också att den kan återanvändas på träffarna från `sokBok()` utan en rad ny kod.

Arrayer är objekt, så parametern bär en referens och inte en kopia av innehållet. Metoden skyddar alltså ingenting i sig själv — det är kopieringen i `getAllaBocker()` som gör upplägget säkert. `return bocker;` returnerar samma referens som kom in, vilket gör anropet i menyval 6 till en enda rad.

**Hur bubble sort arbetar.** Innerloopen jämför två grannar i taget och byter plats på dem när de står i fel ordning. Efter ett helt varv har den största titeln bubblat hela vägen till sista platsen. Ytterloopen upprepar tills allt ligger rätt:

```
Varv 1:  [Sagan, Astrid, Pippi]   jämför Sagan/Astrid  → byt
         [Astrid, Sagan, Pippi]   jämför Sagan/Pippi   → byt
         [Astrid, Pippi, Sagan]   ← Sagan ligger nu rätt, sist

Varv 2:  [Astrid, Pippi, Sagan]   jämför Astrid/Pippi  → rätt, gör inget
```

Bytet kräver en temp-variabel. Utan den skrivs den ena boken över av den andra innan den hunnit sparas undan, och en bok försvinner ur arrayen medan en annan dubbleras.

Ytterloopen går till `length - 1` eftersom `n - 1` varv räcker — efter så många är varje element garanterat på plats. Innerloopen går till `length - 1` av ett helt annat skäl: den läser `bocker[j + 1]`, och utan avdraget pekar det sista steget utanför arrayen med en `ArrayIndexOutOfBoundsException` som följd.

**Varför `compareToIgnoreCase` och inte `compareTo`.** `compareTo` jämför tecknens Unicode-värden, och där ligger samtliga versaler före samtliga gemener — `'Z'` är 90 medan `'b'` är 98. En bok med liten begynnelsebokstav hamnade därför efter alla böcker med stor:

```
Apelsin
Citron
Zebra
banan     ← sorterat efter Z trots att b kommer före z
```

`compareToIgnoreCase` löser det utan att `toLowerCase()` behöver anropas på båda sidor, vilket är samma problem som `sokBok()` löser på det andra sättet.

**Känd begränsning: sorteringen är inte svensk kollationering.** Jämförelsen bygger fortfarande på Unicode-värden, och där ligger å, ä och ö efter z — vilket råkar stämma med svensk bokstavsordning. Inbördes gör de det inte: Unicode ger `ä` (0x00E4) före `å` (0x00E5), medan svensk ordning är å, ä, ö. En korrekt lösning hade krävt `java.text.Collator` med svensk `Locale`. Det är medvetet utelämnat, eftersom uppgiften handlar om att skriva algoritmen själv och inte om språkriktig kollationering.

**Komplexitet.** Bubble sort gör i storleksordning `n²` jämförelser. `Arrays.sort()` använder en betydligt effektivare algoritm i `n log n`, vilket för hundra böcker är skillnaden mellan runt 10 000 och runt 700 jämförelser. För ett bibliotek av den här storleken är det utan praktisk betydelse. Innerloopen hade dessutom kunnat krympa med ett steg per avklarat varv, eftersom slutet av arrayen redan är färdigsorterat — `j < bocker.length - 1 - i`. Det halverar ungefär antalet jämförelser men ändrar inte storleksordningen, och är utelämnat till förmån för en algoritm som är lättare att läsa.

**Verifiering.** Sorteringsmetoden kördes mot sju fall: tom array och en enda bok passerar utan att looparna kör ett enda varv, redan sorterad och omvänd ordning ger båda samma resultat, dubbletter behålls, och blandat skiftläge sorteras rätt. Därefter kördes hela programmet med böckerna inmatade i ordningen Sagan om ringen, astrid bok, Pippi, Äpple, med Pippi utlånad till Anna:

```
astrid bok - Lindgren - 222 - Tillgänglig
Pippi - Lindgren - 333 - Utlånad till Anna
Sagan om ringen - Tolkien - 111 - Tillgänglig
Äpple - Okand - 444 - Tillgänglig
```

Listan är sorterad, `astrid bok` ligger först trots liten begynnelsebokstav, `Äpple` sist, och lånestatusen följer med rätt bok — statusen slås upp på isbn vid utskriften och påverkas därför inte av att ordningen ändrats.

### Sökning

Menyval 5 ska hitta böcker på **del av** titeln eller författaren, skiftlägesokänsligt. Det låter som en uppgift för `hittaBok()`, men den metoden duger inte, och skillnaderna är just det som formar `sokBok()`:

| | `hittaBok()` | `sokBok()` |
| --- | --- | --- |
| Söker i | `isbn()` | `titel()` **eller** `forfattare()` |
| Jämför med | `equals()` — hela strängen exakt | `contains()` på gemener — del av strängen |
| Antal svar | första träffen, sedan `return` | alla träffar, loopen går klart |
| Returnerar | `Book` eller `null` | `Book[]`, tom array vid noll träffar |

Den tredje raden är den avgörande. `hittaBok()` får avbryta vid första träffen eftersom isbn är unikt — dubblettvakten i `laggTillBok()` garanterar det. Ett sökord som `astrid` kan däremot matcha flera böcker, så sökloopen måste gå hela vägen till `antalBocker`.

#### Ett sökord, två fält

`sokBok()` tar **en** parameter, inte en för titel och en för författare:

```java
if (boklista[i].titel().toLowerCase().contains(sokord.toLowerCase())
        || boklista[i].forfattare().toLowerCase().contains(sokord.toLowerCase())) {
```

Kravets "titel eller författare" handlar om vilka fält som genomsöks, inte om hur många frågor användaren ska svara på. `||` gör att det räcker att ett av fälten matchar, så `pippi` ger träff via titeln och `astrid` via författaren — utan att användaren behöver tala om vilketdera hen skrev.

Två parametrar hade dessutom öppnat en fälla: den som bara vill söka på författare lämnar titelfältet tomt, och `"".contains("")` är sant för varje sträng. Alla böcker i biblioteket hade blivit träffar. Med ett enda sökord räcker den vanliga `isBlank()`-kontrollen i `CliApp` för att stänga den vägen.

`toLowerCase()` måste stå på **båda** sidor av `contains()`. Med gemener bara på bokens fält matchar `"astrid lindgren".contains("Astrid")` inte — och versal begynnelsebokstav är precis vad man skriver när man söker på ett namn.

#### Okänt antal träffar i en array med fast storlek

Utan `ArrayList` går det inte att skapa returarrayen förrän antalet träffar är känt. `sokBok()` löser det med en temp-array i värsta-fallsstorlek och en egen räknare, och kopierar sedan ner till exakt rätt längd:

```java
public Book[] sokBok(String sokord) {
    Book[] traffar = new Book[antalBocker];   // värsta fallet: alla böcker matchar
    int antalTraffar = 0;

    for (int i = 0; i < antalBocker; i++) {
        if ( /* matchar titel eller författare */ ) {
            traffar[antalTraffar] = boklista[i];
            antalTraffar++;
        }
    }

    Book[] kopia = new Book[antalTraffar];    // exakt rätt storlek
    for (int i = 0; i < antalTraffar; i++) {
        kopia[i] = traffar[i];
    }
    return kopia;
}
```

Att returnera `traffar` direkt hade varit fel: med tio böcker i biblioteket och två träffar blir arrayen tio lång, med åtta `null`-platser på slutet. En `for`-each i `CliApp` hade kraschat med `NullPointerException` på den första, och `.length` hade sagt tio i stället för två. Nedkopieringen är samma mönster som i `getAllaBocker()` — anroparen får en array vars längd *är* antalet böcker i den.

Noll träffar ger därför en tom array, inte `null`. `CliApp` kan då kolla `traffar.length == 0` för "inga träffar", precis som menyval 6 gör för ett tomt bibliotek, och slipper ännu en `null`-kontroll.

#### Menyvalet

`case "5"` kör samma valideringskedja som övriga inmatningsblock — `null` → `trim()` → `isBlank()` — och anropar sedan metoden:

```java
Book[] traffar = lib.sokBok(sokord);
if (traffar.length == 0) {
    IO.println("Inga böcker hittades med sökordet: " + sokord);
    break;
}
for (Book bok : traffar) {
    IO.println(bok.titel() + " - " + bok.forfattare() + " - " + bok.isbn());
}
break;
```

`isBlank()`-kontrollen bär extra tyngd just här. Ett tomt sökord hade gjort varje `contains("")` sant, och sökningen hade svarat med hela biblioteket i stället för ett felmeddelande.

`for`-each går att använda utan risk, eftersom `sokBok()` redan kopierat ner arrayen till exakt antalet träffar — samma skäl som gör `getAllaBocker()` säker att loopa över i menyval 6.

Grenens avslutande `break` är nödvändig och inte bara god sed: utan den faller `case "5"` igenom till `case "6"` i `switch`-satsen, och användaren hade fått sina träffar följda av hela boklistan.

Verifierat genom körning mot ett bibliotek med tre böcker: `pippi` i gemener hittar *Pippi Långstrump*, `ASTRID` i versaler hittar båda Lindgren-böckerna, `lejon` matchar mitt inne i ett ord, `zzz` ger beskedet om noll träffar, ett sökord med bara blanksteg avvisas av `isBlank()`, och `   ring   ` trimmas innan sökningen och hittar *Sagan om ringen*.
### Statistik: medlemmen med flest aktiva lån

VG-kravet säger att programmet ska hitta **och visa** den medlem som för tillfället har flest aktiva lån, utan Streams och utan Collections. Att funktionen ska *visa* något innebär att den måste vara åtkomlig för användaren, och i ett program vars enda gränssnitt är menyn betyder det ett nytt menyval. Statistiken ligger därför på alternativ 7.

Formuleringen "hittar och visar" delar sig i två ansvar, som hamnar på var sitt ställe enligt samma princip som resten av projektet — `Library` svarar, `CliApp` pratar:

```java
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
```

**Ingen parameter.** Till skillnad från `sorteraPaTitel()`, som tar emot den array den ska arbeta på, går den här metoden direkt på fältet `medlemmar`. Det är bibliotekets egna medlemmar som avses, och det finns ingen utomstående array som vore meningsfull att skicka in.

**Räknaren fanns redan.** Ingen ny datastruktur behövdes. `Member.antalLan` hålls uppdaterad av `lanaBok()` och `aterlamnaBok()`, så statistiken kan läsas ur befintlig data i stället för att bokföras separat. Samma princip som statuslistan i menyval 6: härled i stället för att lagra, så kan uppgiften aldrig hamna i otakt med verkligheten.

**Loopen börjar på index 1.** Plats 0 utses till mästare innan loopen startar, vilket gör att den första jämförelsen inte behöver göras mot medlemmen själv.

**Kontrollen av tomt register är avsiktlig, inte kosmetisk.** Utan den hade `medlemmar[0]` lästs innan det var känt att någon fanns där. Med tomt register hade variabeln blivit `null`, och metoden hade råkat returnera rätt svar enbart därför att loopen aldrig kör ett varv — hade den kört ett enda skulle `flestLan.getAntalLan()` ha kastat en `NullPointerException`. Med kontrollen står avsikten i koden: tomt register ger `null`, och det är ett kontrakt som anroparen kan lita på.

**Lika många lån.** Jämförelsen använder `>` och inte `>=`, vilket betyder att den medlem som ligger först i arrayen behåller förstaplatsen när flera har lika många lån. Det är ett medvetet val snarare än en slump: alternativet hade varit att returnera flera medlemmar, vilket kräver en array med okänd storlek — samma problem som `sokBok()` löser — för en upplysning som knappast motiverar det.

Kravets "utan Streams/Collections" uppfylls automatiskt, eftersom projektet inte använder något av dem någonstans. En `Collections.max()` med en `Comparator` hade gjort samma sak på en rad.

#### Menyvalet

`CliApp` tar emot medlemmen och avgör hur svaret formuleras. Tre utfall behöver skiljas åt, och ordningen mellan dem är inte fri:

```java
Member flestLan = lib.flestLan();
if (flestLan == null) {
    IO.println("Inga medlemmar i biblioteket.");
} else if (flestLan.getAntalLan() == 0) {
    IO.println("Inga medlemmar har några lån.");
} else {
    IO.println("Medlemmen med flest lån är " + flestLan.getNamn() + " (antal aktiva lån: " + flestLan.getAntalLan() + ")");
}
```

`null`-kontrollen måste stå först. Vore ordningen den omvända hade `flestLan.getAntalLan()` anropats på en `null`-referens så fort registret var tomt.

Mellangrenen finns för att en rak max-loop annars ger ett tekniskt sant men missvisande svar. Med två registrerade medlemmar som inte lånat något svarade programmet först "Medlemmen med flest lån är Anna" — hon har flest, delat med alla andra — vilket en användare läser som att hon lånat böcker.

Antalet lån skrivs ut tillsammans med namnet. Utan siffran blir menyvalet svårläst i praktiken: vid en körning där Anna gick från två lån till ett, och därmed hamnade lika med Bosse, var utskriften före och efter återlämningen ordagrant identisk. Formen "(antal aktiva lån: 1)" valdes framför "med 1 aktiva lån" eftersom den senare blir grammatiskt fel i singular.

Tillägget av ett sjunde alternativ krävde också att `printMenu()` kompletterades och att `default`-grenens text ändrades från "Välj 1-6 eller e." till "Välj 1-7 eller e." — annars hade felmeddelandet beskrivit en meny som inte längre fanns.

**Verifiering.** Körning med tomt register ger "Inga medlemmar i biblioteket.". Efter att Anna och Bosse registrerats utan lån ges "Inga medlemmar har några lån.". Efter tre utlåningar:

```
Medlemmen med flest lån är Anna (antal aktiva lån: 2)
```

och efter att en av Annas böcker lämnats tillbaka, när hon och Bosse har ett lån var:

```
Medlemmen med flest lån är Anna (antal aktiva lån: 1)
```

Anna behåller förstaplatsen vid lika, i enlighet med `>`-jämförelsen, och siffran gör förändringen synlig. Ett ogiltigt menyval svarar "Ogiltigt val: '8'. Välj 1-7 eller e.".

### Dynamisk kapacitet

VG-kravet säger att programmet ska skapa en ny, större array, kopiera över befintliga element och fortsätta när en array blir full — manuellt, utan `ArrayList`.

**Startstorlekarna sänktes med avsikt.** Konstanterna hette tidigare `MAX_BOCKER = 100` och `MAX_MEDLEMMAR = 50`. Båda namn och värden blev fel i och med det här kravet: `MAX` betyder "högsta tillåtna", men efter växlingen finns inget tak alls. De heter nu `START_BOCKER = 4` och `START_MEDLEMMAR = 2`. Det låga värdet är inte en förenkling utan en förutsättning för att växandet ska gå att visa — med 100 platser hade en rättare behövt mata in 101 böcker för hand innan något överhuvudtaget hänt.

**Växa-metoden.** Samma tre steg för varje typ: skapa dubbelt så stor array, kopiera över med en egen loop, returnera den nya.

```java
private Book[] dynamiskArray(Book[] gammal) {
    Book[] ny = new Book[gammal.length * 2];
    for (int i = 0; i < gammal.length; i++) {
        ny[i] = gammal[i];
    }
    return ny;
}
```

Kopieringen sker med en handskriven loop, inte med `Arrays.copyOf()` eller `System.arraycopy()`, i linje med kravets formulering om manuell array-växling.

**Varför metoden måste returnera.** Det hade sett rimligt ut att avsluta med `gammal = ny;` i stället för ett `return`, men det hade inte fungerat. När en array skickas som argument får metoden en *kopia av referensen*. Innehållet går att ändra genom kopian, men om man pekar om själva variabeln till ett nytt objekt ändras bara metodens egen kopia — anroparens fält pekar kvar på den gamla arrayen. Därför returnerar metoden den nya arrayen, och anroparen tilldelar om sitt fält:

```java
if (antalBocker >= boklista.length) {
    boklista = dynamiskArray(boklista);
}
boklista[antalBocker] = bok;
antalBocker++;
return true;
```

**Villkoret jämför mot `boklista.length`, inte mot `START_BOCKER`.** Konstanten är låst vid 4 och beskriver bara storleken vid start. Ett villkor mot konstanten hade varit sant vid varje tillägg efter den fjärde boken, så arrayen hade fördubblats om och om igen — 4, 8, 16, 32, 64 platser för sju böcker. Bara `.length` säger sanningen om hur stor arrayen är just nu. Det är samma insikt som gör att alla loopar måste gå till räknaren och inte till `length`, fast spegelvänd.

**Fullkontrollen ersatte nekandet.** `laggTillBok()` och `registreraMedlem()` returnerade tidigare `false` när arrayen var full. Den grenen är borta: metoderna växer i stället och lägger alltid till. Kvar finns ett enda `return false` i vardera metod — dubblettkontrollen. Följdeffekten i `CliApp` var att meddelandena "Biblioteket är fullt." och "Medlemsregistret är fullt." blev omöjliga att nå och togs bort, och att `else if`-kedjorna i menyval 1 och 2 kunde kollapsa till ett rent `else`. Menyval 3 rördes inte, eftersom `lanaBok()` fortfarande kan misslyckas av fyra skäl.

G-kravet "hantera fallet att arrayen är full" är alltså fortfarande uppfyllt, men på ett annat sätt: fallet hanteras genom att arrayen växer i stället för att tillägget nekas.

**Lånearrayen måste växa med.** Det här var kravets egentliga fälla. `lanaBok()` skrev `loans[antalAktivaLan]` utan någon kontroll, vilket var ofarligt så länge `boklista` var låst till 100 platser — fler aktiva lån än böcker kan aldrig uppstå, och `loans` var lika stor som boklistan. I samma sekund som boklistan kunde växa förbi `loans` blev det en krasch:

```
Exception in thread "main" java.lang.ArrayIndexOutOfBoundsException: Index 4 out of bounds for length 4
	at org.example.Library.lanaBok(Library.java:38)
```

Fem böcker gick att lägga in, men `loans` hade fortfarande fyra platser. Kravet nämner bara bok- och medlemsarrayen, men lånearrayen måste följa med — annars introducerar VG-kravet en regression i G-kravet att programmet aldrig ska krascha. Fullkontrollen ligger sist i `lanaBok()`, efter alla fyra `return false`-kontrollerna, så att arrayen inte växer i onödan när ett lån nekas.

**Tre nästan identiska metoder.** `dynamiskArray()`, `dynamiskMember()` och `dynamiskLan()` skiljer sig åt på ett typnamn på tre ställen vardera. Java-arrayer är inte generiska, så en metod som tar `Book[]` kan inte ta emot `Member[]`. Utan Generics finns ingen väg runt det. Duplikationen diskuteras vidare under "Reflektion: hur Collections Framework hade förändrat lösningen".

**Verifiering.** Tjugo böcker matades in, vilket kräver tre växlingar (4 → 8 → 16 → 32). Samtliga tjugo listas därefter av menyval 6, i bokstavsordning och utan `null`-platser. Tolv medlemmar gav tre växlingar av medlemsarrayen (2 → 4 → 8 → 16), och statistiken hittade rätt person på plats nio i den växta arrayen. Tjugo samtidiga lån gav tre växlingar av lånearrayen, med rätt låntagare på varje bok i statuslistan. Sökning, återlämning och statistik fungerade oförändrat efteråt, och inga undantag uppstod. Även den tidigare gränsen testades: 101 böcker och 51 medlemmar läggs numera in utan att något avvisas.

## Beskrivning av lösningen

Programmet är en kommandoradsapplikation som hanterar böcker, medlemmar och lån. Lösningen består av två lager: en datamodell (`Book`, `Member`, `Loan`) och en lagringsklass (`Library`) som äger reglerna, samt ett gränssnittslager (`CliApp`) som sköter meny, inläsning och utskrift. Data lagras i arrayer där en räknare per array håller reda på hur många platser som används, och där arrayen byts ut mot en dubbelt så stor när den blir full.

Gränssnittet anropar aldrig arrayerna direkt, och `Library` skriver aldrig ut något. All kommunikation går genom metodanrop med returvärden.

## Reflektion kring designval

**Record kontra klass.** Det viktigaste valet i projektet. `Book` och `Loan` blev records eftersom de är oföränderliga värdetyper — en bok *är* sin titel, författare och isbn, och ett lån *är* kopplingen mellan en bok och en medlem. `Member` blev en vanlig klass eftersom antalet aktiva lån ändras över tid medan medlemmen behåller sin identitet. Skillnaden är inte bara stilistisk: i en record ingår alla fält i den genererade `equals()`, så samma medlem med olika antal lån hade räknats som två olika medlemmar. Som klass jämförs objekt på referens och medlemmar slås upp på `getId()`.

**Ansvarsfördelning.** `Library` svarar, `CliApp` pratar. `farLana()` returnerar `true`/`false` i stället för att skriva ut ett meddelande, och `laggTillBok()` returnerar `false` när arrayen är full i stället för att krascha. Det gör reglerna testbara oberoende av terminalen, och felmeddelandena kan formuleras på ett enda ställe.

**Objektreferenser i `Loan`.** `Loan` håller `Member` och `Book` direkt i stället för id-strängar. Det ger typsäkerhet — kompilatorn hindrar att ett isbn skickas där ett medlems-id ska vara — och slipper uppslagningar vid varje utskrift. Viktigast är att `lan.member()` är samma objekt som ligger i medlemsarrayen, så lånräknaren aldrig kan hamna i otakt mellan de två.

**Arrayer med räknare.** Uppgiften kräver arrayer i stället för `ArrayList`, vilket innebär att varje array måste kompletteras med en egen räknare och att varje loop måste gå till räknaren i stället för till `length`. Det är mer bokföring, men det tvingar fram en tydlig bild av skillnaden mellan arrayens kapacitet och dess innehåll — en skillnad som blev helt avgörande när arrayerna sedan gjordes dynamiska.

**Inkapsling kontra användbarhet.** `lanaBok()` returnerar ett enda `boolean` trots att den kan misslyckas av fyra skäl. För att ändå ge begripliga felmeddelanden ställer `CliApp` diagnosfrågor efteråt, vilket krävde att tre hjälpmetoder öppnades från `private` till `public`. Det är en medveten avvägning: en helt sluten klass hade gett användaren ett intetsägande "det gick inte". Alternativet vore att låta `lanaBok()` returnera en felorsak i stället för ett `boolean`, vilket hade bevarat inkapslingen men gjort returtypen mer komplicerad.

**Sökning med okänt antal träffar.** Sökningen skilde sig från de övriga uppslagningarna på en punkt som fick styra utformningen: antalet svar är inte känt i förväg. Utan `ArrayList` gick det inte att låta resultatet växa, så `sokBok()` fyller en temp-array i värsta-fallsstorlek och kopierar ner till exakt antalet träffar innan den returnerar. Alternativet — att loopa två gånger, först för att räkna och sedan för att fylla — hade sparat kopieringen men upprepat matchningsvillkoret på två ställen. Valet föll på en enda plats för villkoret. Att metoden tar ett sökord i stället för en titel och en författare var också medvetet: två parametrar hade tvingat användaren att lämna ett fält tomt, och `contains("")` är sant för varje sträng.

**Kända begränsningar.** `Member` har en `setAntalLan()` som gör det möjligt att gå förbi gränsen `MAX_LAN` — en metod som `lanaBok()` i `Member` hade skyddat regeln bättre. Återlämningsdatumet räknas fram i `CliApp` vid utskriften och lagras inte i `Loan`, så systemet kan visa ett datum men inte avgöra om ett lån är försenat.

## Reflektion: hur Collections Framework hade förändrat lösningen

Uppgiften förbjuder Collections Framework, Generics och Streams. Nedan är vad de förbuden faktiskt kostade, och vad de lärde mig — det är svårt att uppskatta vad `ArrayList` gör förrän man byggt den för hand.

**Räknaren hade försvunnit.** Varje array i `Library` måste kompletteras med ett heltal som håller reda på hur många platser som används: `antalBocker`, `antalMedlemmar`, `antalAktivaLan`. Skillnaden mellan arrayens *storlek* och dess *innehåll* går igen i varje metod — `hittaBok()` måste loopa till `antalBocker` och inte till `boklista.length`, annars kraschar den på `null`-platserna i slutet. Med en `ArrayList` hade `size()` varit räknaren, och frågan hade aldrig uppstått.

**Dynamisk kapacitet är `ArrayList`s inre mekanik.** VG-kravet om manuell array-växling går ut på att skapa en större array, kopiera över och peka om. Det är i princip exakt vad `ArrayList.add()` gör när den underliggande arrayen tar slut — skillnaden är att `ArrayList` växer med femtio procent i stället för att fördubbla, och att den använder `System.arraycopy()`. Med Collections hade hela det kravet reducerats till att inte tänka på saken. Att ha skrivit `dynamiskArray()` för hand gör det däremot begripligt *varför* en `ArrayList` kan bli dyr när man lägger till många element: varje växling kopierar om allt som redan finns.

**Tre identiska metoder i stället för noll.** Den tydligaste kostnaden. Java-arrayer är inte generiska, så en metod som tar emot `Book[]` kan inte ta emot `Member[]` — kompilatorn vägrar. Resultatet är `dynamiskArray()`, `dynamiskMember()` och `dynamiskLan()`, som är teckenidentiska så när som på ett typnamn på tre ställen vardera:

```java
private Book[] dynamiskArray(Book[] gammal) {
    Book[] ny = new Book[gammal.length * 2];
    for (int i = 0; i < gammal.length; i++) {
        ny[i] = gammal[i];
    }
    return ny;
}
```

Med Generics hade en enda `<T> T[] vaxa(T[] gammal)` kunnat ersätta alla tre. Med Collections hade frågan inte ställts alls, eftersom ingen skriver växlingskod: `ArrayList<Book>`, `ArrayList<Member>` och `ArrayList<Loan>` hade skött det själva. Duplikationen är alltså inte ett designfel utan en direkt följd av begränsningen — men det är den enda platsen i projektet där jag inte kan försvara koden på egna meriter.

**Uppslagningarna hade blivit konstanttidsoperationer.** `hittaBok()`, `hittaMedlem()` och `hittaLan()` är alla linjära sökningar som i värsta fall går igenom hela arrayen. Med `HashMap<String, Book>` nycklad på isbn hade `hittaBok()` blivit ett `get()` i konstant tid. Effekten hade varit störst i `laggTillBok()`, som anropar `hittaBok()` för dubblettkontroll vid varje tillägg — att fylla biblioteket är därför en kvadratisk operation i dagens lösning.

**Lånehanteringen hade förenklats mest av alla.** `loans` är en array där återlämning löses genom att flytta sista elementet till den lucka som uppstår och nolla sista platsen, just för att undvika hål i mitten. Med en `Map<String, Loan>` nycklad på isbn hade `hittaLan()` blivit `get(isbn)` och hela `aterlamnaBok()` i praktiken blivit `remove(isbn)`. Flyttmanövern hade varit onödig.

**Sorteringen hade varit en rad.** `sorteraPaTitel()` är tjugo rader bubble sort i O(n²). Motsvarigheten är `list.sort(Comparator.comparing(Book::titel, String.CASE_INSENSITIVE_ORDER))`, som dessutom kör i O(n log n). Att ha skrivit bubble sort själv gör skillnaden konkret i stället för abstrakt.

**Sökningen hade sluppit sin omväg.** `sokBok()` vet inte i förväg hur många träffar som kommer, så den fyller en array i värsta-fallsstorlek och kopierar sedan ner till exakt rätt längd innan den returnerar. En `List` som växer vid behov hade gjort både temp-arrayen och nedkopieringen överflödiga.

**Statistiken hade varit ett anrop.** Max-loopen i `flestLan()` motsvaras av `Collections.max(medlemmar, Comparator.comparingInt(Member::getAntalLan))`. Med Streams hade den blivit ännu kortare, men då hade jag också tappat kontrollen över hur oavgjort hanteras — min loop använder `>` och låter den första medlemmen behålla platsen, vilket är ett val jag gjorde medvetet.

**Inkapslingen hade fått ett färdigt verktyg.** `getAllaBocker()` returnerar en handkopierad array just för att den som anropar inte ska kunna ändra bibliotekets innehåll utifrån. `List.copyOf()` eller `Collections.unmodifiableList()` uttrycker samma avsikt tydligare, och det senare slipper kopieringen helt.

**Vad begränsningen var värd.** Sammantaget hade lösningen blivit kortare, snabbare och mindre upprepad med Collections — förmodligen en tredjedel så mycket kod i `Library`. Men det jag hade missat är inte trivialt: skillnaden mellan en arrays kapacitet och dess innehåll, varför en växa-metod måste *returnera* den nya arrayen i stället för att tilldela parametern (referensen skickas som värde, så en omtilldelning inuti metoden syns inte hos anroparen), och varför gränsvillkoret i en loop som läser `[j + 1]` måste sluta ett steg tidigare. Det är fel jag gjorde under arbetet och fick felsöka, inte saker jag läste mig till. `ArrayList` hade dolt precis de detaljerna — vilket är poängen med den i skarp kod, och skälet att inte använda den här.

## Källkritik
Jag har skrivit och committat all Java-kod själv och rättat varje fel för hand. Anthropic Claude Code har förklarat, kompilerat och testkört koden, och jag har använt det som guidning och förklaring när jag fastnat på vägen.

## Kravuppfyllnad (G)

| Krav i uppgiften | Var det finns |
| --- | --- |
| Minst en record | `Book` och `Loan` |
| Vanlig klass med privata fält, konstruktor och get/set | `Member` — `getId()`, `getNamn()`, `getAntalLan()`, `setAntalLan()` |
| Minst en egen metod på medlemmen | `Member.farLana()` — avgör om fler lån tillåts |
| Arrayer, ingen `ArrayList` | `boklista`, `medlemmar`, `loans` i `Library` |
| Hantera full array | Arrayen byts ut mot en dubbelt så stor och programmet fortsätter — se "Dynamisk kapacitet" (VG) |
| Struktur som visar vilka böcker som är utlånade och till vem | `loans`-arrayen med `Loan(member, book)` |
| Robust meny som tål felaktig inmatning | Inget värde parsas som tal; `default` fångar ogiltiga val; `null` från tom ström hanteras |
| Lägga till bok | Menyval 1 |
| Registrera medlem | Menyval 2 |
| Låna bok, med kontroll att den finns och inte är utlånad | Menyval 3 via `lanaBok()` — fyra kontroller |
| Lämna tillbaka bok | Menyval 4 via `aterlamnaBok()` |
| Söka på del av titel eller författare, skiftlägesokänsligt, egen linjär sökloop | Menyval 5 via `sokBok()` |
| Visa alla böcker med status och till vem | Menyval 6 via `getAllaBocker()` och `hittaLan()` |
| Tydliga felmeddelanden, ingen krasch | `else if`-kedjor efter varje misslyckat anrop |
| Maven-konfiguration | `pom.xml` |
| Flera meningsfulla commits | Repots historik, en commit per färdigställt moment |
| Beskrivning och reflektion i README | Avsnitten "Beskrivning av lösningen" och "Reflektion kring designval" |

Uppgiften nämner `Scanner` som exempel på hur menyn läser inmatning. Projektet använder `IO.readln()` i stället, av robusthetsskäl som redovisas under "Inläsning med `java.lang.IO` i stället för `Scanner`". Funktionellt är de likvärdiga här — båda läser en rad text i taget från standard in, och inget värde tolkas som tal.

## Kravuppfyllnad (VG)

| Krav i uppgiften | Status | Var det finns |
| --- | --- | --- |
| Egen sorteringsalgoritm på titel för menyval 6, inte `Arrays.sort()`/`Collections.sort()` | Klart | `Library.sorteraPaTitel()` — bubble sort, beskriven under "Sortering i bokstavsordning" |
| Statistik: medlem med flest aktiva lån, utan Streams/Collections | Klart | `Library.flestLan()` och menyval 7 — beskriven under "Statistik: medlemmen med flest aktiva lån" |
| Dynamisk kapacitet: manuell array-växling utan `ArrayList` | Klart | `dynamiskArray()`, `dynamiskMember()`, `dynamiskLan()` — beskrivna under "Dynamisk kapacitet" |
| Reflektion om Collections Framework i README | Klart | Avsnittet "Reflektion: hur Collections Framework hade förändrat lösningen" |

Samtliga VG-krav är genomförda.
