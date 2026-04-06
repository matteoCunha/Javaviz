package archi.PartieMusique;

import archi.interfaces.Consultable;

import java.util.Date;

public class Group implements Consultable {
    private int id;
    private Date dateCreation;
    private String description;
    private String name;


    public String getName() { return name; }

    @Override
    public String getHeaderTitle(){ return this.getName();}

    @Override
    public String getSubtitle(){ return "Artistes :";}

    @Override
    public String getDescription(){ return this.description;}

    @Override
    public SequenceDeMusique getElements(){ return new SequenceDeMusique();}
}
