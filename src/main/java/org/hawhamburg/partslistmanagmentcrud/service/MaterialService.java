package org.hawhamburg.partslistmanagmentcrud.service;

import org.apache.commons.lang3.tuple.Pair;
import org.hawhamburg.partslistmanagmentcrud.model.Material;
import org.hawhamburg.partslistmanagmentcrud.persistence.ComponentRegister;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialService {
    public List<Material> fetchAllMaterials() {
        return ComponentRegister.getInstance().getMaterials();
    }

    public Material fetchMaterial(String materialName) {
        return ComponentRegister.getInstance().getMaterial(materialName);
    }

    public Material createNewMaterial(String name, Integer price) throws MaterialAlreadyExistsException {
        if(ComponentRegister.getInstance().getMaterial(name) != null) {
            throw new MaterialAlreadyExistsException();
        }
        return ComponentRegister.getInstance().createMaterial(name, price);
    }

    public Pair<Boolean, Material> createOrReplaceMaterial(String name, Integer price) {
        Boolean replaced = ComponentRegister.getInstance().containsMaterial(name);
        return Pair.of(replaced, ComponentRegister.getInstance().createMaterial(name, price));
    }

    public Material removeMaterial(String name) {
        return ComponentRegister.getInstance().removeMaterial(name);
    }
}
