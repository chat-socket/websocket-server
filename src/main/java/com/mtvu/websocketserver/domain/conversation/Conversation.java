package com.mtvu.websocketserver.domain.conversation;

import com.mtvu.websocketserver.domain.GenericMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Conversation extends GenericMessage {
    private List<String> participants;
}
