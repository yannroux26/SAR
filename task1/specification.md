# Task 1 : Canaux de communication permettant d'envoyer et recevoir des bytes

Pour écrire et recevoir des messages, nous utilisons des **Tasks**, des **Brokers** et des **Channels**.

Deux taches utiliseront un channel pour communiquer par échanges de bytes. Pour mettre en liason deux tasks, nous utiliserons des brokers identifiés par des noms uniques.

Un broker se connecte à un autre en utilisant la méthode **connect()**, cela nécessite alors le nom (unique) du broker cible et un numéro de port. De l'autre côté, le broker doit utiliser la méthode **accept()** en donnant le même numéro de port. Un channel est ensuite créé.

Les channels utilisent TCP, ils sont fifo (lossless), bidirectionnels, fonctionnent en flux d'octets et non de packets.

Les brokers sont synchronisés (taches simultanées) mais pas les channels.
La méthode **int read(byte[] bytes, int offset, int length)** stocke les bytes lus dans le buffer. Il lit au maximum **lentgh** bytes à partir de **offset**. Il retourne le nombre de bytes lus, -1 en cas d'erreur. Pas de 0 car bloquant.
Même fonctionnement pour la méthode **write**.

Il peut y avoir plusieurs brokers mais un seul est aussi possible (pas recommandé).
Un broker peut être lié à plusieurs tâches mais un seul est aussi possible. Et inversement.
Un broker a un nom unique. Mais ses numéros de ports lui sont propres.

Lorsque l'on veut arrêter d'utiliser un **Channel** on peut le déconnecter avec la méthode **disconnect()** de Channel. Il faut alors vérifier avec **disconnected()** si on est encore connecté. On peut aussi ajouter une déconnection automatique au bout d'une durée à définir.

