package org.hawhamburg.partslistmanagmentcrud.unit.model;

import org.hawhamburg.partslistmanagmentcrud.model.CyclicStructureException;
import org.hawhamburg.partslistmanagmentcrud.model.Material;
import org.hawhamburg.partslistmanagmentcrud.model.Product;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ComponentTests {

    @Test
    public void fetchPriceMaterial() {
        // Arrange
        var m1 = new Material("m1", 5);
        var m2 = new Material("m2", 10);

        // Act & Assert
        assertEquals(5, m1.fetchTotalPrice());
        assertEquals(10, m2.fetchTotalPrice());
    }

    @Test
    public void fetchPriceEmptyProduct() {
        // Arrange
        var p1 = new Product("p1", 2);
        var p2 = new Product("p2", 4);

        // Act & Assert
        assertEquals(2, p1.fetchTotalPrice());
        assertEquals(4, p2.fetchTotalPrice());
    }

    @Test
    public void fetchPriceProductWithOneMaterial() throws CyclicStructureException {
        // Arrange
        var p1 = new Product("p1", 2);
        var m1 = new Material("m1", 5);

        p1.addPart(m1, 10);

        // Act & Assert
        assertEquals((10 * 5) + 2, p1.fetchTotalPrice());
    }

    @Test
    public void fetchPriceProductWithTwoMaterials() throws CyclicStructureException {
        // Arrange
        var p1 = new Product("p1", 2);
        var m1 = new Material("m1", 5);
        var m2 = new Material("m2", 10);

        p1.addPart(m1, 4);
        p1.addPart(m2, 3);

        // Act & Assert
        assertEquals((4 * 5) + (3 * 10) + 2, p1.fetchTotalPrice());
    }

    @Test
    public void fetchPriceProductWithOneSubProduct() throws CyclicStructureException {
        // Arrange
        var p1 = new Product("p1", 2);
        var p2 = new Product("p2", 5);
        var m1 = new Material("m1", 5);
        var m2 = new Material("m2", 10);
        var m3 = new Material("m3", 1);

        p1.addPart(p2, 2);
        p1.addPart(m3, 100);

        p2.addPart(m1, 10);
        p2.addPart(m2, 2);

        // Act & Assert
        assertEquals((10 * 5) + (2 * 10) + 5, p2.fetchTotalPrice());
        assertEquals((2 * 75) + (100 * 1) + 2, p1.fetchTotalPrice());
    }

    @Test
    public void fetchPriceProductWithMultipleSubProducts() throws CyclicStructureException {
        // Arrange
        var p1 = new Product("p1", 2);
        var p2 = new Product("p2", 5);
        var p3 = new Product("p3", 10);
        var p4 = new Product("p4", 1);
        var m1 = new Material("m1", 5);
        var m2 = new Material("m2", 10);
        var m3 = new Material("m3", 1);
        var m4 = new Material("m4", 15);

        p1.addPart(p2, 2);
        p1.addPart(p3, 1);
        p1.addPart(m3, 5);

        p2.addPart(m1, 10);
        p2.addPart(p4, 3);

        p3.addPart(m1, 3);
        p3.addPart(m2, 5);
        p3.addPart(p4, 2);

        p4.addPart(m1, 2);
        p4.addPart(m4, 1);

        // Act & Assert
        var p4TotalPrice = p4.fetchTotalPrice();
        assertEquals((2 * m1.getPrice()) + (1 * m4.getPrice()) + p4.getPrice(), p4TotalPrice);
        var p3TotalPrice = p3.fetchTotalPrice();
        assertEquals((3 * m1.getPrice()) + (5 * m2.getPrice()) + (2 * p4TotalPrice) + p3.getPrice(), p3TotalPrice);
        var p2TotalPrice = p2.fetchTotalPrice();
        assertEquals((10 * 5) + (3 * p4TotalPrice) + p2.getPrice(), p2TotalPrice);
        var p1TotalPrice = p1.fetchTotalPrice();
        assertEquals((2 * p2TotalPrice) + (1 * p3TotalPrice) + (5 * m3.getPrice()) + p1.getPrice(), p1TotalPrice);
    }

    @Test
    public void fetchMaterialListProductWithMultipleSubProducts() throws CyclicStructureException {
        // Arrange
        var p1 = new Product("p1", 2);
        var p2 = new Product("p2", 5);
        var p3 = new Product("p3", 10);
        var p4 = new Product("p4", 1);
        var m1 = new Material("m1", 5);
        var m2 = new Material("m2", 10);
        var m3 = new Material("m3", 1);
        var m4 = new Material("m4", 15);

        p1.addPart(p2, 2);
        p1.addPart(p3, 1);
        p1.addPart(m3, 5);

        p2.addPart(m1, 10);
        p2.addPart(p4, 3);

        p3.addPart(m1, 3);
        p3.addPart(m2, 5);
        p3.addPart(p4, 2);

        p4.addPart(m1, 2);
        p4.addPart(m4, 1);

        var m1ExpectedResult = Collections.singletonList(m1);
        var m2ExpectedResult = Collections.singletonList(m2);
        var m3ExpectedResult = Collections.singletonList(m3);
        var m4ExpectedResult = Collections.singletonList(m4);

        var p4ExpectedResult = List.of(m1, m1, m4);
        var p3ExpectedResult = Stream.of(List.of(m1, m1, m1, m2, m2, m2, m2, m2), p4ExpectedResult, p4ExpectedResult).flatMap(Collection::stream).toList();
        var p2ExpectedResult = Stream.of(List.of(m1, m1, m1, m1, m1, m1, m1, m1, m1, m1), p4ExpectedResult, p4ExpectedResult, p4ExpectedResult).flatMap(Collection::stream).toList();
        var p1ExpectedResult = Stream.of(p2ExpectedResult, p2ExpectedResult, p3ExpectedResult, List.of(m3, m3, m3, m3, m3)).flatMap(Collection::stream).toList();


        // Act & Assert
        assertEquals(m1ExpectedResult, m1.fetchMaterialList());
        assertEquals(m2ExpectedResult, m2.fetchMaterialList());
        assertEquals(m3ExpectedResult, m3.fetchMaterialList());
        assertEquals(m4ExpectedResult, m4.fetchMaterialList());

        assertEquals(p4ExpectedResult, p4.fetchMaterialList());
        assertEquals(p3ExpectedResult, p3.fetchMaterialList());
        assertEquals(p2ExpectedResult, p2.fetchMaterialList());
        assertEquals(p1ExpectedResult, p1.fetchMaterialList());
    }

    @Test
    public void addPartReflexiveCycle() {
        // Arrange
        var p1 = new Product("p1", 1);

        // Act & Assert
        assertThrows(CyclicStructureException.class, () -> p1.addPart(p1, 1));
    }

    @Test
    public void addPartDirectCycle() throws CyclicStructureException {
        // Arrange
        var p1 = new Product("p1", 1);
        var p2 = new Product("p2", 2);
        p1.addPart(p2, 1);

        // Act & Assert
        assertThrows(CyclicStructureException.class, () -> p2.addPart(p1, 1));
    }

    @Test
    public void addPartTransitiveCycle() throws CyclicStructureException {
        // Arrange
        var p1 = new Product("p1", 1);
        var p2 = new Product("p2", 2);
        var p3 = new Product("p3", 3);
        p1.addPart(p2, 1);
        p2.addPart(p3, 1);

        // Act & Assert
        assertThrows(CyclicStructureException.class, () -> p3.addPart(p1, 1));
    }
}
