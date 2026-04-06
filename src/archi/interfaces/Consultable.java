package archi.interfaces;

import archi.PartieMusique.SequenceDeMusique;

public interface Consultable {
    public String getHeaderTitle();
    public String getSubtitle();
    public String getDescription();
    public SequenceDeMusique getElements();
}
