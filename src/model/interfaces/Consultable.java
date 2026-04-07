package model.interfaces;

import model.music.SequenceDeMusique;

public interface Consultable {
    public String getHeaderTitle();
    public String getSubtitle();
    public String getDescription();
    public SequenceDeMusique getElements();
}
