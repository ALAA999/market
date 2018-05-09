package com.example.acer.zebdashop;

/**
 * Created by acer on 1/20/2018.
 */

public class product_class {
    String product_id;
    String name;
    String desc;
    String price;
    String img_URI;

    public product_class(String name, String desc, String price, String img_URI,String product_id) {
        this.name = name;
        this.desc = desc;
        this.price = price;
        this.img_URI = img_URI;
        this.product_id = product_id;
    }
    public product_class(){

    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
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
