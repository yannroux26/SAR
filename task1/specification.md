# Task 1 : Canaux de communication permettant d'envoyer et recevoir des bytes

Pour écrire et recevoir des messages, nous utilisons des **Tasks**, des **Brokers** et des **Channels**.

Deux tâches utiliseront un channel pour communiquer par échanges de bytes. Pour mettre en liason deux tasks, nous utiliserons des brokers identifiés par des noms uniques.


## Broker
Un broker se connecte à un autre en utilisant la méthode **connect()**, cela nécessite alors le nom (unique) du broker cible et un numéro de port. De l'autre côté, le broker doit utiliser la méthode **accept()** en donnant le même numéro de port. Un channel est ensuite créé. Les brokers sont synchronisés (tâches simultanées) mais pas les channels.

Il peut y avoir plusieurs brokers mais un seul est aussi possible (pas recommandé).
Un broker peut être lié à plusieurs tâches mais un seul est aussi possible. Et inversement.
Un broker a un nom unique. Mais ses numéros de ports lui sont propres.

## Channel
Un Channel est un flux d'octets point à point.
Full-duplex, chaque point d'extrémité peut être utilisé pour lire ou écrire.
Les channels utilisent TCP, ils sont fifo (lossless), bidirectionnels, fonctionnent en flux d'octets et non de packets.

### lecture/écriture
La méthode **int read(byte[] bytes, int offset, int length)** stocke les bytes lus dans le buffer. Il lit au maximum **lentgh** bytes à partir de **offset**. Il retourne le nombre de bytes lus, qui ne peut être ni nul ni négatif. Si zéro est renvoyé, l'opération d'écriture se bloque jusqu'à ce qu'elle puisse progresser. Même fonctionnement pour la méthode **write**. Nous avons les règles suivantes : 

- Les deux tâches peuvent lire ou écrire simultanément à l'une ou l'autre des extrémités des canaux sans risque pour les threads. 
- Localement, à l'une des extrémités, deux tâches, l'une lisant et l'autre écrivant, opérant simultanément, sont également sûres. 
- Toutefois, les opérations de lecture ou d'écriture simultanées ne sont pas sûres sur le même point d'extrémité.  

### Déconnexion
Lorsque l'on veut arrêter d'utiliser un **Channel** on peut le déconnecter avec la méthode **disconnect()** de Channel. Il faut alors vérifier avec **disconnected()** si on est encore connecté. On peut aussi ajouter une déconnection automatique au bout d'une durée à définir.
