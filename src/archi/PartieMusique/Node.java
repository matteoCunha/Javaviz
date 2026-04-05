package archi.PartieMusique;

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
