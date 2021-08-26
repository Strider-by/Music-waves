package by.musicwaves.entity;

public class AudioTrack implements Entity
{
    private int id;
    private String name;
    private int genreId;
    private int albumId;
    private int trackNumber;
    private String fileName;
    private boolean active;

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

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public int getGenreId()
    {
        return genreId;
    }

    public void setGenreId(int genreId)
    {
        this.genreId = genreId;
    }

    public int getAlbumId()
    {
        return albumId;
    }

    public void setAlbumId(int albumId)
    {
        this.albumId = albumId;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public int getTrackNumber()
    {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber)
    {
        this.trackNumber = trackNumber;
    }
}
