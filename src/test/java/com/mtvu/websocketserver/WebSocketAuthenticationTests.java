package com.mtvu.websocketserver;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.mtvu.websocketserver.domain.Message;
import com.mtvu.websocketserver.utility.LoggingStompSessionHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.util.Collections;
import java.util.concurrent.LinkedBlockingDeque;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author mvu
 * @project chat-socket
 **/
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketAuthenticationTests {
    private static final String JWKS_RESPONSE = "{\"keys\":[{\"kty\":\"RSA\",\"e\":\"AQAB\",\"use\":\"sig\",\"kid\":\"one\",\"n\":\"i7H90yfquGVhXxekdzXkMaxlIg67Q_ofd7iuFHtgeUx-Iye2QjukuULhl774oITYnZIZsh2UHxRYG8nFypcYZfHJMQes_OYFTkTvRroKll5p3wxSkhpARbkEPFMyMJ5WIm3MeNO2ermMhDWVVeI2xQH-tW6w-C6b5d_F6lrIwCnpZwSv6PQ3kef-rcObp_PZANIo232bvpwyC6uW1W2kpjAvYJhQ8NrkG2oO0ynqEJW2UyoCWRdsT2BLZcFMAcxG3Iw9b9__IbvNoUBwr596JYfzrX0atiKyk4Yg8dJ1wBjHFN2fkHTlzn6HDwTJkj4VNDQvKu4P2zhKn1gmWuxhuQ\"}]}";
    private final String messageReadToken = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAtMjU2In0.CRBAEgvQhpB6pPQhpkTAKpsDai1FDcvkDSRig1R3OI-g18JdTe-qDhzWwP-hV3aCwFwHxQ_g8Z8OIZvhyKpQaPwBb72UeLqqhzSIkm0gEsmmjYg1vEGOrDH5_Fqlm0LnAnXTmsbOIWYIj11ZuenI2lEmMCkVwqth0RlzakdcHRXiuDTEln_trhZpE2j80X-9rS2gZy9Raa9VLir3-F3wC0GKPEL6e3x1OygC03ix9uyXS3vpTsU9zlgoYADZyaLeOF1mCG4mQhvXs7IPmPbwNsElJwKh0xSQCHvNOQShprlvd3cHiUFKYw9fXphY1O-AUYcRzHk4DjoBdkGNQMy_Kw.KtC_z674rYBtDgkN.e8QU50Iq1JHkn-1USSxpjEkbrukb4cobvlQRK40iXGAKVIuOod4bSq5fDpIAPHugqIf-_zGsvr-2OCOdzhtBikL46wU7UdZppxPWtk-X6kl33zH_XObRMaGfe-hLxt3RPxRVn7I1Hp6tGW1Rkxyf_ESq4XlcbbrkhDoIz_G_LKXJhvQ-xahW2e0AUc7RZSucns4XUeq9xX_dd7Ht-o1TmQI9WFoFc1l7oh9GtQ6GZMsghnZ1VrbIS2L7jSYiSsD2JqSv1LLtOGj_FBA0ufhqM3LloGiwflEwAryMD10oNb73WonKEycEj1rBsTIKW7SHkI-VkrQA4-8N-aLWgHwDnzyPZmyNyKpqUMvhjIE_0w6oqU4HpN7J5nfBEIAtpPZ_pDkwAdxCQ7JV3zfiUnF7ZQ3q1PnSId315si02ZN9-wRSrMHcHnboQN1Hs4xCAfGyClVyLpCzfa_fAehjt6v1DjgjbzwSjr_LdNmWTvXYBhNO8Jq9Vb7axksrdwksD3pYNMY8cRZxP-LO0V5Sv1_kT_Hf2yLo2iTwB8n8szzGrJ4QQLb5Znu7Sv-M2x52cnIDMiorP3LNpFk.G85FuMSm-8bGumFAStiFQA";

    @LocalServerPort
    private int rdmServerPort;
    private static WireMockServer mockServer;
    private WebSocketStompClient stompClient;
    private StompSession session;

    @BeforeEach
    public void setupMockServer() {
        mockServer = new WireMockServer(wireMockConfig().port(8081));
        mockServer.start();
        mockServer.stubFor(get(urlEqualTo("/.well-known/jwks.json"))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(JWKS_RESPONSE)
                .withHeader("Content-Type", "application/json")));

        var webSocketClient = new StandardWebSocketClient();
        var sockJsClient = new SockJsClient(Collections.singletonList(new WebSocketTransport(webSocketClient)));
        stompClient = new WebSocketStompClient(sockJsClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @AfterEach
    public void tearDownServer() {
        mockServer.stop();
        if (session != null) {
            session.disconnect();
        }
    }

    @Test
    void performWhenNoBearerTokenThenDeniesAccess() {
        try {
            System.out.print("Current port: " + rdmServerPort);
            session = stompClient.connect(String.format("ws://localhost:%d/websocket", rdmServerPort),
                    new StompSessionHandlerAdapter(){}).get(5, SECONDS);
            Assert.isTrue(false, "An exception should be thrown before reaching this line");
        } catch (Exception ex) {
            String message = ex.getMessage();
            Assert.isTrue(message.contains("Connection closed"), "Connection should be closed due to AccessDenied");
        }
    }

    @Test
    void performWhenHasValidBearerTokenThenAllowsAccess() throws Exception {
        var handshakeHeaders = new WebSocketHttpHeaders();
        var connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + messageReadToken);
        var message = "myMessage";
        var receivedMessages = new LinkedBlockingDeque<Message>();
        session = stompClient.connect(String.format("ws://localhost:%d/websocket", rdmServerPort), handshakeHeaders,
            connectHeaders, new LoggingStompSessionHandler(Message.class) {
                @Override
                public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                    session.subscribe("/topic/message", this);
                    session.send("/app/message", new Message("test", message));
                }

                @Override
                public void handleFrame(StompHeaders headers, Object payload) {
                     Assert.isTrue(receivedMessages.offer((Message) payload), "Should be added");
                }
            }).get(5, SECONDS);

        var response = receivedMessages.poll(30, SECONDS);
        Assert.notNull(response, "No response retrieved");
        Assert.isTrue(message.equals(response.getMessage()), message + " vs " + response.getMessage());
    }
}
