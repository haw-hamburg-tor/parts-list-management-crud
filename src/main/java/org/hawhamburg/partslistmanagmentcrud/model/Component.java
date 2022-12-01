package org.hawhamburg.partslistmanagmentcrud.model;

import java.util.List;

public abstract class Component {

    protected final String name;
    protected final Integer price;

    public Component(String name, Integer price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Component component)) return false;
        return name.equals(component.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + price.hashCode();
        return result;
    }

    public abstract boolean contains(Component component);

    public abstract Integer fetchTotalPrice();

    public abstract List<Material> fetchMaterialList();

    public abstract java.lang.String getType();

    @Override
    public java.lang.String toString() {
        return getType() + "{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
