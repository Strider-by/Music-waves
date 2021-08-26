package by.musicwaves.entity;

public class Artist implements Entity
{
    private int id;
    private String name;
    private boolean active;
    private String imageFileName;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public String getImageFileName()
    {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName)
    {
        this.imageFileName = imageFileName;
    }
}
