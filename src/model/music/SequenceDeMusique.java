package model.music;

//implémentation doublyLinkedList qui va servir pour les albums et les playlists
public class SequenceDeMusique {
    Node head;
    Node tail;
    int size;

    public class Node {
        Morceau morceau;
        Node next;
        Node prev;

        public Node(Morceau m) {
            this.morceau = m;
            this.next = null;
            this.prev = null;
        }
    }

    public SequenceDeMusique() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public SequenceDeMusique(Morceau m) {
        this.head = new Node(m);
        this.tail = this.head;
        this.size = 1;
    }

    public void pushFront(Morceau m) {
        Node newNode = new Node(m);
        if(this.head == null || this.tail == null) { //head ==null so tail also
            this.head = newNode;
            this.tail = this.head;
        } else {
            newNode.next = head;
            this.head.prev = newNode;
            this.head = newNode;
        }
    }

    public void pushBack(Morceau m) {
        Node newNode = new Node(m);
        if (this.head == null || this.tail == null) { //empty list so = pushfront
            this.head = newNode;
            this.tail = this.head;
        } else {
            this.tail.next = newNode;
            newNode.prev = this.tail;
            this.tail = newNode;
        }
    }

    public void remove(Morceau m) {
        Node current = this.head;
        while (current != null && current.morceau != m) { current = current.next; }
        remove(current);
    }

    public void remove(Node n) {
        if (n == null) return;

        if (n == this.head) {
            this.head = n.next;
            if(this.head != null) {
                this.head.prev = null;
            } else {
                this.tail = null;
            }
        }
        else if (n == this.tail) {
            this.tail = n.prev;
            if (this.tail != null) {
                this.tail.next = null;
            } else {
                this.head = null;
            }
        }
        else {
            n.next.prev = n.prev;
            n.prev.next = n.next;
        }

        n.next = null; n.prev = null;
    }
}
