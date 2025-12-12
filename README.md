# Trotti UL

Service de l’Université Laval de location de trottinettes électriques pour une locomotion optimale

## Comment lancer le projet ?

Pour lancer le projet, assurez vous d'abord d'avoir un projet propore :

```bash
mvn clean
```

puis installez les dépendances nécessaires :

```bash
mvn install
```

puis compilez le projet :

```bash
mvn compile
```

enfin executer le projet avec :

```bash
mvn exec:java -Dexec.mainClass="ca.ulaval.glo4003.trotti.Main"
```

IL est également possible de lancer le projet avec le script `start.sh`:
```bash
./start.sh
```

## Tester le projet

Une fois l'application lancée, utilisez un outils d'envoie de requêtes http (curl ou postman par exemple).
Vous trouverez à la racine du projet une collection postman qui vous permettra de tester les routes (`TrottiUL.postman_collection.json`).
Voici les routes imlémentées :

---

### POST http://localhost:8080/api/user/register

#### Headers

`Content-Type : application/json`

#### Body

<details>
<summary>Exemple</summary>

```json
{
  "name": "Alexandra Dupont",
  "dateOfBirth": "2000-05-15",
  "gender": "F",
  "age": 24,
  "idul": "adupo123",
  "email": "alexandra.dupont@example.com",
  "password": "Paaaaaassw0rd!"
}
```

</details>

#### Response

- Succès : 201 CREATED + l'utilisateur créé sans le mot de passe
- Erreur : 400 Bad Request + le nom des champs invalides ou manquants

---

### POST http://localhost:8080/api/user/login

#### Headers

`Content-Type : application/json`

#### Body

<details>
<summary>Exemple</summary>

```json
{
  "email": "alexandra.dupont@example.com",
  "password": "Paaaaaassw0rd!"
}
```

</details>

#### Response

- Succès : 200 OK - token
- Erreur : 400 Bad Request - Email and password required.
- Erreur : 401 Unauthorized - Invalid email or password

---

### GET http://localhost:8080/api/user/all

#### Headers

`Content-Type : application/json`

#### Body

No body

#### Response

- Succès : 200 OK
```json
[
    {
        "name": "Alexandra Dupont",
        "dateOfBirth": "2000-05-15",
        "gender": "F",
        "age": 24,
        "idul": "adupo123",
        "email": "alexandra.dupont@example.com",
        "password": "Paaaaaassw0rd!",
        "paymentInfo": {
            "cardNumber": "4111111111111111",
            "cardHolder": "",
            "expiryDate": "12/28",
            "cvv": "123"
        },
        "purchasedPasses": [
            {
                "session": "A2025",
                "extraMinutesPerDay": 20,
                "monthlyBilling": true,
                "totalPrice": 61
            }
        ]
    }
]
```

---

### POST http://localhost:8080/api/pass/purchase

#### Headers

`Content-Type : application/json`

`Authorization : Bearer YOUR-USER-TOKEN`

> Pour récupérer votre token utilisateur vous devez vous [connecter](#post-httplocalhost8080apiuserlogin).

#### Body

<details>
<summary>Exemple</summary>

```json
{
  "session": "A2025",
  "extraMinutesPerDay": 20,
  "monthlyBilling": true,
  "cardNumber": "4111111111111111",
  "cardHolder": "Alexandra Dupont",
  "expiryDate": "12/28",
  "cvv": "123"
}
```

</details>

#### Response

- Succès : 201 Created

```json
{
  "message": "Purchase successful",
  "invoice": {
    "session": "A2025",
    "totalPrice": 61,
    "monthlyBilling": true
  }
}
```

- Erreur : 401 Unauthorized

```json
{
  "error": "Token manquant ou invalide"
}
```

---

### GET http://localhost:8080/api/pass/my-purchases

#### Headers

`Content-Type : application/json`

`Authorization : Bearer YOUR-USER-TOKEN`

> Pour récupérer votre token utilisateur vous devez vous [connecter](#post-httplocalhost8080apiuserlogin).


#### Body

No body

</details>

#### Response

- Succès : 200 OK

```json
[
    {
        "session": "A2025",
        "extraMinutesPerDay": 20,
        "monthlyBilling": true,
        "totalPrice": 61
    }
]
```
- Erreur : 401 Unauthorized

```json
{
  "error": "Token manquant ou invalide"
}
```

---

### POST http://localhost:8080/api/user/activate

#### Headers

`Content-Type : application/json`

`Authorization : Bearer YOUR-USER-TOKEN`

> Pour récupérer votre token utilisateur vous devez vous [connecter](#post-httplocalhost8080apiuserlogin).

#### Body

No body

#### Response

- Succès : 200 OK

```json
{
  "message": "Account activated successfully"
}
```

- Erreur : 401 Unauthorized

```json
{
  "error": "Token manquant ou invalide"
}
```

---

### POST http://localhost:8080/api/ride/request-code

#### Headers

`Content-Type : application/json`

`Authorization : Bearer YOUR-USER-TOKEN`

> Pour récupérer votre token utilisateur vous devez vous [connecter](#post-httplocalhost8080apiuserlogin).

#### Body

No body

#### Response

- Succès : 200 OK - Le code unique est envoyé par courriel
- Erreur : 401 Unauthorized

```json
{
  "error": "Token manquant ou invalide"
}
```

---

### POST http://localhost:8080/api/ride/start

#### Headers

`Content-Type : application/json`

`Authorization : Bearer YOUR-USER-TOKEN`

> Pour récupérer votre token utilisateur vous devez vous [connecter](#post-httplocalhost8080apiuserlogin).

#### Body

<details>
<summary>Exemple</summary>

```json
{
  "station": "VACHON",
  "code": "45496",
  "slot": 1
}
```

</details>

#### Response

- Succès : 200 OK

```json
{
  "message": "Ride started successfully"
}
```

- Erreur : 400 Bad Request

```json
{
  "error": "Invalid code or scooter not available"
}
```

- Erreur : 401 Unauthorized

```json
{
  "error": "Token manquant ou invalide"
}
```

---

### POST http://localhost:8080/api/ride/end

#### Headers

`Content-Type : application/json`

`Authorization : Bearer YOUR-USER-TOKEN`

> Pour récupérer votre token utilisateur vous devez vous [connecter](#post-httplocalhost8080apiuserlogin).

#### Body

<details>
<summary>Exemple</summary>

```json
{
  "station": "VACHON",
  "slot": 1
}
```

</details>

#### Response

- Succès : 200 OK

```json
{
  "message": "Ride ended successfully"
}
```

- Erreur : 400 Bad Request

```json
{
  "error": "No active ride or invalid station/slot"
}
```

- Erreur : 402 Payment Required

```json
{
  "error": "Payment declined"
}
```

- Erreur : 404 Not Found

```json
{
  "error": "User or station not found"
}
```

- Erreur : 401 Unauthorized

```json
{
  "error": "Token manquant ou invalide"
}
```

---

### GET http://localhost:8080/api/ride/history

#### Headers

`Content-Type : application/json`

`Authorization : Bearer YOUR-USER-TOKEN`

> Pour récupérer votre token utilisateur vous devez vous [connecter](#post-httplocalhost8080apiuserlogin).

#### Body

No body

#### Response

- Succès : 200 OK

```json
[
  {
    "rideId": "ride-123",
    "station": "VACHON",
    "slot": 1,
    "startTime": "2025-01-15T14:30:00",
    "endTime": "2025-01-15T14:45:00",
    "duration": 15,
    "cost": 5.50
  }
]
```

- Erreur : 401 Unauthorized

```json
{
  "error": "Token manquant ou invalide"
}
```

---

### GET http://localhost:8080/api/ride/trip-history

#### Headers

`Content-Type : application/json`

`Authorization : Bearer YOUR-USER-TOKEN`

> Pour récupérer votre token utilisateur vous devez vous [connecter](#post-httplocalhost8080apiuserlogin).

#### Body

No body

#### Response

- Succès : 200 OK

```json
{
  "trips": [
    {
      "rideId": "ride-123",
      "station": "VACHON",
      "slot": 1,
      "startTime": "2025-01-15T14:30:00",
      "endTime": "2025-01-15T14:45:00",
      "duration": 15,
      "cost": 5.50
    }
  ],
  "summary": {
    "totalRides": 10,
    "totalDistance": 25.5,
    "totalCost": 85.00
  }
}
```

- Erreur : 401 Unauthorized

```json
{
  "error": "Token manquant ou invalide"
}
```

---

### GET http://localhost:8080/api/user/balance

#### Headers

`Content-Type : application/json`

`Authorization : Bearer YOUR-USER-TOKEN`

> Pour récupérer votre token utilisateur vous devez vous [connecter](#post-httplocalhost8080apiuserlogin).

#### Body

No body

#### Response

- Succès : 200 OK

```json
{
  "amount": 25.50
}
```

- Erreur : 401 Unauthorized

```json
{
  "error": "Token manquant ou invalide"
}
```

---

### POST http://localhost:8080/api/user/balance/pay

#### Headers

`Content-Type : application/json`

`Authorization : Bearer YOUR-USER-TOKEN`

> Pour récupérer votre token utilisateur vous devez vous [connecter](#post-httplocalhost8080apiuserlogin).

#### Body

No body

#### Response

- Succès : 201 Created

```json
{
  "message": "Purchase successful",
  "invoice": {
    "session": "A2025",
    "totalPrice": 25.50,
    "monthlyBilling": true
  }
}
```

- Erreur : 400 Bad Request

```json
{
  "error": "Invalid request"
}
```

- Erreur : 401 Unauthorized

```json
{
  "error": "Token manquant ou invalide"
}
```

---

### PUT http://localhost:8080/api/user/settings/auto-pay

#### Headers

`Content-Type : application/json`

`Authorization : Bearer YOUR-USER-TOKEN`

> Pour récupérer votre token utilisateur vous devez vous [connecter](#post-httplocalhost8080apiuserlogin).

#### Body

<details>
<summary>Exemple</summary>

```json
{
  "enabled": true
}
```

</details>

#### Response

- Succès : 200 OK

- Erreur : 400 Bad Request

```json
{
  "error": "Invalid request"
}
```

- Erreur : 401 Unauthorized

```json
{
  "error": "Token manquant ou invalide"
}
```

---

### POST http://localhost:8080/api/stations/{stationId}/maintenance/start

#### Headers

`Content-Type : application/json`

`Authorization : Bearer YOUR-TECHNICIAN-TOKEN`

> Cet endpoint est réservé aux techniciens. Le token doit correspondre à un compte technicien.

#### Body

No body

#### Response

- Succès : 200 OK

- Erreur : 401 Unauthorized

```json
{
  "error": "Token manquant ou invalide"
}
```

- Erreur : 403 Forbidden

```json
{
  "error": "Vous n'avez pas les droits d'accès (rôle technicien requis)"
}
```

---

### POST http://localhost:8080/api/stations/{stationId}/maintenance/end

#### Headers

`Content-Type : application/json`

`Authorization : Bearer YOUR-TECHNICIAN-TOKEN`

> Cet endpoint est réservé aux techniciens. Le token doit correspondre à un compte technicien.

#### Body

No body

#### Response

- Succès : 200 OK

- Erreur : 401 Unauthorized

```json
{
  "error": "Token manquant ou invalide"
}
```

- Erreur : 403 Forbidden

```json
{
  "error": "Vous n'avez pas les droits d'accès (rôle technicien requis)"
}
```

---

### POST http://localhost:8080/api/stations/{stationId}/maintenance

#### Headers

`Content-Type : application/json`

`Authorization : Bearer YOUR-USER-TOKEN`

> Pour récupérer votre token utilisateur vous devez vous [connecter](#post-httplocalhost8080apiuserlogin).

#### Body

No body

#### Response

- Succès : 200 OK

- Erreur : 401 Unauthorized

```json
{
  "error": "Token manquant ou invalide"
}
```

---

### POST http://localhost:8080/api/scooters/transfer/start

#### Headers

`Content-Type : application/json`

`Authorization : Bearer YOUR-TECHNICIAN-TOKEN`

> Cet endpoint est réservé aux techniciens. Le token doit correspondre à un compte technicien.

#### Body

<details>
<summary>Exemple</summary>

```json
{
  "station": "VACHON",
  "slot": 1
}
```

</details>

#### Response

- Succès : 200 OK

```json
{
  "scooterId": "3c6ac800-e026-4865-9dec-3451bc094b85"
}
```

- Erreur : 401 Unauthorized

```json
{
  "error": "Token manquant ou invalide"
}
```

- Erreur : 403 Forbidden

```json
{
  "error": "Vous n'avez pas les droits d'accès (rôle technicien requis)"
}
```

---

### POST http://localhost:8080/api/scooters/transfer/end

#### Headers

`Content-Type : application/json`

`Authorization : Bearer YOUR-TECHNICIAN-TOKEN`

> Cet endpoint est réservé aux techniciens. Le token doit correspondre à un compte technicien.

#### Body

<details>
<summary>Exemple</summary>

```json
{
  "station": "PEPS",
  "scooterId": "3c6ac800-e026-4865-9dec-3451bc094b85",
  "slot": 22
}
```

</details>

#### Response

- Succès : 200 OK

- Erreur : 401 Unauthorized

```json
{
  "error": "Token manquant ou invalide"
}
```

- Erreur : 403 Forbidden

```json
{
  "error": "Vous n'avez pas les droits d'accès (rôle technicien requis)"
}
```

---

## Récit 1

En tant qu’Alexandra, je peux me créer un compte afin d’acheter une passe de trottinette

### Critères d’acceptation

**Mon profil doit contenir certaines informations sur mon identité**
- [x] Nom
- [x] Date de naissance
- [x] Âge
- [x] Idul
  - [x] Pas de regex ou de validation de l'idul

**Je dois indiquer mon mot de passe ainsi que l’adresse courriel que je vais utiliser pour me connecter**
- [x] La connexion expire après 60 minutes
- [x] Le mot de passe doit avoir les caractéristiques suivantes: au minimum 10 caractères, une majuscule, un chiffre et un caractère spécial

---

## Récit 2

En tant qu’Alexandra quand j’effectue une commande, je peux choisir le type de passe qui me convient pour ma session

### Critères d’acceptation

- [x] Devoir commencer un processus d’achat
- [x] Devoir sélectionner la durée maximale de déplacement illimité par jour — Options en bond de 10 minutes
- [x] Décider que la facturation est effectuée après chaque voyage ou mensuellement

Conclure la transaction de vente avec le paiement par carte de crédit :

- [x] Le prix de base est de 45$ pour une session. (inclut 30 minutes par jour sans frais supplémentaire)
- [x] 2$ supplémentaires par mois pour chaque 10 minutes de durée maximale de déplacement journalier dépassant le forfait de base.
- [x] Si la transaction ne fonctionne pas, le processus d’achat n’est pas complété. Possible de changer le paiement
- [x] Les informations de paiement sont gardées pour les prochains voyages à son compte
- [x] Il ne faut pas ajouter une route pour le changer plus tard
- [x] Recevoir une facture de la transaction effectuée
- [x] La passe est valide pour une seule session
- [x] Une session correspond au session universitaire comme A2025, H2026, E2026, donc c'est l'alternance entre session d'été, automne, hiver et l'Année
- [x] La passe est valide de la date de début de session actuelle, jusqu’à la date de début de la prochaine
- [x] La session doit être sélectionnée lors de l'achat

---

## Récit 3

En tant qu'Alexandra quand j'ai complété ma commande, je peux maintenant effectuer des transports à trottinette

### Critères d'acceptation

- [x] Elle veut recevoir une notification par courriel de l'activation du compte
- [x] Elle est maintenant autorisée à faire des voyages
- [x] Elle peut demander un code unique pour débarrer les trottinettes
  - [x] Le code est envoyé par courriel
  - [x] Le code doit avoir au minimum 4 chiffres et au maximum 6 chiffres
  - [x] Le code est valide pour seulement 60 secondes
  - [x] Elle devra demander un nouveau code pour effectuer un nouveau voyage (générer un nouveau code)
- [x] Les voyages doivent être seulement possibles si la session active est celle prise dans l'abonnement

---

## Récit 4

En tant qu'Alexandra, je peux entrer le code unique à une station de trottinettes pour débloquer la trottinette afin de commencer mon voyage en trottinette

### Critères d'acceptation

- [x] Je dois indiquer mon code unique pour débarrer la trottinette que je veux utiliser
- [x] La trottinette se trouve à un emplacement à la station pour que je puisse l'identifier (Station X - emplacement #1)
- [x] La trottinette doit avoir un seuil d'énergie à plus de 15 % pour qu'elle soit débarrée
- [x] Au démarrage de l'application, les stations ont 80% de leur capacité maximale de trottinettes
- [x] Les stations sont disponibles (référence externe mentionnée dans les specs)

---