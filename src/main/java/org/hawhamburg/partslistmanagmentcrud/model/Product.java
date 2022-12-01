package org.hawhamburg.partslistmanagmentcrud.model;

import java.util.ArrayList;
import java.util.List;

public class Product extends Component {

    private final List<Component> parts;

    public Product(java.lang.String name, Integer price) {
        super(name, price);
        this.parts = new ArrayList<>();
    }

    @Override
    public boolean contains(Component component) {
        if (this.equals(component)) return true;
        return this.parts.stream().anyMatch(p -> p.contains(component));
    }

    public void addPart(Component part, Integer amount) throws CyclicStructureException {
        if (part.contains(this)) throw new CyclicStructureException();
        for (int i = 0; i < amount; i++) {
            this.parts.add(part);
        }
    }

    @Override
    public Integer fetchTotalPrice() {
        return this.price + this.parts.stream().mapToInt(Component::fetchTotalPrice).sum();
    }

    @Override
    public List<Material> fetchMaterialList() {
        return parts.stream().map(Component::fetchMaterialList).flatMap(List::stream).toList();
    }

    @Override
    public java.lang.String getType() {
        return "Product";
    }
}
