# Task 4 : Canaux de communication permettant d'envoyer et recevoir des messages complets en full événementiel

Cette fois-ci ce ne sera pas seulement l'interface qui sera événementiel mais tout le système. La spécification change donc très peu (les queueBroker, MessagesQueue donc l'interface de la task3 seront ajoutés si le temps le permet):

Pour écrire et recevoir des messages, nous utilisons des **Tasks**, des **Brokers** et des **Channels**. Cette fois, nous ne devons nous bloquer nul part.

On utilisera une pompe de threads qui exécutera les évennements les uns après les autres.

Deux tâches utiliseront un **Channel** pour communiquer par échanges de messages. Pour mettre en liason deux tasks, nous utiliserons des **Brokers** identifiés par des noms uniques.


## Broker
Un broker utilise un **ConnectListener** et un **AcceptListener**.
Un **Broker** se connecte à un autre en utilisant la méthode **connect(String name, int port, ConnectListener listener)**, cela nécessite alors le nom (unique) du **Broker** cible et un numéro de port. Une fois le **connect()** terminé, il appelle le ConnectListener fourni en argument et codé par l'utilisateur. Un **Channel** est ensuite créé dans le cas réussi. Puis il exécute soit la méthode **connected(Channel queue)** soit la méthode **refused()** en fonction du résultat du **connect()**.

De l'autre côté, le **Broker** doit utiliser la méthode **bind(int port, AcceptListener listener)** en donnant le même numéro de port. Le **Broker** attend un **connect()**. Un **Channel** est ensuite créé et appelle la méthode **accepted(Channel queue)** du AcceptListener fourni. 
Il est aussi possible de fermer le bind avec la méthode **unbind(int port)** même si le bind se ferme tout seul si on se connecte..


Il peut y avoir plusieurs **Brokers** mais un seul est aussi possible (pas recommandé).
Un **Broker** peut être lié à plusieurs tâches mais un seul est aussi possible. Et inversement.
Un **Broker** a un nom unique. Mais ses numéros de ports lui sont propres.

## Channel
Un **Channel** est un flux d'octets point à point.
Full-duplex, chaque point d'extrémité peut être utilisé pour lire ou écrire.
Les **Channels** utilisent TCP, ils sont fifo, fonctionnent en flux d'octets. Mais ne sont plus lossless. Un Listener prévient de l'arrivée d'un message.

### lecture/écriture
La méthode **byte[] receive** stocke le message lu dans le buffer. L'opération de lecture se bloque jusqu'à lecture complète du message. Même fonctionnement pour la méthode **boolean send(byte[] bytes, int offset, int length)**, l'écriture se fait à partir de l offset et le message jusqu'à offset+length. Renvois True si le message peut être envoyé false sinon. Nous avons les règles suivantes : 

- Les deux tâches peuvent lire ou écrire simultanément à l'une ou l'autre des extrémités des canaux sans risque pour les threads. 
- Localement, à l'une des extrémités, deux tâches, l'une lisant et l'autre écrivant, opérant simultanément, sont également sûres. 
- Toutefois, les opérations de lecture ou d'écriture simultanées ne sont pas sûres sur le même point d'extrémité.  

### Déconnexion
Lorsque l'on veut arrêter d'utiliser un **Channel** on peut le déconnecter avec la méthode **disconnect()** de **Channel**. Il faut alors vérifier avec **disconnected()** si on est encore connecté. On peut aussi ajouter une déconnection automatique au bout d'une durée à définir.

Lorsque l'on est déconnecté, on ne peux plus lire ni écrire, cela renvois une exception. Si l'autre est déconnecté, on peut écrire dans le vide et on peut lire le(s) dernier(s) byte(s) envoyé(s). Puis on devient déconnecté.