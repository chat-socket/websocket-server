package com.mtvu.websocketserver.domain.message;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Attachment {
    private int id;
    private String type;
    private String name;
    private long size;
    private String url;
    private String thumbnail;
}
