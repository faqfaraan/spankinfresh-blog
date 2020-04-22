package com.spankinfresh.blog.data;

import com.spankinfresh.blog.domain.BlogPost;
import com.spankinfresh.blog.domain.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class BlogPostJdbcTemplateRepository {

    private JdbcTemplate jdbcTemplate;
    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<BlogPost> getAllBlogPostingsOmittingContent() {
        return jdbcTemplate.query(
                "select id, title, category, date_posted " +
                        "from blog_post order by date_posted desc",
                BeanPropertyRowMapper.newInstance(BlogPost.class));
    }

    public List<Category> getCategoryList() {
        return jdbcTemplate.query(
                "select distinct category from blog_post order by category",
                (resultsRow, rowNum) -> {
                    Category category = new Category();
                    category.setId(rowNum);
                    category.setCategoryName(resultsRow.getString("category"));
                    return category;
                });
    }
}
