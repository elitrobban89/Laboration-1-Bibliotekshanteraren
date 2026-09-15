# Laboration 1 — Bibliotekshanteraren (CLI)

Ett enkelt kommandoradsprogram i Java för att hantera ett bibliotek: böcker, medlemmar samt lån och återlämning.

## Status

Menysystemet är på plats och körbart. Menyvalen skriver just nu ut vad som valts — funktionaliteten bakom varje val implementeras steg för steg.

- [x] Meny med loop och avslut
- [ ] Lägg till bok
- [ ] Registrera medlem
- [ ] Låna bok
- [ ] Lämna tillbaka bok
- [ ] Sök bok (titel eller författare)
- [ ] Visa alla böcker och status

## Krav

- JDK 26 (projektet kompileras mot source/target 26 och använder `java.lang.IO` samt `main` utan parametrar)
- Maven (wrapper medföljer: `mvnw` / `mvnw.cmd`)

## Bygga och köra

```bash
# Bygg
./mvnw clean package        # Windows: mvnw.cmd clean package

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

## Projektstruktur

```
Bibliotekshanteraren/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── org/
                └── example/
                    └── CliApp.java     # meny och programloop
```

## Författare

Robert — Laboration 1, Javakurs
