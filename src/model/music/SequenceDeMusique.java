package model.music;

import java.lang.reflect.WildcardType;
import java.sql.SQLException;

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

        public Node getNext() { return this.next; }
        public Node getNode() { return this; }
        public Morceau getMorceaux() { return this.morceau; }


    }

    public SequenceDeMusique() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public Node getHead() { return head; }

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
        this.size++;
    }

    public void moveUp(Node n) {// bouger le morceau plus haut dans la playlist (vers le head)
        if (n == null || n == this.head) { return ; }

        Morceau temp = n.morceau;
        int iTemp = n.morceau.getPosition();
        n.morceau = n.prev.morceau;
        n.morceau.setPosition(n.prev.morceau.getPosition());
        n.prev.morceau = temp;
        n.prev.morceau.setNumeroPiste(iTemp);
    }

    public void moveDown(Node n) {
        if (n == null || n == this.tail) { return; }

        Morceau temp = n.morceau;
        int iTemp = n.morceau.getPosition();
        n.morceau = n.next.morceau;
        n.morceau.setPosition(n.next.morceau.getPosition());
        n.next.morceau = temp;
        n.next.morceau.setPosition(iTemp);
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
        this.size++;
    }

    public void remove(Morceau m) {
        Node current = this.head;
        while (current != null && current.morceau != m) { current = current.next; }
        remove(current);
        this.size--;
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
        this.size--;
    }

    public void printPlaylist() {
        Node current = head;
        System.out.println("\nSequence de Musiques :");
        int i = 1;
        while(current != null) {
            System.out.print(i + " - " + current.morceau.getPosition() + " - "); i++;
            System.out.println(current.morceau.getContent());
            current = current.next;
        }
    }

    public Node passToNext(Node current) { return current.next; }
    public Morceau getMorceau(Node current) { return current.morceau; }
}
//TODO : fonction lecture (viendras avec l'interface graphique)