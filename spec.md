# Task 2 : Canaux de communication permettant d'envoyer et recevoir des messages complets

Pour écrire et recevoir des messages, nous utilisons des **Tasks**, des **QueueBrokers** et des **MessageQueues**.

Deux tâches utiliseront un **MessageQueue** pour communiquer par échanges de messages. Pour mettre en liason deux tasks, nous utiliserons des **queueBrokers** identifiés par des noms uniques.


## QueueBroker
Un **queueBroker** se connecte à un autre en utilisant la méthode **connect()**, cela nécessite alors le nom (unique) du **queueBroker** cible et un numéro de port. De l'autre côté, le **queueBroker** doit utiliser la méthode **accept()** en donnant le même numéro de port. Un QueueMessage est ensuite créé. Les **queueBrokers** sont synchronisés (tâches simultanées) mais pas les messageQueues.

Il peut y avoir plusieurs **queueBrokers** mais un seul est aussi possible (pas recommandé).
Un **queueBroker** peut être lié à plusieurs tâches mais un seul est aussi possible. Et inversement.
Un **queueBroker** a un nom unique. Mais ses numéros de ports lui sont propres.

## MessageQueue
Un **messageQueue** est un flux de paquets (=messages) point à point.
Full-duplex, chaque point d'extrémité peut être utilisé pour lire ou écrire.
Les **messageQueues** utilisent UDP, ils sont fifo, fonctionnent en packets et non en flux d'octets. Mais ne sont plus lossless.

### lecture/écriture
La méthode **byte[] receive** stocke le message lu dans le buffer. L'opération de lecture se bloque jusqu'à lecture complète du message. Même fonctionnement pour la méthode **void send(byte[] bytes, int offset, int length)**, l'écriture se fait à partir de l offset et le message jusqu'à offset+length. Nous avons les règles suivantes : 

- Les deux tâches peuvent lire ou écrire simultanément à l'une ou l'autre des extrémités des canaux sans risque pour les threads. 
- Localement, à l'une des extrémités, deux tâches, l'une lisant et l'autre écrivant, opérant simultanément, sont également sûres. 
- Toutefois, les opérations de lecture ou d'écriture simultanées ne sont pas sûres sur le même point d'extrémité.  

### Déconnexion
Lorsque l'on veut arrêter d'utiliser un **messageQueue** on peut le déconnecter avec la méthode **disconnect()** de **messageQueue**. Il faut alors vérifier avec **disconnected()** si on est encore connecté. On peut aussi ajouter une déconnection automatique au bout d'une durée à définir.

Lorsque l'on est déconnecté, on ne peux plus lire ni écrire, cela renvois une exception. Si l'autre est déconnecté, on peut écrire dans le vide et on peut lire le(s) dernier(s) message(s) COMPLET(S) envoyé(s). Puis on devient déconnecté.