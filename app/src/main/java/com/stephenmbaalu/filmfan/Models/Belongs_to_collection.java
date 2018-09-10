package com.stephenmbaalu.filmfan.Models;

public class Belongs_to_collection {


    private String id;

    private String backdrop_path;

    private String name;

    private String poster_path;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getBackdrop_path ()
    {
        return backdrop_path;
    }

    public void setBackdrop_path (String backdrop_path)
    {
        this.backdrop_path = backdrop_path;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getPoster_path ()
    {
        return poster_path;
    }

    public void setPoster_path (String poster_path)
    {
        this.poster_path = poster_path;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [id = "+id+", backdrop_path = "+backdrop_path+", name = "+name+", poster_path = "+poster_path+"]";
    }

}
