package com.stephenmbaalu.filmfan.Models;

import com.stephenmbaalu.filmfan.NowPlaying;

public class Similar {

    private int page;
    private MoviePlaying [] results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public MoviePlaying[] getResults() {
        return results;
    }

    public void setResults(MoviePlaying[] results) {
        this.results = results;
    }
}
