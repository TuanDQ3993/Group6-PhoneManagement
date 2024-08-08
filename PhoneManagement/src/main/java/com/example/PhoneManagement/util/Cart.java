package com.example.PhoneManagement.util;

import com.example.PhoneManagement.entity.Products;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart {
    List<Item> items;

    public Cart() {
        items = new ArrayList<Item>();
    }

    public int getQuantityById(int id){
        return getItemById(id).getQuantity();
    }

    public Item getItemById(int id) {
        for(Item i : items) {
            if(i.getProductColor().getProductcolorId()==id) {
                return i;
            }
        }
        return null;
    }

    public void addItem(Item item) {
        if(getItemById(item.getProductColor().getProductcolorId()) != null) {
            Item m=getItemById(item.getProductColor().getProductcolorId());
            m.setQuantity(m.getQuantity()+ item.getQuantity());
        }
        else{
            items.add(item);
        }
    }

    public void removeItem(int id) {
        if(getItemById(id)!=null){
            items.remove(getItemById(id));
        }
    }

    public double getTotalPrice() {
        double total = 0;
        for(Item i : items) {
            total += i.getPrice()*i.getQuantity();
        }
        return total;
    }

    public Products getProductById(int id,List<Products> list) {
        for (Products i : list) {
            if (i.getProductId() == id) {
                return i;
            }
        }
        return null;
    }


}
