# PSS
Ce bot Discord est développé par StartPimp47 et DeepDev404. Il a pour projet de protéger au maximum les serveurs Discord et les systèmes de protection ne seront jamais publics pour éviter tout contournement de ceux-ci.

## Version Alpha
Version de base.

**Système de langue**. Ceci permet à chaque utilisateur de pouvoir comprendre le bot dans sa langue natale.
  * Français
  * English
  * 日本語
  * Commande `.setlang` et `.getlang`

**Commande `.help`**, version bêta. Il contient toutes les commandes de la version Alpha.

## v0.0.1

**Système de contrôle de contenu**.
 * Majuscules
 * Mots bannis
 * Liens
 
La détection des majuscules se fait simplement. Les caractères non-alphabétiques sont comptés comme étant des majuscules, ainsi que les espaces et les sauts de lignes. Le reste, tel que les miniscules, les lettres Coréennes, etc. ne sont pas comptés.
> ^([^a-z0-9あ-んア-ンㄱ-희.]+)$
