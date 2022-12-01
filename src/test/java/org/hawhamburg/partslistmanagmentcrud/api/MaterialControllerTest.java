package org.hawhamburg.partslistmanagmentcrud.api;

import org.hawhamburg.partslistmanagmentcrud.controller.MaterialController;
import org.hawhamburg.partslistmanagmentcrud.controller.dto.ApiError;
import org.hawhamburg.partslistmanagmentcrud.controller.dto.MaterialDTO;
import org.hawhamburg.partslistmanagmentcrud.model.Material;
import org.hawhamburg.partslistmanagmentcrud.persistence.ComponentRegister;
import org.hawhamburg.partslistmanagmentcrud.service.MaterialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.net.URI;
import java.util.List;

import static org.hawhamburg.partslistmanagmentcrud.controller.MaterialController.toMaterialDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class MaterialControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<MaterialDTO> materialDTOJsonHandler;
    @Autowired
    private JacksonTester<List<MaterialDTO>> listOfMaterialDTOJsonHandler;
    @Autowired
    private JacksonTester<ApiError> apiErrorJsonHandler;

    private final Material m1 = new Material("m1", 1);
    private final Material m2 = new Material("m2", 2);
    private final Material m3 = new Material("m3", 3);

    private final List<Material> materials = List.of(m1, m2, m3);


    @BeforeEach
    public void setup() {
        ComponentRegister.getInstance().clear();
    }

    @Test
    public void getAllMaterialsOK() throws Exception {
        //Arrange
        ComponentRegister.getInstance().createMaterial(m1.getName(), m1.getPrice());
        ComponentRegister.getInstance().createMaterial(m2.getName(), m2.getPrice());
        ComponentRegister.getInstance().createMaterial(m3.getName(), m3.getPrice());
        List<MaterialDTO> expectedResponseBody = materials.stream().map(MaterialController::toMaterialDTO).toList();

        //Act
        MvcResult mvcResult = mvc.perform(get(new URI("/materials"))).andReturn();

        //Assert
        HttpStatus responseCode = HttpStatus.valueOf(mvcResult.getResponse().getStatus());
        List<MaterialDTO> actualResponseBody = listOfMaterialDTOJsonHandler.parse(mvcResult.getResponse().getContentAsString()).getObject();

        assertEquals(HttpStatus.OK, responseCode);
        assertEquals(expectedResponseBody, actualResponseBody);
    }

    @Test
    public void getMaterialOK() throws Exception {
        //Arrange
        ComponentRegister.getInstance().createMaterial(m1.getName(), m1.getPrice());
        MaterialDTO expectedResponseBody = toMaterialDTO(m1);

        //Act
        MvcResult mvcResult = mvc.perform(get(new URI("/materials/" + m1.getName()))).andReturn();

        //Assert
        HttpStatus responseCode = HttpStatus.valueOf(mvcResult.getResponse().getStatus());
        MaterialDTO actualResponseBody = materialDTOJsonHandler.parse(mvcResult.getResponse().getContentAsString()).getObject();

        assertEquals(HttpStatus.OK, responseCode);
        assertEquals(expectedResponseBody, actualResponseBody);
    }

    @Test
    public void getMaterialNotFound() throws Exception {
        //Act
        MvcResult mvcResult = mvc.perform(get(new URI("/materials/" + m1.getName()))) .andReturn();

        //Assert
        HttpStatus responseCode = HttpStatus.valueOf(mvcResult.getResponse().getStatus());
        String actualResponseBodyAsString = mvcResult.getResponse().getContentAsString();

        assertEquals(HttpStatus.NOT_FOUND, responseCode);
        assertTrue(actualResponseBodyAsString.isEmpty());
    }

    @Test
    public void createNewMaterialCreated() throws Exception {
        //Arrange
        MaterialDTO m1DTO = toMaterialDTO(m1);

        //Act
        MvcResult mvcResult = mvc.perform(post(new URI("/materials/"))
                .content(materialDTOJsonHandler.write(m1DTO).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        //Assert
        HttpStatus responseCode = HttpStatus.valueOf(mvcResult.getResponse().getStatus());
        MaterialDTO actualResponseBody = materialDTOJsonHandler.parse(mvcResult.getResponse().getContentAsString()).getObject();

        assertEquals(HttpStatus.CREATED, responseCode);
        assertEquals(m1DTO, actualResponseBody);
    }

    @Test
    public void createNewMaterialConflict() throws Exception {
        //Arrange
        ComponentRegister.getInstance().createMaterial(m1.getName(), m1.getPrice());
        MaterialDTO m1DTO = toMaterialDTO(m1);
        var expectedError = new ApiError("Material already exists");

        //Act
        MvcResult mvcResult = mvc.perform(post(new URI("/materials/"))
                .content(materialDTOJsonHandler.write(m1DTO).getJson())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andReturn();

        //Assert
        HttpStatus responseCode = HttpStatus.valueOf(mvcResult.getResponse().getStatus());
        ApiError actualResponseBody = apiErrorJsonHandler.parse(mvcResult.getResponse().getContentAsString()).getObject();

        assertEquals(HttpStatus.CONFLICT, responseCode);
        assertEquals(expectedError, actualResponseBody);
    }

    @Test
    public void createOrReplaceMaterialOK() throws Exception {
        //Arrange
        ComponentRegister.getInstance().createMaterial(m1.getName(), m1.getPrice());
        MaterialDTO updatedM1DTO = new MaterialDTO(m1.getName(), m1.getPrice() + 1);

        //Act

        //Assert
    }

    @Test
    public void createOrReplaceMaterialCreated() throws Exception {
        //Arrange
        MaterialDTO m1DTO = toMaterialDTO(m1);

        //Act

        //Assert
    }

    @Test
    public void createOrReplaceMaterialBadRequest() throws Exception {
        //Arrange
        MaterialDTO m1DTO = toMaterialDTO(m1);

        //Act

        //Assert
    }

    @Test
    public void deleteMaterialNoContent() throws Exception {
        //Arrange

        //Act

        //Assert
    }

    @Test
    public void deleteMaterialNotFound() throws Exception {
        //Arrange

        //Act

        //Assert
    }


    // The following code may demonstrate how to use Mocks to manipulate a Components behavior.
    // Except for the Arrange part it is the same test case as getMaterialNotFound() above.
//    @MockBean
//    MaterialService materialService;
//
//    @Test
//    public void getMaterialNotFoundButWithMock() throws Exception {
//        //Arrange
//        when(materialService.fetchMaterial(anyString())).thenReturn(m1);
//
//        //Act
//        MvcResult mvcResult = mvc.perform(get(new URI("/materials/" + m1.getName()))) .andReturn();
//
//        //Assert
//        verify(materialService).fetchMaterial(anyString());
//
//        HttpStatus responseCode = HttpStatus.valueOf(mvcResult.getResponse().getStatus());
//        String actualResponseBodyAsString = mvcResult.getResponse().getContentAsString();
//
//        assertEquals(HttpStatus.NOT_FOUND, responseCode);
//        assertTrue(actualResponseBodyAsString.isEmpty());
//    }

}
