package ru.yaromudr.wschat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.MessageMappingEndpoint;
import org.springframework.boot.actuate.endpoint.WebSocketEndpoint;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;
import ru.yaromudr.wschat.domain.SessionProfanity;
import ru.yaromudr.wschat.event.ParticipantRepository;
import ru.yaromudr.wschat.event.PresenceEventListener;
import ru.yaromudr.wschat.util.ProfanityChecker;

@Configuration
@EnableConfigurationProperties(ChatProperties.class)
public class ChatConfig {

    @Autowired
    private ChatProperties chatProperties;

    @Bean
    @Description("Tracks user presence (join / leave) and broacasts it to all connected users")
    public PresenceEventListener presenceEventListener(SimpMessagingTemplate messagingTemplate) {
        PresenceEventListener presence = new PresenceEventListener(messagingTemplate, participantRepository());
        presence.setLoginDestination(chatProperties.getDestinations().getLogin());
        presence.setLogoutDestination(chatProperties.getDestinations().getLogout());
        return presence;
    }

    @Bean
    @Description("Keeps connected users")
    public ParticipantRepository participantRepository() {
        return new ParticipantRepository();
    }

    @Bean
    @Scope(value = "websocket", proxyMode = ScopedProxyMode.TARGET_CLASS)
    @Description("Keeps track of the level of profanity of a websocket session")
    public SessionProfanity sessionProfanity() {
        return new SessionProfanity(chatProperties.getMaxProfanityLevel());
    }

    @Bean
    @Description("Utility class to check the number of profanities and filter them")
    public ProfanityChecker profanityFilter() {
        ProfanityChecker checker = new ProfanityChecker();
        checker.setProfanities(chatProperties.getDisallowedWords());
        return checker;
    }

    @Bean
    @Description("Spring Actuator endpoint to expose WebSocket stats")
    public WebSocketEndpoint websocketEndpoint(WebSocketMessageBrokerStats stats) {
        return new WebSocketEndpoint(stats);
    }

    @Bean
    @Description("Spring Actuator endpoint to expose WebSocket message mappings")
    public MessageMappingEndpoint messageMappingEndpoint() {
        return new MessageMappingEndpoint();
    }
}
