package io.relevantbox.android.event;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;

import io.relevantbox.android.model.ecommerce.Order;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EcommerceEventProcessorHandlerTest {

    @InjectMocks
    private EcommerceEventProcessorHandler ecommerceEventProcessorHandler;

    @Mock
    private EventProcessorHandler eventProcessorHandler;

    @Test
    public void it_should_create_product_view_event() {

        ArgumentCaptor<Map<String, Object>> eventArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ecommerceEventProcessorHandler.productView(
                "productId", "small", 400d,
                300d, "USD", "supplier1",
                "resolvedPath"
        );
        verify(eventProcessorHandler).pageView(eq("productDetail"), eventArgumentCaptor.capture());

        Map<String, Object> params = eventArgumentCaptor.getValue();

        assertEquals("products", params.get("entity"));
        assertEquals("productId", params.get("id"));
        assertEquals("small", params.get("variant"));
        assertEquals(400d, params.get("price"));
        assertEquals(300d, params.get("discountedPrice"));
        assertEquals("USD", params.get("currency"));
        assertEquals("supplier1", params.get("supplierId"));
        assertEquals("resolvedPath", params.get("path"));
    }

    @Test
    public void it_should_create_category_view_event() {
        ArgumentCaptor<Map<String, Object>> eventArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ecommerceEventProcessorHandler.categoryView(
                "categoryId", "resolvedPath"
        );

        verify(eventProcessorHandler).pageView(eq("categoryPage"), eventArgumentCaptor.capture());
        Map<String, Object> params = eventArgumentCaptor.getValue();

        assertEquals("categories", params.get("entity"));
        assertEquals("categoryId", params.get("id"));
        assertEquals("resolvedPath", params.get("path"));

    }

    @Test
    public void it_should_create_search_view_event() {
        ArgumentCaptor<Map<String, Object>> eventArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ecommerceEventProcessorHandler.searchResult(
                "search text", 300, "resolvedPath"
        );

        verify(eventProcessorHandler).pageView(eq("searchPage"), eventArgumentCaptor.capture());
        Map<String, Object> params = eventArgumentCaptor.getValue();

        assertEquals("search text", params.get("keyword"));
        assertEquals(300, params.get("resultCount"));
        assertEquals("resolvedPath", params.get("path"));

    }

    @Test
    public void it_should_create_add_to_cart_event() {
        ArgumentCaptor<Map<String, Object>> eventArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ecommerceEventProcessorHandler.addToCart(
                "productId", "variantId", 3, 300d,
                250d, "USD", "detailPage", "basketId",
                "supplierId"
        );

        verify(eventProcessorHandler).actionResult(eq("addToCart"), eventArgumentCaptor.capture());
        Map<String, Object> params = eventArgumentCaptor.getValue();

        assertEquals("productId", params.get("id"));
        assertEquals("products", params.get("entity"));
        assertEquals("variantId", params.get("variant"));
        assertEquals(3, params.get("quantity"));
        assertEquals(300d, params.get("price"));
        assertEquals(250d, params.get("discountedPrice"));
        assertEquals("USD", params.get("currency"));
        assertEquals("detailPage", params.get("origin"));
        assertEquals("basketId", params.get("basketId"));
        assertEquals("supplierId", params.get("supplierId"));
    }

    @Test
    public void it_should_create_remove_from_cart_event() {
        ArgumentCaptor<Map<String, Object>> eventArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ecommerceEventProcessorHandler.removeFromCart(
                "productId", "variantId", 3, "basketId"
        );

        verify(eventProcessorHandler).actionResult(eq("removeFromCart"), eventArgumentCaptor.capture());
        Map<String, Object> params = eventArgumentCaptor.getValue();

        assertEquals("productId", params.get("id"));
        assertEquals("products", params.get("entity"));
        assertEquals("variantId", params.get("variant"));
        assertEquals(3, params.get("quantity"));
        assertEquals("basketId", params.get("basketId"));
    }

    @Test
    public void it_should_create_cart_view_page() {
        ArgumentCaptor<Map<String, Object>> eventArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ecommerceEventProcessorHandler.cartView(
                "basketId"
        );

        verify(eventProcessorHandler).pageView(eq("cartView"), eventArgumentCaptor.capture());
        Map<String, Object> params = eventArgumentCaptor.getValue();
        assertEquals("basketId", params.get("basketId"));
    }

    @Test
    public void it_should_create_order_funnel_event() {
        ArgumentCaptor<Map<String, Object>> eventArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ecommerceEventProcessorHandler.orderFunnel(
                "basketId", "addressPage"
        );

        verify(eventProcessorHandler).pageView(eq("orderFunnel"), eventArgumentCaptor.capture());
        Map<String, Object> params = eventArgumentCaptor.getValue();
        assertEquals("basketId", params.get("basketId"));
        assertEquals("addressPage", params.get("step"));
    }

    @Test
    public void it_should_create_order_success_events() {
        ArgumentCaptor<Map<String, Object>> pageViewEventArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<Map<String, Object>> conversionArgumentCaptor = ArgumentCaptor.forClass(Map.class);

        Order order = Order.create("orderId")
                .addItem("product1", "variant1", 3, 300d, 200d, "USD", "supplier1")
                .addItem("product2", "variant2", 1, 100d, 76d, "USD", "supplier2")
                .addItem("product3", "variant3", 4, 1300d, 1200d, "USD", "supplier3")
                .paidWith("creditCard")
                .totalAmount(3220d)
                .discountAmount(2000d)
                .withPromotion("promotionName")
                .withDiscount("discountName")
                .withCoupon("couponName");


        ecommerceEventProcessorHandler.orderSuccess(
                "basketId", order
        );

        verify(eventProcessorHandler).pageView(eq("orderSuccess"), pageViewEventArgumentCaptor.capture());
        Map<String, Object> params = pageViewEventArgumentCaptor.getValue();
        assertEquals("basketId", params.get("basketId"));
        assertEquals("orderId", params.get("orderId"));
        assertEquals(3220d, params.get("totalAmount"));
        assertEquals(2000d, params.get("discountAmount"));
        assertEquals("discountName", params.get("discountName"));
        assertEquals("couponName", params.get("couponName"));
        assertEquals("promotionName", params.get("promotionName"));
        assertEquals("creditCard", params.get("paymentMethod"));

        verify(eventProcessorHandler, times(3)).actionResult(eq("conversion"), conversionArgumentCaptor.capture());
        List<Map<String, Object>> conversionParams = conversionArgumentCaptor.getAllValues();

        Map<String,Object> conversion1Params = conversionParams.get(0);
        assertEquals("product1", conversion1Params.get("id"));
        assertEquals("products", conversion1Params.get("entity"));
        assertEquals("variant1", conversion1Params.get("variant"));
        assertEquals("orderId", conversion1Params.get("orderId"));
        assertEquals(3, conversion1Params.get("quantity"));
        assertEquals(300d, conversion1Params.get("price"));
        assertEquals(200d, conversion1Params.get("discountedPrice"));
        assertEquals("USD", conversion1Params.get("currency"));
        assertEquals("orderId", conversion1Params.get("orderId"));
        assertEquals("supplier1", conversion1Params.get("supplierId"));

        Map<String,Object> conversion2Params = conversionParams.get(1);
        assertEquals("product2", conversion2Params.get("id"));
        assertEquals("products", conversion2Params.get("entity"));
        assertEquals("variant2", conversion2Params.get("variant"));
        assertEquals("orderId", conversion2Params.get("orderId"));
        assertEquals(1, conversion2Params.get("quantity"));
        assertEquals(100d, conversion2Params.get("price"));
        assertEquals(76d, conversion2Params.get("discountedPrice"));
        assertEquals("USD", conversion2Params.get("currency"));
        assertEquals("orderId", conversion2Params.get("orderId"));
        assertEquals("supplier2", conversion2Params.get("supplierId"));

        Map<String,Object> conversion3Params = conversionParams.get(2);
        assertEquals("product3", conversion3Params.get("id"));
        assertEquals("products", conversion3Params.get("entity"));

        assertEquals("variant3", conversion3Params.get("variant"));
        assertEquals("orderId", conversion3Params.get("orderId"));
        assertEquals(4, conversion3Params.get("quantity"));
        assertEquals(1300d, conversion3Params.get("price"));
        assertEquals(1200d, conversion3Params.get("discountedPrice"));
        assertEquals("USD", conversion3Params.get("currency"));
        assertEquals("orderId", conversion3Params.get("orderId"));
        assertEquals("supplier3", conversion3Params.get("supplierId"));
    }
}
