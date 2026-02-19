# Tableau des tests unitaires 

Ce document répertorie tous les tests unitaires du projet SAE-5.A.1-API avec leurs résultats attendus.

##  Résumé de couverture

**11 contrôleurs testés** avec un total de **52 cas de test** couvrant les scénarios heureux et les cas limites.

| # | Contrôleur | Fichier Test | Cas de test |
|---|---|---|---|
| 1 | AuthController | AuthControllerTest | 8 |
| 2 | TeamController | TeamControllerTest | 7 |
| 3 | MatchController | MatchControllerTest | 8 |
| 4 | RankingController | RankingControllerTest | 5 |
| 5 | ImportController | ImportControllerTest | 6 |
| 6 | BaseController | BaseControllerTest | 2 |
| 7 | EvenementController | EvenementControllerTest | 5 |
| 8 | ObjectiveController | ObjectiveControllerTest | 5 |
| 9 | PositionController | PositionControllerTest | 2 |
| 10 | RencontreController | RencontreControllerTest | 2 |
| 11 | TeamsController | TeamsControllerTest | 2 |



## Tests par contrôleur

### AuthControllerTest

| Test | Méthode | URL | Simulation (Mock) | Résultat attendu |
|---|---|---|---|---|
| shouldRegisterCoachSuccessfully | POST | `/auth/register/coach` | `createCoach()` retourne un Coach fictif | 200 OK, JSON avec données du coach |
| shouldRegisterPlayerSuccessfully | POST | `/auth/register/player` | `createPlayer()` retourne un Player fictif | 200 OK, JSON avec données du joueur |
| shouldLoginSuccessfully | POST | `/auth/login` | `login()` retourne un User fictif | 200 OK, JWT token dans la réponse |
| shouldFailLoginWithInvalidCredentials | POST | `/auth/login` | `login()` retourne `null` | 401 Unauthorized |
| shouldUpdateUserInformationSuccessfully | PUT | `/auth/update/{userId}` | `updateInformation()` retourne l'User modifié | 200 OK, JSON avec données mises à jour |
| shouldDeleteUserSuccessfully | DELETE | `/auth/delete/{userId}` | `deleteUser()` retourne `true` | 200 OK ou 204 No Content |
| shouldRejectInvalidUUID | POST | `/auth/register/coach` | Paramètre teamId invalide | 400 Bad Request |
| shouldRejectInvalidDateFormat | PUT | `/auth/update/{userId}` | Date de naissance au format incorrect | 400 Bad Request |



### TeamControllerTest

| Test | Méthode | URL | Simulation (Mock) | Résultat attendu |
|---|---|---|---|---|
| shouldReturnAllTeams | GET | `/teamlogo` | `getAllTeams()` retourne 1 équipe | 200 OK, JSON `{"docs": [...]}` |
| shouldReturnEmptyTeamList | GET | `/teamlogo` | `getAllTeams()` retourne une liste vide `[]` | 200 OK, JSON `{"docs": []}` |
| shouldReturnTeamById | GET | `/teamlogo/{teamId}` | `getTeamById()` retourne 1 équipe | 200 OK, JSON avec données de l'équipe |
| shouldReturn404WhenTeamNotFound | GET | `/teamlogo/{teamId}` | `getTeamById()` retourne `null` | 404 Not Found |
| shouldCreateTeamSuccessfully | POST | `/teamlogo` | `createTeam()` retourne la Team créée | 201 Created, JSON avec données |
| shouldUpdateTeamSuccessfully | PUT | `/teamlogo/{teamId}` | `updateTeam()` retourne la Team modifiée | 200 OK, JSON avec données mises à jour |
| shouldDeleteTeamSuccessfully | DELETE | `/teamlogo/{teamId}` | `deleteTeam()` retourne `true` | 200 OK ou 204 No Content |



### MatchControllerTest

| Test | Méthode | URL | Simulation (Mock) | Résultat attendu |
|---|---|---|---|---|
| shouldReturnAllMatches | GET | `/match` | `getAllMatchs()` retourne 1 match | 200 OK, JSON `{"docs": [...]}` |
| shouldReturnEmptyMatchList | GET | `/match` | `getAllMatchs()` retourne une liste vide `[]` | 200 OK, JSON `{"docs": []}` |
| shouldReturnMatchById | GET | `/match/{matchId}` | `getMatchById()` retourne 1 match | 200 OK, JSON avec données du match |
| shouldReturn404WhenMatchNotFound | GET | `/match/{matchId}` | `getMatchById()` retourne `null` | 404 Not Found |
| shouldCreateMatchSuccessfully | POST | `/match` | `createMatch()` retourne le Match créé | 201 Created, JSON avec données |
| shouldUpdateMatchSuccessfully | PUT | `/match/{matchId}` | `updateMatch()` retourne le Match modifié | 200 OK, JSON avec données mises à jour |
| shouldDeleteMatchSuccessfully | DELETE | `/match/{matchId}` | `deleteMatch()` retourne `true` | 200 OK ou 204 No Content |
| shouldReturnMatchesByTeam | GET | `/match/team/{teamId}` | `getMatchesByTeam()` retourne les matchs de l'équipe | 200 OK, JSON `{"docs": [...]}` |



### RankingControllerTest

| Test | Méthode | URL | Simulation (Mock) | Résultat attendu |
|---|---|---|---|---|
| shouldReturnCompleteRanking | GET | `/ranking` | `getRanking()` retourne la liste des équipes classées | 200 OK, JSON `{"docs": [...]}` avec classement |
| shouldReturnEmptyRanking | GET | `/ranking` | `getRanking()` retourne une liste vide `[]` | 200 OK, JSON `{"docs": []}` |
| shouldReturnRegionalRankingAvesnois | GET | `/rankingAvesnois` | `getRankingAvesnois()` retourne les équipes de la région Avesnois | 200 OK, JSON `{"docs": [...]}` |
| shouldReturnTeamRankPosition | GET | `/ranking/{teamId}` | `getTeamRankPosition()` retourne la position classée de l'équipe | 200 OK, JSON avec position et points |
| shouldReturn404WhenTeamNotInRanking | GET | `/ranking/{teamId}` | `getTeamRankPosition()` retourne `null` | 404 Not Found |



### ImportControllerTest

| Test | Méthode | URL | Simulation (Mock) | Résultat attendu |
|---|---|---|---|---|
| shouldImportMatchSuccessfully | POST | `/import/match` | `MatchManager.importMatch()` retourne le match importé | 200 OK, JSON avec données du match importé |
| shouldImportEventSuccessfully | POST | `/import/event` | `EvenementManager.importEvent()` retourne l'événement importé | 200 OK, JSON avec données de l'événement importé |
| shouldRejectInvalidTeamId | POST | `/import/match` | Parameter `teamId` au format invalide | 400 Bad Request |
| shouldRejectEmptyEventList | POST | `/import/match` | Parameter `events` est un tableau vide | 400 Bad Request |
| shouldRejectInvalidDateFormat | POST | `/import/match` | Date de rencontre au format incorrect | 400 Bad Request |
| shouldHandleImportWithMultipleEvents | POST | `/import/match` | `MatchManager.importMatch()` avec tableau d'événements | 200 OK, Match avec tous les événements associés |



### BaseControllerTest

| Test | Méthode | URL | Simulation (Mock) | Résultat attendu |
|---|---|---|---|---|
| shouldReturnServerIsOperational | GET | `/` | Aucun mock (test simple) | 200 OK, Message "Server response : OK" |
| shouldLogPongRequest | GET | `/` | `SAE5A1ApiApplication.getLogger()` enregistre la requête | 200 OK avec log au niveau FINE |



### EvenementControllerTest

| Test | Méthode | URL | Simulation (Mock) | Résultat attendu |
|---|---|---|---|---|
| shouldReturnAllEvents | GET | `/evenement` | `EvenementManager.getAllEvenements()` retourne 1 événement | 200 OK, JSON `{"docs": [...]}` |
| shouldReturnEmptyEventList | GET | `/evenement` | `EvenementManager.getAllEvenements()` retourne `[]` | 200 OK, JSON `{"docs": []}` |
| shouldImportEventsSuccessfully | POST | `/evenement/import` | `EvenementManager.save()` appelé pour chaque événement | 200 OK, Message "Importation réussie : X événements ajoutés" |
| shouldRejectEmptyEventListOnImport | POST | `/evenement/import` | Tableau vide d'événements | 200 OK, Message "Importation réussie : 0 événements ajoutés" |
| shouldHandleImportException | POST | `/evenement/import` | `EvenementManager.save()` lève une Exception | 500 Internal Server Error avec message d'erreur |



### ObjectiveControllerTest

| Test | Méthode | URL | Simulation (Mock) | Résultat attendu |
|---|---|---|---|---|
| shouldReturnAllObjectives | GET | `/objective` | `ObjectiveManager.getAllObjectives()` retourne 1 objectif | 200 OK, JSON `{"docs": [...]}` |
| shouldReturnEmptyObjectiveList | GET | `/objective` | `ObjectiveManager.getAllObjectives()` retourne `[]` | 200 OK, JSON `{"docs": []}` |
| shouldCreateObjectiveSuccessfully | POST | `/objective` | `ObjectiveManager.createObjective()` ne lève pas d'exception | 200 OK, Message "Objectif créé avec succès" |
| shouldHandleCreateObjectiveException | POST | `/objective` | `ObjectiveManager.createObjective()` lève une Exception | 500 Internal Server Error avec message d'erreur |
| shouldRejectInvalidObjectiveFormat | POST | `/objective` | Objectif avec données invalides | 400 Bad Request ou 500 selon validation |



### PositionControllerTest

| Test | Méthode | URL | Simulation (Mock) | Résultat attendu |
|---|---|---|---|---|
| shouldReturnAllPositions | GET | `/positions` | `PositionManager.getAllPositions()` retourne 1 position | 200 OK, JSON `{"docs": [...]}` |
| shouldReturnEmptyPositionList | GET | `/positions` | `PositionManager.getAllPositions()` retourne `[]` | 200 OK, JSON `{"docs": []}` |



### RencontreControllerTest

| Test | Méthode | URL | Simulation (Mock) | Résultat attendu |
|---|---|---|---|---|
| shouldReturnAllRencontres | GET | `/rencontre` | `RencontreManager.getAllRencontres()` retourne 1 rencontre | 200 OK, JSON `{"docs": [...]}` |
| shouldReturnEmptyRencontreList | GET | `/rencontre` | `RencontreManager.getAllRencontres()` retourne `[]` | 200 OK, JSON `{"docs": []}` |



### TeamsControllerTest

| Test | Méthode | URL | Simulation (Mock) | Résultat attendu |
|---|---|---|---|---|
| shouldReturnAllTeamsFromTeamsEndpoint | GET | `/teams` | `TeamsManager.getAllTeams()` retourne 1 équipe | 200 OK, JSON `{"docs": [...]}` |
| shouldReturnEmptyTeamsListFromTeamsEndpoint | GET | `/teams` | `TeamsManager.getAllTeams()` retourne `[]` | 200 OK, JSON `{"docs": []}` |


