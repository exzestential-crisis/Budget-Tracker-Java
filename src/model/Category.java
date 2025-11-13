package model;

public class Category {
    private String name;
    private String iconPath;

    public Category(String name, String iconPath) {
        this.name = name;
        this.iconPath = iconPath;
    }

    public String getName() {
        return name;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }
}
