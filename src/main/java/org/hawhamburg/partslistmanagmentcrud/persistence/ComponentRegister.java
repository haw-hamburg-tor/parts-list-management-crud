package org.hawhamburg.partslistmanagmentcrud.persistence;

import org.hawhamburg.partslistmanagmentcrud.model.Component;
import org.hawhamburg.partslistmanagmentcrud.model.CyclicStructureException;
import org.hawhamburg.partslistmanagmentcrud.model.Material;
import org.hawhamburg.partslistmanagmentcrud.model.Product;

import java.util.*;
import java.util.stream.Stream;

public class ComponentRegister {

    private static ComponentRegister instance = null;
    private final Map<String, Material> materials;
    private final Map<String, Product> products;

    private ComponentRegister() {
        this.products = new LinkedHashMap<>();
        this.materials = new LinkedHashMap<>();
    }

    public static ComponentRegister getInstance() {
        if (instance == null) instance = new ComponentRegister();
        return instance;
    }

    public Material createMaterial(String name, Integer price) {
        var material = new Material(name, price);
        addMaterial(material);
        return material;
    }

    public Product createProduct(String name, Integer price, List<String> componentNames, List<Integer> componentAmounts) {
        try {
            var product = new Product(name, price);
            for (int i = 0; i < componentNames.size(); i++) {
                Component component = getComponent(componentNames.get(i));
                product.addPart(component, componentAmounts.get(i));
            }
            addProduct(product);
            return product;

        } catch (CyclicStructureException e) {
            return null;
        }
    }

    private void addMaterial(Material material) {
        materials.put(material.getName(), material);
    }

    public Material getMaterial(String name) {
        return materials.get(name);
    }

    public List<Material> getMaterials() {
        return new ArrayList<>(materials.values());
    }

    public Boolean containsMaterial(String name) {
        return materials.containsKey(name);
    }

    public Material removeMaterial(String name) {
        return materials.remove(name);
    }

    private void addProduct(Product product) {
        products.put(product.getName(), product);
    }

    public Product getProduct(String name) {
        return products.get(name);
    }

    public List<String> getProductNames() {
        return products.keySet().stream().sorted().toList();
    }

    public Component getComponent(String name) {
        var product = products.get(name);
        return product != null ? product : materials.get(name);
    }

    public List<Component> getComponents() {
        return Stream.concat(materials.values().stream(), products.values().stream())
                .sorted(Comparator.comparing(Component::getName)).toList();
    }

    public List<String> getComponentNames() {
        return Stream.concat(materials.keySet().stream(), products.keySet().stream()).sorted().toList();
    }
}
