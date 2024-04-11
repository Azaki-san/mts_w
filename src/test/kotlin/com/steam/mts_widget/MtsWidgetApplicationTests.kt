package com.steam.mts_widget

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.equalTo
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
class MtsWidgetApplicationTests {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `test getBillLink endpoint with valid parameters`() {
        mockMvc.perform(
            get("/bill")
                .param("priceWithoutFee", "350")
                .param("username", "the_jack_zaka")
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `test getBillLink endpoint with invalid parameters`() {
        val expectedResponseBody =
            "Parameter 'priceWithoutFee' must be a valid integer. Parameter 'username' must be a valid string."
        mockMvc.perform(
            get("/bill")
                .param("priceWithoutFee", "abc")
                .param("username", "123фыas")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `test getBillLink endpoint without priceWithoutFee`() {
        val expectedResponseBody = "Parameter 'priceWithoutFee' must be a valid integer."
        mockMvc.perform(
            get("/bill")
                .param("username", "123")
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(containsString(expectedResponseBody)))
    }

    @Test
    fun `test getBillLink endpoint without priceWithoutUsername`() {
        val expectedResponseBody = "Parameter 'username' must be a valid string."
        mockMvc.perform(
            get("/bill")
                .param("priceWithoutFee", "355")
        )
            .andExpect(status().isBadRequest)
            .andExpect(content().string(containsString(expectedResponseBody)))
    }

    @Test
    fun `test getBillLink endpoint without everything`() {
        val expectedResponseBody =
            "Parameter 'priceWithoutFee' must be a valid integer. Parameter 'username' must be a valid string."

        mockMvc.perform(get("/bill"))
            .andExpect(status().isBadRequest)
            .andExpect(content().string(containsString(expectedResponseBody)))
    }

    @Test
    fun `test help endpoint`() {
        val expectedResponseBody = "©2024 Techaas. All rights reserved"

        mockMvc.perform(get("/help"))
            .andExpect(status().isOk)
            .andExpect(content().string(containsString(expectedResponseBody)))
    }

    @Test
    fun `test games endpoint`() {
        mockMvc.perform(get("/games"))
            .andExpect(status().isOk)
    }

//    @Test
//    fun `test game endpoint`() {
//        mockMvc.perform(get("/game")
//            .param("gameId", "57690"))
//            .andExpect(status().isOk)
//            .andExpect(jsonPath("$.name", equalTo("Tropico 4")))
//            .andExpect(jsonPath("$.steam_appid", equalTo("57690")))
//    }


}
