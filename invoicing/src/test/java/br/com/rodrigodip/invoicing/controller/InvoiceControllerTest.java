package br.com.rodrigodip.invoicing.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.com.rodrigodip.invoicing.bucket.BucketService;

@ExtendWith(MockitoExtension.class)
class InvoiceControllerTest {

    @Mock
    private BucketService bucketService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new InvoiceController(bucketService)).build();
    }

    @Test
    void redirectsToPresignedUrlWhenInvoiceExists() throws Exception {
        when(bucketService.exists("invoices/42.pdf")).thenReturn(true);
        when(bucketService.generatePresignedUrl("invoices/42.pdf"))
                .thenReturn("http://localhost:9000/order-invoice/invoices/42.pdf?signature=abc");

        mockMvc.perform(get("/invoices/42/download"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location",
                        "http://localhost:9000/order-invoice/invoices/42.pdf?signature=abc"));
    }

    @Test
    void returnsNotFoundWhenInvoiceDoesNotExist() throws Exception {
        when(bucketService.exists("invoices/42.pdf")).thenReturn(false);

        mockMvc.perform(get("/invoices/42/download"))
                .andExpect(status().isNotFound());
    }
}
