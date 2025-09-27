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

## Tester le projet

Une fois l'application lancée, utilisez un outils d'envoie de requêtes http (curl ou postman par exemple).
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
