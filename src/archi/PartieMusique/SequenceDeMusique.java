package archi.PartieMusique;

//implémentation doublyLinkedList qui va servir pour les albums et les playlists
public class SequenceDeMusique {
    Node head;
    Node tail;

    public void appendEnd(Morceau m) {
        Node newNode = new Node(m);
        if (this.head == null) {
            this.head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

    public void appendFront(Morceau m) {
        Node newNode = new Node(m);
        if(this.head != null) {
            newNode.next = this.head;
            this.head.prev = newNode;
        }
        this.head = newNode;

    }
}
