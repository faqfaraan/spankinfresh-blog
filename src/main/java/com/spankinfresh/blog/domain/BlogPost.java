package com.spankinfresh.blog.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @Size(min = 1, max = 200, message = "Please enter a category name of up to 200 characters")
    private String category;

    @Temporal(TemporalType.TIMESTAMP)
    private Date datePosted;

    @NotNull
    @Size(min = 1, max = 200 , message = "Please enter a title up to 200 characters in length")
    private String title;

    @NotNull
    @Size(min = 1, max = 500000, message = "Content is required")
    private String content;

    public BlogPost() {
    }

    public BlogPost(long id, String category, Date datePosted, String title, String content) {
        this.id = id;
        this.category = category;
        this.datePosted = datePosted;
        this.title = title;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
