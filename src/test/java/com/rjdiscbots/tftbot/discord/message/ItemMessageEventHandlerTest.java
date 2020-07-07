package com.rjdiscbots.tftbot.discord.message;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

import com.rjdiscbots.tftbot.db.items.ItemsRepository;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;

public class ItemMessageEventHandlerTest {

    private ItemsRepository itemsRepository;

    private ItemMessageEventHandler itemMessageEventHandler;

    @Before
    public void setup() {
        itemsRepository = mock(ItemsRepository.class);

        itemMessageEventHandler = new ItemMessageEventHandler(itemsRepository);
    }

    @AfterEach
    public void destroy() {
        reset(itemsRepository);
    }
}
