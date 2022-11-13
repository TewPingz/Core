package me.tewpingz.core.chat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatManager {

    private long chatSlow = 0;
    private boolean chatEnabled = true;

}
