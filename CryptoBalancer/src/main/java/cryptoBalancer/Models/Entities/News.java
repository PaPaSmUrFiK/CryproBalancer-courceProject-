package cryptoBalancer.Models.Entities;

public class News {
    private String title;
    private String url;
    private String publishedOn;
    private String description;

    // Конструктор по умолчанию
    public News() {}

    public News(String title, String url, String publishedOn, String description) {
        this.title = title;
        this.url = url;
        this.publishedOn = publishedOn;
        this.description = description;
    }

    // Геттеры и сеттеры
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(String publishedOn) {
        this.publishedOn = publishedOn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}