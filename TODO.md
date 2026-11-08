# TODO — gestion-compte-api

Issu des revues architecturales du 2026-10-26 et 2026-11-08.

---

## FAIT

- [x] **Tests unitaires — fonctions prioritaires**
  - `normalize`, `monthSortKey`, `findSortedInsertRowForDate`, `parseOperation`, `parseDouble`, `findLastFilledReelRow`, `findRowByLabel`
  - Déplacés dans `SheetReaderTest` suite au refactoring

- [x] **Découper `GoogleSheetsService`**
  - `SheetReader` — lecture, navigation et helpers purs
  - `SheetWriter` — toutes les opérations d'écriture API
  - `GoogleSheetsService` — orchestrateur métier (délègue à reader/writer)

- [x] **Sécurité — wildcard `/api/diagnostic/**` supprimé** (`SecurityConfig`)
- [x] **Validation `monthKey`** — `@Pattern` sur les 7 endpoints de `MonthController`
- [x] **Correction `OperationRequest` à 5 args** dans `depointeOperation`
- [x] **`@NotBlank` sur `compte`** dans `UpdateOperationRequest`
- [x] **Guard `sheetExists`** dans `depointeOperation` — empêche `createMonth` non intentionnel
- [x] **Filtrage onglet RECURRENTS** dans `getMonthKeys`
- [x] **`@Validated` + `@NotBlank`** sur `AppProperties` — fail-fast au démarrage

---

## MOYEN TERME

- [ ] **Extraire `RecurrentEntry` hors de `SheetReader` (`SheetReader.java:287`)**
  - Type métier exposé comme inner record public dans une classe de service technique
  - En faire un record standalone dans `service/` ou `dto/`

- [ ] **Remplacer `catch (Exception e)` dans `readRecurrentsTemplate` (`SheetReader.java:305`)**
  - Cibler `GoogleJsonResponseException` pour l'onglet absent
  - Logger le stack trace complet pour les autres erreurs inattendues

- [ ] **Documenter la sémantique `montant == null → clearAmountCell`**
  - Règle métier implicite : si `montant` est null dans `UpdateOperationRequest`, la cellule est effacée
  - Ajouter un commentaire dans le DTO et mettre à jour le CLAUDE.md

- [ ] **Homogénéiser `VALUE_INPUT_OPTION`**
  - `writeOperation` utilise `RAW`, `updateReelFormulas` utilise `USER_ENTERED`
  - Documenter le choix délibéré ou aligner les deux

---

## LONG TERME

- [ ] **Tests unitaires sur `GoogleSheetsService`**
  - Couvrir `addOperation`, `pointerOperation`, `transfererOperation`, `createMonth` avec mocks SheetReader/SheetWriter

- [ ] **Profil Spring `test` avec mock Google Sheets**
  - Permettre des tests d'intégration sans dépendance réseau
