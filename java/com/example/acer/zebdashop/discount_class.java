package com.example.acer.zebdashop;

/**
 * Created by acer on 1/20/2018.
 */

public class discount_class {
    String discount_id;
    String name;
    String desc;
    String price;
    String img_URI;

    public discount_class(String name, String desc, String price, String img_URI, String discount_id) {
        this.discount_id = discount_id;
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.img_URI = img_URI;
    }
public discount_class(){

}

    public String getDiscount_id() {
        return discount_id;
    }

    public void setDiscount_id(String discount_id) {
        this.discount_id = discount_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImg_URI() {
        return img_URI;
    }

    public void setImg_URI(String img_URI) {
        this.img_URI = img_URI;
    }
}
