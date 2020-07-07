package com.rjdiscbots.tftbot.discord.message;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.rjdiscbots.tftbot.db.galaxies.GalaxiesRepository;
import com.rjdiscbots.tftbot.db.galaxies.GalaxyEntity;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;

public class GalaxyMessageEventHandlerTest {

    public GalaxyMessageEventHandler galaxyMessageEventHandler;
    private GalaxiesRepository galaxiesRepository;

    @Before
    public void setup() {
        galaxiesRepository = mock(GalaxiesRepository.class);
        galaxyMessageEventHandler = new GalaxyMessageEventHandler(galaxiesRepository);
    }

    @AfterEach
    public void destroy() {
        reset(galaxiesRepository);
    }

    @Test
    public void whenMessageIsValidGalaxy_then_returnDescription() {
        List<GalaxyEntity> galaxiesEntities = new ArrayList<>();
        galaxiesEntities.add(new GalaxyEntity("key", "Binary Star", "binary star description"));
        when(galaxiesRepository.findByName("binary star")).thenReturn(galaxiesEntities);

        String galaxyDesc = galaxyMessageEventHandler.handleGalaxyMessage("binary star");

        verify(galaxiesRepository, times(1)).findByName("binary star");
        assertEquals("binary star description", galaxyDesc);
    }

    @Test
    public void whenMessageIsInvalidGalaxy_then_returnNoGalaxyFound() {
        List<GalaxyEntity> galaxiesEntities = new ArrayList<>();
        when(galaxiesRepository.findByName("binary star")).thenReturn(galaxiesEntities);

        String galaxyDesc = galaxyMessageEventHandler.handleGalaxyMessage("binary star");

        verify(galaxiesRepository, times(1)).findByName("binary star");
        assertEquals("No such galaxy exists!", galaxyDesc);
    }
}
