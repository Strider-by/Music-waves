package by.musicwaves.entity;

public class Album implements Entity
{
    private int id;
    private int year;
    private String name;
    private int artist;
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

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
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

    public int getArtist()
    {
        return artist;
    }

    public void setArtist(int artist)
    {
        this.artist = artist;
    }
}
